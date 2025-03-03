package com.example.domain.customer

import events.EventPublisherImpl
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.Application
import io.ktor.server.request.receive
import io.ktor.server.response.respondText
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import io.ktor.server.routing.route
import io.ktor.server.response.respond
import io.ktor.server.routing.routing


fun Application.customerRoutes() {
    routing {
        val repository = InMemoryCustomerRepository()
        val eventPublisher = EventPublisherImpl()

        //TODO: use the service in the routes below
        val service = CustomerService(repository, eventPublisher)

        route("/customers") {
            // Create customer
            post {
                val customer = call.receive<Customer>()
                val createdCustomer = service.createCustomer(customer.name, customer.email)
                call.respond(HttpStatusCode.Created, createdCustomer)
            }

            // Get customer by ID
            get("/{id}") {
                val id = call.parameters["id"] ?: return@get call.respondText(
                    "Missing or malformed id",
                    status = HttpStatusCode.BadRequest
                )

                val customer = service.getCustomer(id)
                if (customer != null) {
                    call.respond(customer)
                } else {
                    call.respondText("Customer not found", status = HttpStatusCode.NotFound)
                }
            }

            // Add contact to customer
            post("/{id}/contacts") {
                val id = call.parameters["id"] ?: return@post call.respondText(
                    "Missing or malformed id",
                    status = HttpStatusCode.BadRequest
                )

                val contact = call.receive<Contact>()
                val updatedCustomer = service.addContact(CustomerId(id), contact)

                if (updatedCustomer != null) {
                    call.respond(updatedCustomer)
                } else {
                    call.respondText("Customer not found", status = HttpStatusCode.NotFound)
                }
            }

            // Add note to customer
            post("/{id}/notes") {
                val id = call.parameters["id"] ?: return@post call.respondText(
                    "Missing or malformed id",
                    status = HttpStatusCode.BadRequest
                )

                val note = call.receive<Note>()
                val updatedCustomer = service.addNote(CustomerId(id), note)

                if (updatedCustomer != null) {
                    call.respond(updatedCustomer)
                } else {
                    call.respondText("Customer not found", status = HttpStatusCode.NotFound)
                }
            }
        }
    }
}
