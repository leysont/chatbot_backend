import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.routing.*
import plugins.configureDatabases
import plugins.configureRouting
import plugins.configureSerialization
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
//    configureHTTP()
    configureRouting()

    routing {
        configureEndpoints()
    }

}
