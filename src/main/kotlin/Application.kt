package com.example

import com.example.domain.customer.JdbcCustomerRepository
import com.example.domain.customer.customerRoutes
import com.example.domain.reminder.reminderRoutes
import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import domain.reminder.InMemoryReminderRepository
import io.exoquery.controller.jdbc.JdbcControllers
import io.exoquery.controller.runActions
import io.ktor.serialization.kotlinx.json.json
import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.config.ApplicationConfig
import io.ktor.server.plugins.contentnegotiation.ContentNegotiation
import io.ktor.server.routing.routing
import io.zonky.test.db.postgres.embedded.EmbeddedPostgres
import kotlinx.coroutines.runBlocking
import java.io.File

fun Application.module() {
    val dbConfig = environment.config
    install(ContentNegotiation) {
        json()
    }

    // To use the embedded postgres database use this:
    //val ctx = runBlocking { setupEmbeddedDB() }

    // To use the docker postgres database use this:
    val ctx = runBlocking { setupDockerDB(environment.config) }

    val customerRepository = JdbcCustomerRepository(ctx)
    val reminderRepository = InMemoryReminderRepository() // InMemoryCustomerRepository()

    routing {
        // Register routes from the domain subpackages
        customerRoutes(customerRepository)
        reminderRoutes(reminderRepository)
    }
}

suspend fun setupEmbeddedDB(): JdbcControllers.Postgres {
    val ds = EmbeddedPostgres.start().postgresDatabase
    println("Postgres started on ${ds.connection.metaData.url}")

    val controller = JdbcControllers.Postgres(ds)
    controller.runActions(File("src/main/sql/schema.sql").readText())
    return controller
}

suspend fun setupDockerDB(dbConfig: ApplicationConfig): JdbcControllers.Postgres {
    val config = DatabaseConfig.readFrom(dbConfig)

    val ds = HikariDataSource(HikariConfig().apply {
        jdbcUrl = config.dbUrl
        username = config.dbUser
        password = config.dbPassword
        driverClassName = config.dbDriver
        maximumPoolSize = config.dbMaxPoolSize
    })

    val controller = JdbcControllers.Postgres(ds)
    controller.runActions(File("src/main/sql/schema.sql").readText())
    return controller
}
