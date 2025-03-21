package com.example.domain.reminder

import com.example.domain.customer.CustomerId
import domain.reminder.InMemoryReminderRepository
import domain.reminder.Reminder
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.Application
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.response.respondText
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import io.ktor.server.routing.route
import io.ktor.server.routing.routing

fun Application.reminderRoutes() {
    routing {
        val repository = InMemoryReminderRepository()
        val service = ReminderService(repository)

        route("/reminders") {
            // Create a reminder
            post {
                val reminder = call.receive<Reminder>()
                val createdReminder = service.createReminder(
                    customerId = reminder.customerId,
                    noteId = reminder.noteId?.value,
                    remindAt = reminder.remindAt,
                    message = reminder.message
                )
                call.respond(HttpStatusCode.Created, createdReminder)
            }

            // Get reminder by ID
            get("/{id}") {
                val id = call.parameters["id"]?.toLongOrNull() ?: return@get call.respondText(
                    "Missing or malformed id",
                    status = HttpStatusCode.BadRequest
                )

                val reminder = service.getReminder(id)
                if (reminder != null) {
                    call.respond(reminder)
                } else {
                    call.respondText("Reminder not found", status = HttpStatusCode.NotFound)
                }
            }

            // Get reminders for customer
            get("/customer/{customerId}") {
                val customerId = call.parameters["customerId"]?.toLongOrNull() ?: return@get call.respondText(
                    "Missing or malformed customerId",
                    status = HttpStatusCode.BadRequest
                )

                val reminders = service.getRemindersForCustomer(CustomerId(customerId))
                call.respond(reminders)
            }
        }
    }
}
