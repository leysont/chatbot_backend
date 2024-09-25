import io.ktor.server.application.call
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.Routing
import io.ktor.server.routing.post

fun Routing.configureEndpoints() {

    // send message. params: message, channel ID
    post("/message") {
        val message = call.receive<String>()

        val prompt = toPrompt(message)

        val response = sendPrompt(prompt)

        call.respond(response)
    }

    // start chat params: user ID, channel ID (issue ID)
    post("/start") { }

    // end the chat: channel ID
    post("/cancel") { }

}


fun toPrompt(message: String): String {
    TODO("Not yet implemented")
}

fun sendPrompt(prompt: String): String {
    TODO("Not yet implemented")
}