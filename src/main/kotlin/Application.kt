package com.example

import com.example.domain.customer.ContactId
import com.example.domain.customer.CustomerId
import com.example.domain.customer.InMemoryCustomerRepository
import com.example.domain.customer.JdbcCustomerRepository
import com.example.domain.customer.NoteId
import com.example.domain.customer.customerRoutes
import com.example.domain.reminder.reminderRoutes
import domain.reminder.InMemoryReminderRepository
import io.exoquery.controller.jdbc.JdbcBasicEncoding
import io.exoquery.controller.jdbc.JdbcControllers
import io.exoquery.controller.jdbc.JdbcEncodingConfig
import io.exoquery.controller.runActions
import io.ktor.serialization.kotlinx.json.json
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.plugins.contentnegotiation.ContentNegotiation
import io.ktor.server.routing.*
import io.zonky.test.db.postgres.embedded.EmbeddedPostgres
import kotlinx.coroutines.runBlocking
import java.io.File


fun main() {
    embeddedServer(Netty, port = 8080, module = Application::module, host = "localhost")
        .start(wait = true)
}

fun Application.module() {
    install(ContentNegotiation) {
        json()
    }

    //val customerRepository = InMemoryCustomerRepository()

    fun encodingConfig() =
        JdbcEncodingConfig.Default(
            additionalEncoders =
                JdbcEncodingConfig.Default.additionalEncoders +
                        JdbcBasicEncoding.IntEncoder.contramap { id: CustomerId -> id.value } +
                        JdbcBasicEncoding.IntEncoder.contramap { id: NoteId -> id.value } +
                        JdbcBasicEncoding.IntEncoder.contramap { id: ContactId -> id.value },
            additionalDecoders =
                JdbcEncodingConfig.Default.additionalDecoders +
                        JdbcBasicEncoding.IntDecoder.map { CustomerId(it) } +
                        JdbcBasicEncoding.IntDecoder.map { NoteId(it) } +
                        JdbcBasicEncoding.IntDecoder.map { ContactId(it) },
            debugMode = true
        )

    val ds = EmbeddedPostgres.start().postgresDatabase
    println("Postgres started on ${ds.connection.metaData.url}")
    val controller = JdbcControllers.Postgres(ds, encodingConfig())
    runBlocking {
        controller.runActions(File("src/main/sql/schema.sql").readText())
    }
    val customerRepository = JdbcCustomerRepository(controller)
    val reminderRepository = InMemoryReminderRepository()

    routing {
        // Register routes from the domain subpackages
        customerRoutes(customerRepository)
        reminderRoutes(reminderRepository)
    }
}
