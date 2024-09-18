package team.team_trashcan
import io.github.smiley4.ktorswaggerui.dsl.route
import io.ktor.http.ContentDisposition.Companion.File
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.request.*
import io.ktor.server.routing.*
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import team.team_trashcan.plugins.configureDatabases
import team.team_trashcan.plugins.configureHTTP
import team.team_trashcan.plugins.configureRouting
import team.team_trashcan.plugins.configureSerialization
import java.io.File


fun main() {
    embeddedServer(Netty, port = 8080, host = "0.0.0.0", module = Application::module)
        .start(wait = true)

//    // Kundendaten aus der Json lesen
//    val filePath = "kunde.json" // Pfad zu deiner JSON-Datei
//    val jsonString = File(filePath).readText()
//    val user = Json.decodeFromString<Kunde>(jsonString)
//    println(user)
//
//    // Tickets aus der Json lesen
//    val filePathTickets = "ticket.json" // Pfad zu deiner JSON-Datei
//    val jsonStringTicket = File(filePath).readText()
//    val ticket = Json.decodeFromString<Ticket>(jsonString)
//    println(ticket)
//
//    // Mitarbeiter aus der Json lesen
//    val filePathMitarbeiter = "mitarbeiter.json" // Pfad zu deiner JSON-Datei
//    val jsonStringMitarbeiter = File(filePath).readText()
//    val mitarbeiter = Json.decodeFromString<Ticket>(jsonString)
//    println(mitarbeiter)
}

fun Application.module() {
    configureSerialization()
    configureDatabases()
    configureHTTP()
    configureRouting()

    routing {
        route("api") {
            route("/tickets/") {
                post {
                    // neues Ticket erstellen
                    val text = call.receiveText()

                }
                get {
                    // alle Tickets laden
                }
                delete {
                    // alle Tickets löschen
                }
                route("{id}") {
                    get {
                        // ein Ticket laden
                    }
                    delete {
                        // ein Ticket löschen
                    }
                }

            }
        }
    }
}
