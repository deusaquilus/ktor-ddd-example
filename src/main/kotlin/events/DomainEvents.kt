package com.example.events

import com.example.domain.customer.Contact
import com.example.domain.customer.CustomerId
import com.example.domain.customer.Note

sealed interface DomainEvent

data class ContactAddedEvent(
    val customerId: CustomerId,
    val contact: Contact
): DomainEvent

data class NoteAddedEvent(
    val customerId: CustomerId,
    val note: Note
): DomainEvent
