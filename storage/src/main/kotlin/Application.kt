import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.response.respondText
import io.ktor.server.routing.*
import plugins.configureDatabase
import plugins.configure
import java.io.File

const val port = 8081

fun main() {
    embeddedServer(Netty, port = port, host = "0.0.0.0", module = Application::module)
        .start(wait = true)
    val filePath = "data.json" // Pfad zu deiner JSON-Datei
    val file = File(filePath)
    val jsonString = file.readText()
}

fun Application.module() {
    configure(port)

    configureDatabase()

    // Configure API endpoints
    routing {
        get("/") {
            call.respondText("hello yes this is storage")
        }
        configureApiEndpoints()
    }

}
