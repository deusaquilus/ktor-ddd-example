package com.example.domain.customer

import io.exoquery.controller.jdbc.JdbcBasicEncoding
import io.exoquery.controller.jdbc.JdbcControllers
import io.exoquery.controller.jdbc.JdbcEncodingConfig
import io.exoquery.controller.transaction
import io.exoquery.jdbc.runOn
import java.util.concurrent.atomic.AtomicInteger
import javax.sql.DataSource
import kotlin.collections.component1

interface CustomerRepository {
    suspend fun findById(id: CustomerId): Customer?
    suspend fun save(customer: Customer): Customer

    // Additional methods like delete, update, list, etc.
}

class InMemoryCustomerRepository : CustomerRepository {
    private val customers = mutableMapOf<CustomerId, Customer>()
    private val sequence = AtomicInteger(0)
    private val notesSequence = AtomicInteger(0)
    private val contactsSequence = AtomicInteger(0)

    suspend override fun findById(id: CustomerId): Customer? = customers[id]

    suspend override fun save(customer: Customer): Customer {
        val id = customer.id ?: CustomerId(sequence.incrementAndGet().toLong())
        val customerWithId =
            // Let's at least pretend that we're talking to a DB that is actually writing in the IDs!
            customer.copy(
                id = id,
                notes = customer.notes.map { it.copy(id = it.id ?: NoteId(notesSequence.incrementAndGet().toString())) },
                contacts = customer.contacts.map { it.copy(id = it.id ?: ContactId(contactsSequence.incrementAndGet().toString())) }
            )
        customers[id] = customerWithId
        return customerWithId
    }
}

class JdbcCustomerRepository(ds: DataSource) : CustomerRepository {
    fun encodingConfig() =
        JdbcEncodingConfig.Default(
            additionalEncoders =
                JdbcEncodingConfig.Default.additionalEncoders +
                        JdbcBasicEncoding.LongEncoder.contramap { id: CustomerId -> id.value } +
                        JdbcBasicEncoding.StringEncoder.contramap { id: NoteId -> id.value } +
                        JdbcBasicEncoding.StringEncoder.contramap { id: ContactId -> id.value },
            additionalDecoders =
                JdbcEncodingConfig.Default.additionalDecoders +
                        JdbcBasicEncoding.LongDecoder.map { CustomerId(it) } +
                        JdbcBasicEncoding.StringDecoder.map { NoteId(it) } +
                        JdbcBasicEncoding.StringDecoder.map { ContactId(it) }
        )

    private val controller =
        JdbcControllers.Postgres(ds, encodingConfig())

    override suspend fun findById(id: CustomerId): Customer? {
        return DAO.findById(id).runOn(controller).regroup()
    }

    private fun List<DAO.CustomerWithData>.regroup() =
        this.groupBy { (customer, _, _) -> customer }
            .map { (customer, rows) ->
                Customer(
                    id = customer.id,
                    name = customer.name,
                    notes = rows.map { it.note }.map { Note(it.id, it.content, it.createdAt) },
                    contacts = rows.map { it.contact }.map { Contact(it.id, it.name, Email(it.email), it.phone) }
                )
            }.firstOrNull()

    override suspend fun save(customer: Customer): Customer {
        val customerRow = DAO.CustomerRow.from(customer)
        fun Customer.newNotes(id: CustomerId) = notes.map { DAO.NoteRow.from(it, id) }
        fun Customer.newContacts(id: CustomerId) = customer.contacts.map { DAO.ContactRow.from(it, id) }

        return controller.transaction {
            val customerId =
                if (customer.id == null) {
                    DAO.insertCustomer(customerRow).runOnTransaction()
                } else {
                    DAO.upsertCustomer(customerRow).runOnTransaction()
                }

            customer.newNotes(customerId).forEach { note ->
                if (note.id == null) {
                    DAO.insertNote(note).runOnTransaction()
                } else {
                    DAO.upsertNote(note).runOnTransaction()
                }
            }
            customer.newContacts(customerId).forEach { contact ->
                if (contact.id == null) {
                    DAO.insertContact(contact).runOnTransaction()
                } else {
                    DAO.upsertContact(contact).runOnTransaction()
                }
            }
            // No copy-id tricks, read the data back from the DB the way it's actually written!
            DAO.findById(customerId).runOnTransaction().regroup() ?: error("Customer id:$customerId that we just wrote was not found: $customer")
        }
    }
}
