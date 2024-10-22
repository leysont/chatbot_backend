package bot

import bot.Prompts.IPrompt
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlin.reflect.typeOf

interface AiModel {
    fun generate(prompt: String): String?
    fun generate(prompt: IPrompt): String? = generate(
        Json.Default.encodeToString(prompt)
    )

}

inline fun <reified T> AiModel.generateTo(prompt: IPrompt): T {
    val answer = generate(prompt) ?: throw AiNoAnswerException()
    return try {
        Json.Default.decodeFromString<T>(answer)
    } catch (e: IllegalArgumentException) {
        throw AiWrongFormatException("Expected answer deserializable to ${typeOf<T>()}, got $answer.")
    }
}

class Gpt4oMini : AiModel {

    override fun generate(prompt: String): String? {
        TODO("OpenAI call in generate()")
    }
}