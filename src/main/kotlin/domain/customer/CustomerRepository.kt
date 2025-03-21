package com.example.domain.customer

import java.util.concurrent.atomic.AtomicInteger

interface CustomerRepository {
    fun findById(id: CustomerId): Customer?
    fun save(customer: Customer)

    // Additional methods like delete, update, list, etc.
}

class InMemoryCustomerRepository : CustomerRepository {
    private val customers = mutableMapOf<CustomerId, Customer>()
    private val sequence = AtomicInteger(0)

    override fun findById(id: CustomerId): Customer? = customers[id]

    override fun save(customer: Customer) {
        val id = customer.id ?: CustomerId(sequence.incrementAndGet().toLong())
        customers[id] = customer.copy(id = id)
    }
}