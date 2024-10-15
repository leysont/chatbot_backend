package bot

import bot.AssistantMessage.Intent
import kotlinx.serialization.Serializable

abstract class Message(
    open val content: String,
)

data class UserMessage(
    override val content: String,
) : Message(content)

data class AssistantMessage(
    override val content: String,
    val intent: Intent,
) : Message(content) {
    enum class Intent {
        Summary, Solution, GatherInfo, EndAndSolved, EndAndSendTicket,
    }
}

class History {
    /**
     * Maintains a history of interactions within a user session.
     * The key consists of the role, the value is the message.
     */
    private val history = mutableListOf<Message>()

    fun add(message: Message) {
        history.add(message)
    }

    fun lastAssistantMessage(): AssistantMessage? {
        return history.lastOrNull { message -> message is AssistantMessage } as AssistantMessage?
    }
}

object SessionManager {

    // TODO: Move to database for persistence
    val sessions = mutableMapOf<String, Session>()

    fun get(id: String): Session? {
        return sessions[id]
    }

    fun end(id: String) {
        sessions.remove(id)
    }

    fun create(userId: String, sessionId: String): Session {
        return Session(userId).also {
            sessions[sessionId] = it
        }
    }
}

class Session(
    val userId: String,
) {
    private val issue = Issue()

    val history = History()

    fun processMessage(userMessage: String): AssistantMessage? =
        when (history.lastAssistantMessage()?.intent) {

            // Last message was a summary, check response; if successful send solution, else gather info
            Intent.Summary -> {
                val summaryResult = Prompts.extractBoolean(
                    message = userMessage,
                    prompt = "Consider the userMessage an answer to a support ticket summary with the question: 'Does this summary accurately describe your issue?'. If the userMessage represents a positive response to this question, return `true`, otherwise return `false` If it isn't entirely clear, return `null`/ (Don't include the backticks)."
                )
                if (summaryResult == true) {
                    val solutions = Prompts.generatePotentialSolutions()

                    AssistantMessage(
                        solutions,
                        Intent.Solution
                    )
                } else {
                    AssistantMessage(
                        "Summary not accurate? Please provide more information.",
                        Intent.GatherInfo
                    )
                }
            }

            // Last message was a solution, check response; if successful end chat, else send ticket
            Intent.Solution -> {
                val solutionResult = Prompts.extractBoolean(
                    message = userMessage,
                    prompt = "Consider the userMessage an answer to a potential tech support solution with the question: 'Has this solution worked?'. If the userMessage represents a positive response to this question, return `true`, otherwise return `false` If it isn't entirely clear or the user hasn't tried the solution, return `null`/ (Don't include the backticks)."
                )
                if (solutionResult == true) {
                    AssistantMessage(
                        "Issue resolved successfully! 🙌 Chat will now end. Thank you for using the ticket system.👋",
                        Intent.EndAndSolved
                    )
                } else {
                    AssistantMessage(
                        "Issue could not be resolved, ticket is being sent.",
                        Intent.EndAndSendTicket
                    )
                }
            }

            // Either no previous message or it was GatherInfo; extract info and keep asking.
            // Generate summary if issue is complete.
            else -> {
                //TODO: set guards to avoid off-topic chat
                val info = Prompts.extractInfo(userMessage)
                if (info != null) {
                    issue[info.field] = info.value
                }

                val response = if (issue.isComplete()) {
                    Prompts.generateTicketSummary(issue)
                } else {
                    // TODO: set a guard to avoid getting stuck in the loop without resolving the issue
                    Prompts.gatherInfo("Try to determine missing info to complete the ticket")
                }

                AssistantMessage(
                    response,
                    Intent.GatherInfo
                )
            }
        }
}

@Serializable
data class Info(
    var field: String,
    var value: String,
)