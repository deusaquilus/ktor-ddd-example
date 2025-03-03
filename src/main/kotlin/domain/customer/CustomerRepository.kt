package com.example.domain.customer

interface CustomerRepository {
    fun findById(id: CustomerId): Customer?
    fun save(customer: Customer)

    // Additional methods like delete, update, list, etc.
}

class InMemoryCustomerRepository : CustomerRepository {
    private val customers = mutableMapOf<CustomerId, Customer>()

    override fun findById(id: CustomerId): Customer? = customers[id]

    override fun save(customer: Customer) {
        customers[customer.id] = customer
    }
}