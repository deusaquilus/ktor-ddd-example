package com.example.domain.customer

import com.example.events.ContactAddedEvent
import com.example.events.NoteAddedEvent
import events.EventPublisher

class CustomerService(
    private val customerRepository: CustomerRepository,
    private val eventPublisher: EventPublisher
) {

    suspend fun createCustomer(name: String): Customer {
        val customer = Customer(name = name)
        return customerRepository.save(customer)
    }

    suspend fun getCustomer(id: Long): Customer? {
        return customerRepository.findById(CustomerId(id))
    }

    suspend fun addContact(customerId: CustomerId, contact: Contact): Customer? {
        val customer = customerRepository.findById(customerId)
            ?: return null

        // Business logic to add a contact (could be a method on Customer entity)
        val updatedCustomer = customer.withContact(contact)
        customerRepository.save(updatedCustomer)

        // Publish a domain event to signal that a new contact has been added
        eventPublisher.publish(ContactAddedEvent(customerId, contact))

        return updatedCustomer
    }


    suspend fun addNote(customerId: CustomerId, note: Note): Customer? {
        val customer = customerRepository.findById(customerId)
            ?: return null

        val updatedCustomer = customer.withNote(note)
        customerRepository.save(updatedCustomer)

        // Publish a domain event to signal about a new note
        eventPublisher.publish(NoteAddedEvent(customerId, note))

        return updatedCustomer
    }
}
