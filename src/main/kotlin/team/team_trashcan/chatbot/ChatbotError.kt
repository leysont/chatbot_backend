package team.team_trashcan.chatbot

sealed class ChatbotError(
    override val message: String?
): Throwable(message) {
    class UserEndedSession: ChatbotError("The session was ended by the user")
}