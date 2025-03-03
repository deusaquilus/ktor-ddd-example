package com.example.domain.customer

import java.time.LocalDateTime
import java.util.UUID

// A simple Value Object for unique identifiers
data class CustomerId(val value: String)
data class ContactId(val value: String)
data class NoteId(val value: String)

data class Contact(
    val id: ContactId = ContactId(UUID.randomUUID().toString()),
    val name: String,
    val email: String,  // could be 'Email' value object
    val phone: String
)

data class Note(
    val id: NoteId = NoteId(UUID.randomUUID().toString()),
    val content: String,
    val createdAt: LocalDateTime = LocalDateTime.now()
)

data class Customer(
    val id: CustomerId = CustomerId(UUID.randomUUID().toString()),
    val name: String,
    val email: String,
    val contacts: List<Contact> = emptyList(),
    val notes: List<Note> = emptyList()
) {
    fun addContact(contact: Contact): Customer {
        return copy(contacts = contacts + contact)
    }

    fun addNote(note: Note): Customer {
        return copy(notes = notes + note)
    }
}