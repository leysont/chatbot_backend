package bot

import bot.Prompts.IPrompt
import com.aallam.openai.api.chat.ChatChoice
import com.aallam.openai.api.chat.ChatCompletionRequest
import com.aallam.openai.api.chat.ChatMessage
import com.aallam.openai.api.chat.ChatRole
import com.aallam.openai.api.http.Timeout
import com.aallam.openai.api.model.ModelId
import com.aallam.openai.client.OpenAI
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlin.time.Duration.Companion.seconds

interface AiModel {
    suspend fun generate(promptText: String, context: Context): String?
    suspend fun generate(prompt: IPrompt): String? = generate(
        Json.Default.encodeToString(prompt), prompt.context
    )
}

suspend inline fun <reified T> AiModel.generateTo(prompt: IPrompt): T {
    val answer = generate(prompt) ?: throw AiNoAnswerException()
    return try {
        when (T::class) {
            String::class -> answer as T
            else -> Json.Default.decodeFromString<T>(answer)
        }
    } catch (e: IllegalArgumentException) {
        answer as T
        // throw AiWrongFormatException("Expected answer deserializable to ${typeOf<T>()}, got $answer.")
    }
}

class Gpt4oMini(apiToken: String) : AiModel {
    private val openAi = OpenAI(
        token = apiToken,
        timeout = Timeout(socket = 60.seconds),
    )

    private val model = ModelId("gpt-4o-mini")

    override suspend fun generate(
        promptText: String,
        context: Context,
    ): String {
        return generateChatChoice(context, promptText).message.content!!
    }

    private suspend fun generateChatChoice(context: Context, prompt: String): ChatChoice {
        return openAi.chatCompletion(
            ChatCompletionRequest(
                model = model,
                messages = context.history.historyList.map {
                    ChatMessage(
                        role = when (it) {
                            is AssistantMessage -> ChatRole.Assistant
                            is SystemMessage -> ChatRole.System
                            is UserMessage -> ChatRole.User
                        },
                        content = it.content
                    )
                }.toMutableList().apply {
                    add(ChatMessage(ChatRole.System, prompt))
                }
            )
        ).choices.first()
    }
}