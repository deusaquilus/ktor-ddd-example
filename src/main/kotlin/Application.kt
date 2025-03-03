package com.example

import com.example.domain.customer.customerRoutes
import com.example.domain.reminder.reminderRoutes
import io.ktor.serialization.kotlinx.json.json
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.plugins.contentnegotiation.ContentNegotiation
import io.ktor.server.routing.*


fun main() {
    embeddedServer(Netty, port = 8080, module = Application::module)
        .start(wait = true)
}

fun Application.module() {
    install(ContentNegotiation) {
        json()
    }

    routing {
        // Register routes from the domain subpackages
        customerRoutes()
        reminderRoutes()
    }
}
