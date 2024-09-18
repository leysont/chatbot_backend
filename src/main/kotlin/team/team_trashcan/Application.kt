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
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction


fun main() {
    embeddedServer(Netty, port = 8080, host = "0.0.0.0", module = Application::module)
        .start(wait = true)

    Database.connect("jdbc:mariadb://0.0.0.0:58304/",
        driver = "org.mariadb.jdbc.Driver",
        user = "root",
        password = "chatbot")

    // Create the table
    transaction {
        SchemaUtils.create(Customer())
    }
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
