package com.example

import com.example.domain.customer.Contact
import com.example.domain.customer.Customer
import com.example.domain.customer.Note
import domain.reminder.Reminder
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.server.testing.*
import kotlinx.serialization.json.*
import java.time.LocalDateTime
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class ApplicationTest {

    @Test
    fun testCustomerEndpoints() = testApplication {
        application {
            module()
        }

        // Create customer
        val response = client.post("/customers") {
            contentType(ContentType.Application.Json)
            setBody("""
                {
                    "name": "John Doe",
                    "email": "john@example.com"
                }
            """.trimIndent())
        }
        assertEquals(HttpStatusCode.Created, response.status)
        val responseBody = response.bodyAsText()
        val customerId = Json.parseToJsonElement(responseBody).jsonObject["id"]?.jsonObject?.get("value")?.jsonPrimitive?.content
        assertNotNull(customerId)

        // Get customer
        client.get("/customers/$customerId").apply {
            assertEquals(HttpStatusCode.OK, status)
        }

        // Add contact
        client.post("/customers/$customerId/contacts") {
            contentType(ContentType.Application.Json)
            setBody("""
                {
                    "name": "Jane Doe",
                    "email": "jane@example.com",
                    "phone": "123-456-7890"
                }
            """.trimIndent())
        }.apply {
            assertEquals(HttpStatusCode.OK, status)
        }

        // Add note
        client.post("/customers/$customerId/notes") {
            contentType(ContentType.Application.Json)
            setBody("""
                {
                    "content": "Test note"
                }
            """.trimIndent())
        }.apply {
            assertEquals(HttpStatusCode.OK, status)
        }
    }

    @Test
    fun testReminderEndpoints() = testApplication {
        application {
            module()
        }

        // Create customer first
        val customerResponse = client.post("/customers") {
            contentType(ContentType.Application.Json)
            setBody("""
                {
                    "name": "John Doe",
                    "email": "john@example.com"
                }
            """.trimIndent())
        }
        val customerId = Json.parseToJsonElement(customerResponse.bodyAsText()).jsonObject["id"]?.jsonObject?.get("value")?.jsonPrimitive?.content
        assertNotNull(customerId)

        // Create reminder
        val reminderResponse = client.post("/reminders") {
            contentType(ContentType.Application.Json)
            setBody("""
                {
                    "customerId": {"value": "$customerId"},
                    "noteId": null,
                    "remindAt": "2024-01-01T10:00:00",
                    "message": "Test reminder"
                }
            """.trimIndent())
        }
        assertEquals(HttpStatusCode.Created, reminderResponse.status)
        val reminderId = Json.parseToJsonElement(reminderResponse.bodyAsText()).jsonObject["id"]?.jsonObject?.get("value")?.jsonPrimitive?.content
        assertNotNull(reminderId)

        // Get reminder
        client.get("/reminders/$reminderId").apply {
            assertEquals(HttpStatusCode.OK, status)
        }

        // Get reminders for customer
        client.get("/reminders/customer/$customerId").apply {
            assertEquals(HttpStatusCode.OK, status)
        }
    }
}
