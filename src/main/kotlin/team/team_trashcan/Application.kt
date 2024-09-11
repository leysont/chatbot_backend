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
    val filePath = "data.json" // Pfad zu deiner JSON-Datei
    val file = File(filePath)
    val jsonString = file.readText()
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
