package com.example

import com.example.domain.customer.JdbcCustomerRepository
import com.example.domain.customer.customerRoutes
import com.example.domain.reminder.reminderRoutes
import com.example.module
import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import domain.reminder.InMemoryReminderRepository
import io.exoquery.controller.jdbc.JdbcControllers
import io.exoquery.controller.runActions
import io.ktor.server.engine.applicationEnvironment
import io.ktor.serialization.kotlinx.json.json
import io.ktor.server.application.*
import io.ktor.server.config.ApplicationConfig
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.plugins.contentnegotiation.ContentNegotiation
import io.ktor.server.routing.*
import io.zonky.test.db.postgres.embedded.EmbeddedPostgres
import kotlinx.coroutines.runBlocking
import java.io.File


suspend fun main() {
    val appConfig: ApplicationConfig = ApplicationConfig("application.yaml")
    val appPort = appConfig.property("ktor.deployment.port").getString().toInt()
    val env = applicationEnvironment {
        log = org.slf4j.LoggerFactory.getLogger("ktor")
        config = appConfig
    }

    embeddedServer(
        factory = Netty,
        environment = env,
        configure = { connector { port = appPort } },
        //module = Application::module, // Passed in from application.yaml
    ).start(wait = true)
}
