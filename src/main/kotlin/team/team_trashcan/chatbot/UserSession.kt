package team.team_trashcan.chatbot

class UserSession(
    val bot: Bot,
    val onSessionEnded: () -> Unit
) {

    private val issue = Issue()

    /**
     * Maintains a history of interactions within a user session.
     * The key consists of the role, the value is the message.
     */
    val history = mutableListOf<Message>()

    data class Message(
        val role: String,
        val content: String
    )

    fun readUserMessage(): Result<String> {
        val message = ""

        history.add(Message(role = "user", content = message))

        TODO("Not yet implemented")
    }

    fun respond(message: String) {

        history.add(Message(role = "user", content = message))
        TODO("Not yet implemented")
    }

    fun waitForSummaryCheck(): Result<Boolean> {
        readUserMessage()

        TODO("Not yet implemented")
    }

    fun waitForSolutionCheck(): Result<Boolean> {
        readUserMessage()

        TODO("Not yet implemented")
    }

    fun sessionLoop(): Result<Issue?> {
        Result
        val isMainLoop = true

        while (isMainLoop) {
            mainChatLoop()
                .onFailure { error ->
                    return Result.failure(error)
                }
                .onSuccess { summary ->
                    respond(summary)
                }

            val summaryCheckResult = waitForSummaryCheck()
                .onFailure { error ->
                    when (error) {
                        is ChatbotError.UserEndedSession -> {

                        }
                    }
                }
                .onSuccess { it ->

                }

            if (summaryCheckResult.getOrNull() as Boolean) {
                val solutions = bot.generatePotentialSolutions()
                respond(solutions)
                val solutionCheckResult = waitForSolutionCheck()

                solutionCheckResult.onFailure { error ->
                    when (error) {
                        is ChatbotError.UserEndedSession -> {

                        }
                    }
                }

                if (solutionCheckResult.getOrNull() as Boolean) {
                    // Success: Problem resolved
                    respond("Issue resolved successfully! Chat will now end. Thank you for using the ticket system")
                    return Result.success(null)

                } else {
                    // Success: Problem not resolved, ticket submitted
                    respond("Issue not resolved. Ticket is being sent to the system for human processing.")
                    return Result.success(issue)
                }
            } else {
                respond("Summary not accurate? Please provide more information.")
                // Restart session loop. Chat history and past summaries are kept track of in the bot session.
            }
        }
        // end
        TODO()
    }

    private fun mainChatLoop(): Result<String> {
        val isLooping = true

        while (isLooping) {
            var isIssueComplete = false
            val userInputResult = readUserMessage()
            //TODO: record user message in bot session history

            //TODO: set guards to avoid off-topic chat

            userInputResult.onFailure { error ->
                when (error) {
                    is ChatbotError.UserEndedSession -> {
                        return Result.failure(error)
                    }
                }
            }

            userInputResult.onSuccess { input ->
                val info = bot.extractInfo(input)
                if (info != null) {
                    //relevant info
                    issue[info.field] = info.value
                    isIssueComplete = issue.isComplete()
                }
            }
            if (isIssueComplete) break

            // TODO: set a guard to avoid getting stuck in the loop without resolving the issue

            val nextMessage = bot.generateNextMessage("Try to determine missing info to complete the ticket")
            respond(nextMessage)
        }

        // only reached if ticket has been completed
        return Result.success(bot.generateTicketSummary(issue))
    }

}