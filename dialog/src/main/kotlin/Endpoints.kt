import bot.AssistantMessage
import bot.SessionManager
import bot.UserMessage
import data.Repos
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.*
import io.ktor.server.request.ContentTransformationException
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.Routing
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import kotlinx.serialization.Serializable
import models_dialog.Ticket

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
        val (sessionId, message) = try {
            call.receive<RequestMessage>()
        } catch (ex: ContentTransformationException) {
            call.respond(HttpStatusCode.BadRequest, ex.message!!)
            return@post
        }

        postMessage(sessionId, message)
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
        val (userId, sessionId) = try {
            call.receive<RequestStart>()
        } catch (e: ContentTransformationException) {
            call.respond(HttpStatusCode.BadRequest, e.message ?: "")
            return@post
        }

        SessionManager.create(userId, sessionId)

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
        val (sessionId) = call.receive<RequestEnd>()

        SessionManager.end(sessionId)

        call.respond(HttpStatusCode.NoContent)
    }

    /**
     * Gets the supporter role ID for a server by its ID
     * params: server ID
     * responses:
     *      200 Ok (Success)
     *      404 NotFound (If ID is not found)
     *      400 BadRequest (If param is wrong format / missing)
     */
    get("/supporter_role") {
        val (status, message) = getSupporterRole(call.parameters["serverId"])
        call.respond(status, message ?: "")
    }
}

private suspend fun postMessage(sessionId: String, message: String): HttpSimpleResponse {

    if (sessionId.isBlank() || message.isBlank()) {
        return HttpSimpleResponse(HttpStatusCode.BadRequest, "sessionId and message cannot be blank")
    }

    val session = SessionManager
        .get(sessionId)

    if (session == null) {
        return HttpSimpleResponse(HttpStatusCode.NotFound)
    }

    session.history.add(UserMessage(message))

    val response = session.processMessage()

    data class ResponseSummary(val message: String, val body: Ticket)

    return when (response?.intent) {

        AssistantMessage.Intent.EndAndSolved -> {
            SessionManager.end(sessionId)
            session.history.add(UserMessage(response.content))
            HttpSimpleResponse(
                HttpStatusCode.OK,
                response.content
            )
        }

        AssistantMessage.Intent.EndAndSendTicket -> {
            SessionManager.end(sessionId)
            session.history.add(UserMessage(response.content))
            Repos.Tickets.add(response.body!!)
            HttpSimpleResponse(
                HttpStatusCode.OK,
                response.content
            )
        }

        AssistantMessage.Intent.Summary -> {
            session.history.add(UserMessage(response.content))
            HttpSimpleResponse(
                HttpStatusCode.OK,
                ResponseSummary(response.content, response.body!!)
            )
        }

        null -> {
            // No AssistantMessage could be generated. Probably OpenAI API not available. 😨
            HttpSimpleResponse(HttpStatusCode.InternalServerError)
        }

        else -> {
            // Either GatherInfo or Solution
            session.history.add(UserMessage(response.content))
            HttpSimpleResponse(HttpStatusCode.OK, response.content)
        }
    }
}

data class HttpSimpleResponse(val status: HttpStatusCode, val message: Any? = null)

suspend fun getSupporterRole(serverId: String?): HttpSimpleResponse {
    val serverIdInt = serverId?.toIntOrNull()
        ?: return HttpSimpleResponse(
            HttpStatusCode.BadRequest,
            "serverId has to be an integer"
        )

    val server = Repos.Servers.get(serverIdInt)
        ?: return HttpSimpleResponse(
            HttpStatusCode.NotFound,
            "Could not find server with id $serverIdInt"
        )

    return HttpSimpleResponse(
        HttpStatusCode.OK,
        server.supporterRoleID.toString()
    )
}