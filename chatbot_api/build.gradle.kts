val kotlin_version: String by project
val logback_version: String by project
val exposed_version: String by project

plugins {
    id("io.ktor.plugin") version "2.3.12"
    id("org.jetbrains.kotlin.plugin.serialization") version "2.0.20"
    kotlin("jvm")
    application
}

group = "team.team_trashcan"
version = "0.0.1"

application {
//    mainClass.set("team.team_trashcan.ApplicationKt")
    mainClass.set("ApplicationKt")

    val isDevelopment: Boolean = project.ext.has("development")
    applicationDefaultJvmArgs = listOf("-Dio.ktor.development=$isDevelopment")
}

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(kotlin("test"))

    implementation("io.ktor:ktor-server-core-jvm")
    implementation("io.ktor:ktor-server-content-negotiation-jvm")
    implementation("io.ktor:ktor-server-openapi")
    implementation("io.ktor:ktor-server-netty-jvm")
    implementation("io.ktor:ktor-server-webjars-jvm")
    implementation("io.ktor:ktor-serialization-kotlinx-json-jvm")

    implementation("org.jetbrains.exposed:exposed-core:$exposed_version")
    implementation("org.jetbrains.exposed:exposed-jdbc:$exposed_version")
    implementation("org.jetbrains.exposed:exposed-dao:$exposed_version")

    implementation("org.mariadb.jdbc:mariadb-java-client:3.3.1")
    implementation("org.webjars:jquery:3.2.1") //TODO: probably remove this
    implementation("io.github.smiley4:ktor-swagger-ui:2.9.0")
    implementation("ch.qos.logback:logback-classic:$logback_version")
    implementation("mysql:mysql-connector-java:8.0.26") //TODO: probably remove this

    testImplementation("io.ktor:ktor-server-test-host-jvm")
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit:$kotlin_version")
}

tasks.test {
    useJUnitPlatform()
}
kotlin {
    jvmToolchain(11)
}