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
    implementation("io.ktor:ktor-server-core-jvm")
    implementation("io.ktor:ktor-server-content-negotiation-jvm")
    implementation("io.ktor:ktor-server-openapi")
    implementation("io.ktor:ktor-server-netty-jvm")
    implementation("io.ktor:ktor-server-webjars-jvm")
    implementation("io.ktor:ktor-serialization-kotlinx-json-jvm")

    implementation("ch.qos.logback:logback-classic:$logbackVersion")


    implementation("io.github.smiley4:ktor-swagger-ui:2.9.0")

    testImplementation(kotlin("test"))
//    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.7.1")
}

tasks.test {
    useJUnitPlatform()
}
kotlin {
    jvmToolchain(11)
}