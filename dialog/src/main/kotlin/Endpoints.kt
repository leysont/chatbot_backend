import bot.AssistantMessage
import bot.SessionManager
import bot.SystemMessage
import bot.UserMessage
import data.Repos
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.serialization.Serializable
import models.Ticket

@Serializable
data class RequestMessage(
    val sessionId: String,
    val message: String,
    val isSystemMessage: Boolean? = false,
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
     *  - `200 Ok`: Success, if user message
     *  - `204 NoContent`: Success, if system message
     *  - `404 NotFound`: If any ID is not found
     *  - `400 BadRequest`: If any param is wrong format / missing
     *  - `500 InternalServerError`: If no response could be generated
     */
    post("/message") {
        val (sessionId, message, isSystem) = try {
            call.receive<RequestMessage>()
        } catch (ex: ContentTransformationException) {
            call.respond(HttpStatusCode.BadRequest, ex.message!!)
            return@post
        }

        postMessage(sessionId, message, isSystem)
    }

    /**
     * Start a new chat session.
     * params: userId, channelId (becomes issue ID)
     * responses:
     *  - `201 Created`: Success
     *  - `404 NotFound`: If any ID is not found
     *  - `400 BadRequest`: If any ID is wrong format / missing
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
     *  - `204 NoContent`: Success
     *  - `404 NotFound`: If ID is not found
     *  - `400 BadRequest`: If param is wrong format / missing
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
     *  - `200 Ok`: Success
     *  - `404 NotFound`: If ID is not found
     *  - `400 BadRequest`: If param is wrong format / missing
     */
    get("/supporter_role") {
        val (status, message) = getSupporterRole(call.parameters["serverId"])
        call.respond(status, message ?: "")
    }
}

/**
 * Posts a message to the designated session and processes it.
 *
 * @param sessionId The unique identifier for the session.
 * @param message The content of the message to be posted.
 * @param isSystem A flag indicating if the message is a system message (true) or user message (false or null).
 *
 * @return An HttpSimpleResponse object containing the status code and an optional message.
 * - `200 Ok`: Success, if user message; returns AI response
 * - `204 NoContent`: Success, if system message
 * - `400 BadRequest`: If the sessionId or message is blank.
 * - `404 NotFound`: If the session is not found.
 * - `500 InternalServerError`: If no response could be generated.
*/
private suspend fun postMessage(
    sessionId: String,
    message: String,
    isSystem: Boolean?,
): HttpSimpleResponse {


    if (sessionId.isBlank() || message.isBlank()) {
        return HttpSimpleResponse(HttpStatusCode.BadRequest, "sessionId and message cannot be blank")
    }

    val session = SessionManager
        .get(sessionId)

    if (session == null) {
        return HttpSimpleResponse(HttpStatusCode.NotFound)
    }

    if (isSystem == true) {
        session.history.add(SystemMessage(message))
        return HttpSimpleResponse(HttpStatusCode.NoContent)
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