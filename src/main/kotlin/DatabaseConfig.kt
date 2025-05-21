package com.example

import io.ktor.server.config.ApplicationConfig

data class DatabaseConfig(
    val dbUrl: String,
    val dbUser: String,
    val dbPassword: String,
    val dbDriver: String,
    val dbMaxPoolSize: Int = 10
) {

    companion object {
        fun readFrom(config: ApplicationConfig) =
            DatabaseConfig(
                dbUrl = config.property("ktor.database.url").getString(),
                dbUser = config.property("ktor.database.user").getString(),
                dbPassword = config.property("ktor.database.password").getString(),
                dbDriver = config.property("ktor.database.driver").getString(),
                dbMaxPoolSize = config.propertyOrNull("ktor.database.maxPoolSize")?.getString()?.toInt() ?: 10
            )

    }
}
