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

        }
    }
}
