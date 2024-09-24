package team.team_trashcan

import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.routing.*
import team.team_trashcan.plugins.configure

const val port = 55555

fun main() {
    embeddedServer(Netty, port = port, host = "0.0.0.0", module = Application::module)
        .start(wait = true)
}

fun Application.module() {
    configure(port)

    routing {
        configureEndpoints()
    }
}
