import bot.AssistantMessage
import bot.SessionManager
import bot.UserMessage
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.call
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.Routing
import io.ktor.server.routing.post
import kotlinx.serialization.Serializable

@Serializable
data class RequestMessage(
    val sessionId: String,
    val message: String,
)

@Serializable
data class RequestStart(
    val userId: String,
    val sessionId: String,
)

@Serializable
data class RequestEnd(
    val sessionId: String,
)


/**
 * Configures the endpoints for managing chat sessions.
 *
 * Defines routes for handling chat interactions including sending messages,
 * starting new sessions, and ending sessions. The endpoints are defined as follows:
 *
 * - `POST /message`: Accepts a message to be processed within an active session.
 *   Expects a request body of type `RequestMessage` containing `sessionId` and `message`.
 *   Responds with the processed message or a `NotFoundException` if the session is not found.
 *
 * - `POST /start`: Starts a new chat session.
 *   Expects a request body of type `RequestStart` containing `userId` and `sessionId`.
 *   Responds with `HttpStatusCode.Created` on success.
 *
 * - `POST /cancel`: Ends an existing chat session.
 *   Expects a request body of type `RequestEnd` containing `sessionId`.
 *   Responds with `HttpStatusCode.NoContent` on success.
 */
fun Routing.configureEndpoints() {

    /**
     * Receives user message and returns chatbot answer.
     * params: message, sessionId
     * responses:
     *      200 Ok (Success)
     *      404 NotFound (If any ID is not found)
     *      400 BadRequest (If any param is wrong format / missing)
     *      500 InternalServerError (If no response could be generated)
     */
    post("/message") {
        val body = call.receive<RequestMessage>()

        if (body.sessionId.isBlank() || body.message.isBlank()) {
            call.respond(HttpStatusCode.BadRequest)
            return@post
        }

        val session = SessionManager
            // TODO: The session might not even need the userId,
            //  because it seems to be only necessary to be included in the ticket which is sent from here
            .get(body.sessionId)

        if (session == null) {
            call.respond(HttpStatusCode.NotFound)
            return@post
        }

        session.history.add(UserMessage(body.message))

        val response = session.processMessage(body.message)
        when (response?.intent) {

            AssistantMessage.Intent.EndAndSolved -> {
                SessionManager.end(body.sessionId)
                session.history.add(UserMessage(response.content))
                call.respond(HttpStatusCode.OK, response.content)
            }

            AssistantMessage.Intent.EndAndSendTicket -> {
                SessionManager.end(body.sessionId)
                session.history.add(UserMessage(response.content))
                // TODO: send ticket
                call.respond(HttpStatusCode.OK, response.content)
            }

            null -> {
                // No AssistantMessage could be generated. Probably OpenAI API not available. 😨
                call.respond(HttpStatusCode.InternalServerError)
            }

            else -> {
                // Either GatherInfo, Summary or Solution
                session.history.add(UserMessage(response.content))
                call.respond(HttpStatusCode.OK, response.content)
            }
        }
    }

    /**
     * Start a new chat session.
     * params: userId, channelId (becomes issue ID)
     * responses:
     *      201 Created (Success)
     *      404 NotFound (If any ID is not found)
     *      400 BadRequest (If any ID is wrong format / missing)
     */
    post("/start") {
        val body = call.receive<RequestStart>()

        SessionManager
            .create(body.userId, body.sessionId)

        call.respond(HttpStatusCode.Created)
    }

    /**
     * Ends a chat session.
     * params: channel ID
     * responses:
     *      204 NoContent (Success)
     *      404 NotFound (If ID is not found)
     *      400 BadRequest (If param is wrong format / missing)
     */
    post("/cancel") {
        val body = call.receive<RequestEnd>()

        SessionManager
            .end(body.sessionId)

        call.respond(HttpStatusCode.NoContent)
    }

}