package com.example.domain.customer

import io.exoquery.capture
import kotlinx.serialization.Contextual
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import serialization.LocalDateTimeSerializer
import java.time.LocalDateTime




internal object DAO {

  data class CustomerWithData(val customer: CustomerRow, val note: NoteRow, val contact: ContactRow)

  fun findById(id: CustomerId) = capture.select {
    val c = from(Table<CustomerRow>())
    val n = join(Table<NoteRow>()) { n -> n.customerId == c.id }
    val cr = join(Table<ContactRow>()) { co -> co.customerId == c.id }
    where { c.id == paramCtx(id) }
    CustomerWithData(c, n, cr)
  }.buildPrettyFor.Postgres()

  fun insertCustomer(c: CustomerRow) = capture {
    insert<CustomerRow> { setParams(c).excluding(id) }.returning { it.id }
  }.buildPrettyFor.Postgres()

  fun upsertCustomer(c: CustomerRow) = capture {
      /*
      e: file:///home/alexi/git/ktor-ddd-example/src/main/kotlin/domain/customer/DAO.kt:32:40 io.exoquery.ParseError: [ExoQuery] Could not understand an expression or query due to an error: Could not parse the expression inside of the action.
      Not a useful error when they don't specify fields `setParams(c).onConflictUpdate` need to have an error saying they need to be specified
       */
    insert<CustomerRow> {
      setParams(c).onConflictUpdate(id) { excluded ->
        set(name to excluded.name)
      }
    }.returning { it.id }
  }.buildPrettyFor.Postgres()

  fun upsertNote(n: NoteRow) = capture {
    insert<NoteRow> {
      setParams(n).onConflictUpdate(id, customerId) { excluded ->
        set(content to excluded.content)
      }
    }
  }.buildPrettyFor.Postgres()

    fun insertNote(n: NoteRow) = capture {
        insert<NoteRow> { setParams(n).excluding(id) }.returning { it.id }
    }.buildPrettyFor.Postgres()

  fun upsertContact(c: ContactRow) = capture {
    insert<ContactRow> {
      setParams(c).onConflictUpdate(id, customerId) { excluded ->
        set(name to excluded.name, email to excluded.email, phone to excluded.phone)
      }
    }
  }.buildPrettyFor.Postgres()

    fun insertContact(c: ContactRow) = capture {
        insert<ContactRow> { setParams(c).excluding(id) }.returning { it.id }
    }.buildPrettyFor.Postgres()

    // NOT USEFUL: If this is a custom type defined on a data-class e.g. `data class Customer(lastTransacted: MyCustomDate)` make sure to either:
    // we need to know that it's CustomerId that can't be parsed!
    @SerialName("Customer")
    @Serializable
    data class CustomerRow(
        @Contextual // This should work, need to add a test for it! Also it should just work for a value class!
        val id: CustomerId,
        val name: String
    ) {
        companion object {
            fun from(customer: Customer): CustomerRow {
                return CustomerRow(
                    id = customer.id ?: CustomerId(0),
                    name = customer.name
                )
            }
        }
    }


  @SerialName("Note")
  @Serializable
  data class NoteRow(
    val id: NoteId,
    @Contextual
    val customerId: CustomerId,
    val content: String,
    @Serializable(with = LocalDateTimeSerializer::class)
    val createdAt: LocalDateTime
  ) {
    companion object {
      fun from(note: Note, customerId: CustomerId): NoteRow {
        return NoteRow(
          id = note.id ?: NoteId("0"),
          customerId = customerId,
          content = note.content,
          createdAt = note.createdAt
        )
      }
    }
    fun toNote() = Note(id, content, createdAt)
  }

  @SerialName("Contact")
  @Serializable
  data class ContactRow(
    val id: ContactId,
    @Contextual
    val customerId: CustomerId,
    val name: String,
    val email: String,
    val phone: String
  ) {
    companion object {
      fun from(contact: Contact, customerId: CustomerId): ContactRow {
        return ContactRow(
          id = contact.id ?: ContactId("0"),
          customerId = customerId,
          name = contact.name,
          email = contact.email.address,
          phone = contact.phone
        )
      }
    }
    fun toContact() = Contact(id, name, Email(email), phone)
  }
}
