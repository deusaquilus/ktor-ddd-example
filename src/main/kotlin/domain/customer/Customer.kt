package com.example.domain.customer

import kotlinx.serialization.Serializable
import serialization.LocalDateTimeSerializer
import java.time.LocalDateTime

// A simple Value Object for unique identifiers
@JvmInline
@Serializable
value class CustomerId(val value: Long)
@JvmInline
@Serializable
value class ContactId(val value: Long)
@JvmInline
@Serializable
value class NoteId(val value: Long)

@Serializable
data class Contact(
    val id: ContactId,
    val name: String,
    val email: Email,  // could be 'Email' value object
    val phone: String
)

@Serializable
data class Note(
    val id: NoteId,
    val content: String,
    @Serializable(with = LocalDateTimeSerializer::class)
    val createdAt: LocalDateTime = LocalDateTime.now()
)

@Serializable
data class Customer(
    val id: CustomerId? = null,
    val name: String,
    val contacts: List<Contact> = emptyList(),
    val notes: List<Note> = emptyList()
) {
    fun withContact(contact: Contact): Customer {
        return copy(contacts = contacts + contact)
    }

    fun withNote(note: Note): Customer {
        return copy(notes = notes + note)
    }
}

// Example: a more complex value object for
// Email might include validation logic.
@JvmInline
@Serializable
value class Email(val address: String) {
    init {
        require(address.contains("@")) { "Invalid email address" }
    }
}
