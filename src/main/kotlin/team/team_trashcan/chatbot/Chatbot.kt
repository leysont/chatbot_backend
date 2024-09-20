package team.team_trashcan.chatbot

fun handleFrontendCall(receiveText: String) {

}

class Chatbot {

    fun newSession() {
        val bot = Bot.newSession()

        val userSession = UserSession(
            bot,
            onSessionEnded = TODO()
        )
            .sessionLoop()
    }

    // session keeps tracks of chat history and chat progress


}