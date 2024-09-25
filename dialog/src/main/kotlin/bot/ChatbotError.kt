package bot

sealed class ChatbotError(
    override val message: String?
): Throwable(message) {
    class UserEndedSession: ChatbotError("The session was ended by the user")
}