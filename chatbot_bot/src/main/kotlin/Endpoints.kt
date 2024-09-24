package team.team_trashcan

import io.ktor.server.application.call
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.Routing
import io.ktor.server.routing.post

fun Routing.configureEndpoints() {

    // send message. take message, user ID
    post("/message") {
        val message = call.receive<String>()

        val prompt = toPrompt(message)

        val response = sendPrompt(prompt)

        call.respond(response)
    }

    // end the chat
    post("/cancel") { }

}


fun toPrompt(message: String): String {
    TODO("Not yet implemented")
}

fun sendPrompt(prompt: String): String {
    TODO("Not yet implemented")
}