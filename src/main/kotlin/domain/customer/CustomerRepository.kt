package com.example.domain.customer

import io.exoquery.controller.jdbc.JdbcBasicEncoding
import io.exoquery.controller.jdbc.JdbcController
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
        val id = customer.id ?: CustomerId(sequence.incrementAndGet())
        val customerWithId =
            // Let's at least pretend that we're talking to a DB that is actually writing in the IDs!
            customer.copy(
                id = id,
                notes = customer.notes.map { it.copy(id = it.id ?: NoteId(notesSequence.incrementAndGet())) },
                contacts = customer.contacts.map { it.copy(id = it.id ?: ContactId(contactsSequence.incrementAndGet())) }
            )
        customers[id] = customerWithId
        return customerWithId
    }
}

class JdbcCustomerRepository(private val controller: JdbcController) : CustomerRepository {
    override suspend fun findById(id: CustomerId): Customer? {
        return DAO.findById(id).runOn(controller).regroup()
    }

    private fun List<DAO.CustomerWithData>.regroup() =
        this.groupBy { (customer, _, _) -> customer }
            .map { (customer, rows) ->
                Customer(
                    id = customer.id,
                    name = customer.name,
                    notes = rows.mapNotNull { it.note }.map { Note(it.id, it.content, it.createdAt) },
                    contacts = rows.mapNotNull { it.contact }.map { Contact(it.id, it.name, Email(it.email), it.phone) }
                )
            }.firstOrNull()

    override suspend fun save(customer: Customer): Customer {
        val customerRow = DAO.CustomerRow.from(customer)
        fun Customer.newNotes(id: CustomerId) = notes.map { DAO.NoteRow.from(it, id) }
        fun Customer.newContacts(id: CustomerId) = customer.contacts.map { DAO.ContactRow.from(it, id) }

        val out = controller.transaction {
            val customerId =
                if (customer.id == null || customer.id.value == 0) {
                    DAO.insertCustomer(customerRow).runOnTransaction()
                } else {
                    DAO.upsertCustomer(customerRow).runOnTransaction()
                }

            customer.newNotes(customerId).forEach { note ->
                if (note.id.value == 0) {
                    DAO.insertNote(note).runOnTransaction()
                } else {
                    DAO.upsertNote(note).runOnTransaction()
                }
            }
            customer.newContacts(customerId).forEach { contact ->
                if (contact.id.value == 0) {
                    DAO.insertContact(contact).runOnTransaction()
                } else {
                    DAO.upsertContact(contact).runOnTransaction()
                }
            }
            // No copy-id tricks, read the data back from the DB the way it's actually written!
            val out = DAO.findById(customerId).runOnTransaction()
            out.regroup()
        }
        return out ?: error("Customer that we just wrote was not found: $customer")
    }
}
