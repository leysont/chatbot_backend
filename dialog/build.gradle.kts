import io.ktor.plugin.features.*

val logbackVersion: String by project

plugins {
    id("io.ktor.plugin") version "2.3.12"
    id("org.jetbrains.kotlin.plugin.serialization") version "2.0.20"
    kotlin("jvm")
    application
}

group = "team.trashcan"
version = "0.0.1"

application {
    mainClass.set("ApplicationKt")

    val isDevelopment: Boolean = project.ext.has("development")
    applicationDefaultJvmArgs = listOf("-Dio.ktor.development=$isDevelopment")
}

ktor {
    docker {
        jreVersion.set(JavaVersion.VERSION_17)
        localImageName.set("adira_core.dialog")
        portMappings.set(
            listOf(
                DockerPortMapping(
                    outsideDocker = 8081,
                    insideDocker = 8081,
                    protocol = DockerPortMappingProtocol.TCP
                )
            )
        )
    }
}

repositories {
    mavenCentral()
}

dependencies {
    // Ktor server
    implementation("io.ktor:ktor-server-core-jvm")
    implementation("io.ktor:ktor-server-content-negotiation-jvm")
    implementation("io.ktor:ktor-server-openapi")
    implementation("io.ktor:ktor-server-netty-jvm")
    implementation("io.ktor:ktor-server-webjars-jvm")

    // Ktor client
    implementation("io.ktor:ktor-client-core")
    implementation("io.ktor:ktor-client-cio")

    // Generic stuff
    implementation("io.ktor:ktor-serialization-kotlinx-json-jvm")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json")
    implementation("ch.qos.logback:logback-classic:$logbackVersion")
    implementation("io.github.smiley4:ktor-swagger-ui:2.9.0")

    // Project-specific
    implementation("com.aallam.openai:openai-client:3.8.2")
    // implementation(project(":storage"))

    testImplementation(kotlin("test"))
}

tasks.test {
    useJUnitPlatform()
}
kotlin {
    jvmToolchain(11)
}