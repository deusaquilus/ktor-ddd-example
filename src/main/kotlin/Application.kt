package com.example

import com.example.domain.customer.customerRoutes
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.routing.*


fun main() {
    embeddedServer(Netty, port = 8080, module = Application::module)
        .start(wait = true)
}

fun Application.module() {
    configureSerialization()

    routing {
        // Register routes from the domain subpackages
        customerRoutes()

    }
}