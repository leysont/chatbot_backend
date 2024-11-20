package bot

import bot.AssistantMessage.Intent
import bot.Issue.Keys.*
import data.Repos
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.Serializable
import models.Ticket
import java.io.File

@Serializable
sealed interface Message {
    val content: String
}

@Serializable
data class UserMessage(
    override val content: String,
) : Message

@Serializable
class SystemMessage(
    // content: String,
    override val content: String,
) : Message

@Serializable
class AssistantMessage(
    // content: String,
    override val content: String,
    val intent: Intent,
    val body: Ticket? = null,
) : Message {
    enum class Intent {
        Summary, Solution, GatherInfo, EndAndSolved, EndAndSendTicket,
    }
}

@Serializable
class History {
    /**
     * Maintains a history of interactions within a user session.
     * The key consists of the role, the value is the message.
     */
    val historyList = mutableListOf<Message>()

    fun add(message: Message) {
        historyList.add(message)
    }

    fun lastAssistantMessage(): AssistantMessage? {
        return historyList.lastOrNull { message -> message is AssistantMessage } as AssistantMessage?
    }
}

object SessionManager {

    // TODO: Replace session map with Repos.Tickets.get(id) and filter open. New error if it's closed.
    private val sessions = mutableMapOf<String, Session>()

    suspend fun get(id: String): Session? {
        val ticket = Repos.Tickets.get(id.toInt())
        return sessions[id]
    }

    fun getAll(): Map<String, Session> {
        return sessions
    }

    fun end(id: String) {
        sessions.remove(id)
    }

    fun create(userId: String, sessionId: String): Session {
        return Session(sessionId, userId).also {
            sessions[sessionId] = it
        }
    }
}

@Serializable
data class Context(
    var history: History,
    var issue: Issue,
)

/**
 * Reads the bot configuration from a YAML file located in the resources directory.
 *
 * @return The content of the bot configuration file as a UTF-8 string.
 * @throws
 */
fun readBotConfigYaml(): String {
    val classLoader = Thread.currentThread().contextClassLoader
    val resource = classLoader.getResource("bot_config.yaml")
        ?: throw IllegalArgumentException("File not found: bot_config.yaml")

    val workingDir = System.getProperty("user.dir")
    println("Current working directory: $workingDir")
    return File(Env.botConfigPath)
        .readText(Charsets.UTF_8)
}

class Session(
    val sessionId: String,
    val userId: String,
) {

    private val issue = Issue().apply {
        this[TicketId.name] = sessionId
        this[CustomerId.name] = userId
        println("Session init. userId: $userId, sessionId: $sessionId")
        this[CustomerName.name] = runBlocking { userId.toIntOrNull()?.let { Repos.Customers.get(it) } }?.name
    }

    val history = History()

    private val context: Context
        get() = Context(history, issue)

    init {
        // Initialize conversation by sending the bot config as a system message
        val botConfigYaml = readBotConfigYaml()
        history.add(SystemMessage(botConfigYaml))
    }

    suspend fun
            processMessage(): AssistantMessage? = try {
        when (history.lastAssistantMessage()?.intent) {

            // Last message was a summary, check response; if successful send solution, else gather info
            Intent.Summary -> {
                when (Prompts.ExtractBooleanNullable(context).execute()) {
                    true -> {
                        // Ticket is complete and accurate. Generate solutions.
                        AssistantMessage(
                            Prompts.GeneratePotentialSolutions(context).execute(),
                            Intent.Solution
                        )
                    }

                    false -> {
                        // Ticket is incomplete. Continue gather information
                        AssistantMessage(
                            Prompts.GatherInfo(context).execute(),
                            Intent.GatherInfo
                        )
                    }

                    null -> {
                        // User message is not clear on whether the ticket is complete. Continue gather information
                        AssistantMessage(
                            Prompts.GatherInfo(context).execute(),
                            Intent.GatherInfo
                        )
                    }
                }
            }

            // Last message was a solution, check response; if successful end chat, else send ticket
            Intent.Solution -> {
                val solutionResult = Prompts.ExtractBooleanNullable(context).execute()
                if (solutionResult == true) {
                    // TODO: Replace solved issue message with prompt
                    AssistantMessage(
                        "Issue resolved successfully! 🙌 Chat will now end. Thank you for using the ticket system.👋",
                        Intent.EndAndSolved
                    )
                } else {
                    // TODO: Replace sent ticket message with prompt
                    AssistantMessage(
                        content = "Issue could not be resolved, ticket is being sent.",
                        body = issue.toTicket(context),
                        intent = Intent.EndAndSendTicket,
                    )
                }
            }

            // Either no previous message or it was GatherInfo; extract info and keep asking.
            // Generate summary if issue is complete.
            else -> {
                // TODO Advanced: set guards to avoid off-topic chat

                Prompts.ExtractInfo(context).execute().let {
                    issue[it.field] = it.value
                }

                when (issue.isComplete()) {
                    true -> {
                        val ticket = issue.toTicket(context)
                        // TODO: Handle ticket being null (only if employee couldn't be assigned)
                        AssistantMessage(
                            Prompts.RequestTicketConfirmation(context).execute(),
                            Intent.Summary,
                            ticket,
                        )
                    }

                    false -> {
                        // TODO Advanced: set a guard to avoid getting stuck in the loop without resolving the issue
                        AssistantMessage(
                            Prompts.GatherInfo(context).execute(),
                            Intent.GatherInfo,
                        )
                    }
                }
            }
        }

    } catch (ex: Exception) { // TODO Replace null return with error object cast to AssistantMessage or smth
        null
    }
}

@Serializable
data class Info(
    var field: String,
    var value: String,
)