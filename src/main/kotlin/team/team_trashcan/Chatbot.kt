package team.team_trashcan

class Chatbot {
    private val ticket = MapTicket()
    // contains frontend calls
    private val user = User()
    // session keeps tracks of chat history and chat progress
    private val bot = Bot.newSession()

    fun sessionLoop() {
        val isMainLoop = true

        while(isMainLoop){
            val summary = chatLoop()
            user.respond(summary)
            val isSummaryAccurate = user.waitForSummaryCheck()

            if (isSummaryAccurate) {
                val solutions = bot.generatePotentialSolutions()
                user.respond(solutions)
                val isIssueResolved = user.waitForSolutionCheck()

                if (isIssueResolved) {
                    user.respond("Issue resolved successfully! Chat will now end. Thank you for using the ticket system")
                    break // Success: Problem resolved

                } else {
                    user.respond("Issue not resolved. Ticket is being sent to the system for human processing.")
                    break // Success: Problem not resolved, ticket submitted
                }
            } else {
                user.respond("Summary not accurate? Please provide more information.")
                // Restart session loop. Chat history and past summaries are kept track of in the bot session.
            }
        }
        // end
    }

    private fun chatLoop(): String {
        val isLooping = true

        while (isLooping) {

            val message = user.readUserMessage()

            bot.processMessage(message)

            val info = bot.determineIfMessageContainsRelevantInfoAndReturnInfoOrNull(message)
            if (info != null) {
                //relevant info
                ticket[info.field] = info.value
                if (ticket.isComplete()) {
                    break
                }
            }

            val nextMessage = bot.generateNextMessage(prompt = "Try to determine missing info to complete the ticket")
            user.respond(nextMessage)
            continue
        }

        // only reached if ticket has been completed
        return bot.generateTicketSummary(ticket)
    }

}

class MapTicket {
    private val map = mutableMapOf<String, String>()

    operator fun set(key: String, value: String) {
        map[key] = value
    }

    operator fun get(key: String): String? {
        return map[key]
    }

    fun isComplete(): Boolean {
        TODO("Not yet implemented")
    }
}

class User {
    fun readUserMessage(): String {
        TODO("Not yet implemented")
    }

    fun respond(nextMessage: String) {

    }

    fun waitForSummaryCheck(): Boolean {
        TODO("Not yet implemented")
    }

    fun waitForSolutionCheck(): Boolean {
        TODO("Not yet implemented")
    }

}

class Bot {
    fun processMessage(message: String) {
        TODO("Not yet implemented")
    }

    fun determineIfMessageContainsRelevantInfoAndReturnInfoOrNull(message: String): Info? {
        TODO("Not yet implemented")
    }

    fun generateNextMessage(prompt: String): String {
        TODO("Not yet implemented")
    }

    fun generateTicketSummary(ticket: MapTicket): String {
        TODO("Not yet implemented")
    }

    fun generatePotentialSolutions(): String {
        TODO("Not yet implemented")
    }

    companion object {
        fun newSession(): Bot {
            TODO("Not yet implemented")
        }
    }
}

data class Info(
    var field: String = "",
    var value: String = "",
)
