package com.example.domain.customer

import com.example.events.ContactAddedEvent
import com.example.events.NoteAddedEvent
import events.EventPublisher

class CustomerService(
    private val customerRepository: CustomerRepository,
    private val eventPublisher: EventPublisher
) {

    fun createCustomer(name: String): Customer {
        val customer = Customer(name = name)
        customerRepository.save(customer)
        return customer
    }

    fun getCustomer(id: Long): Customer? {
        return customerRepository.findById(CustomerId(id))
    }

    fun addContact(customerId: CustomerId, contact: Contact): Customer? {
        val customer = customerRepository.findById(customerId)
            ?: return null

        // Business logic to add a contact (could be a method on Customer entity)
        val updatedCustomer = customer.withContact(contact)
        customerRepository.save(updatedCustomer)

        // Publish a domain event to signal that a new contact has been added
        eventPublisher.publish(ContactAddedEvent(customerId, contact))

        return updatedCustomer
    }


    fun addNote(customerId: CustomerId, note: Note): Customer? {
        val customer = customerRepository.findById(customerId)
            ?: return null

        val updatedCustomer = customer.withNote(note)
        customerRepository.save(updatedCustomer)

        // Publish a domain event to signal about a new note
        eventPublisher.publish(NoteAddedEvent(customerId, note))

        return updatedCustomer
    }
}