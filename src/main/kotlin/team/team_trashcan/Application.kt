package team.team_trashcan
//import io.ktor.application.*
//import io.ktor.features.ContentNegotiation
//import io.ktor.gson.gson
//import io.ktor.response.*
//import io.ktor.request.*
//import io.ktor.routing.*
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import team.team_trashcan.plugins.*


fun main() {
    //DatabaseConfig.connect()


    embeddedServer(Netty, port = 8080, host = "0.0.0.0", module = Application::module)
        .start(wait = true)
}

fun Application.module() {
    configureSerialization()
    configureDatabases()
    configureHTTP()
    configureRouting()
}
