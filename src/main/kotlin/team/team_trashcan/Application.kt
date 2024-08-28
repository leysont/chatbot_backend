package team.team_trashcan

import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import team.team_trashcan.plugins.*

fun main() {
    embeddedServer(Netty, port = 8080, host = "0.0.0.0", module = Application::module)
        .start(wait = true)
}

fun Application.module() {
    configureSerialization()
    configureDatabases()
    configureHTTP()
    configureRouting()
}
