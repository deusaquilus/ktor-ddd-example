val kotlin_version: String by project
val logback_version: String by project

plugins {
    kotlin("jvm") version "2.1.20"
    id("io.ktor.plugin") version "3.1.2"
    id("org.jetbrains.kotlin.plugin.serialization") version "2.1.20"
    id("io.exoquery.exoquery-plugin") version "2.1.20-LD.1.2.4.PL"
}

group = "com.example"
version = "0.0.1"

application {
    mainClass = "io.ktor.server.netty.EngineMain"

    val isDevelopment: Boolean = project.ext.has("development")
    applicationDefaultJvmArgs = listOf("-Dio.ktor.development=$isDevelopment")
}

repositories {
    mavenCentral()
    mavenLocal()
}

dependencies {
    implementation("io.exoquery:exoquery-runner-core:LD.1.2.4.PL-LD.1.2.4")
    implementation("io.exoquery:exoquery-runner-jdbc:LD.1.2.4.PL-LD.1.2.4")

    implementation("io.exoquery:controller-core-jvm") {
        version {
            strictly("LO.3.2.2")
        }
    }
    implementation("io.exoquery:controller-jdbc-jvm") {
        version {
            strictly("LO.3.2.2")
        }
    }

    // Only one of these is needed
    implementation("io.zonky.test:embedded-postgres:2.0.7")
    implementation("com.zaxxer:HikariCP:5.0.1")

    api("org.jetbrains.kotlinx:kotlinx-serialization-core:1.8.0")
    api("org.jetbrains.kotlinx:kotlinx-serialization-json:1.8.0")
    api("org.jetbrains.kotlinx:kotlinx-serialization-protobuf:1.8.0")

    implementation("io.ktor:ktor-server-core")
    implementation("io.ktor:ktor-server-content-negotiation")
    implementation("io.ktor:ktor-serialization-kotlinx-json")
    implementation("io.ktor:ktor-server-netty")
    implementation("io.ktor:ktor-server-call-logging")
    implementation("ch.qos.logback:logback-classic:$logback_version")
    implementation("io.ktor:ktor-server-config-yaml")
    testImplementation("io.ktor:ktor-server-test-host")
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit:$kotlin_version")
}
