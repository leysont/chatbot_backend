package bot

import bot.Prompts.Prompt
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

interface AiModel {
    fun generate(prompt: String): String
    fun generate(prompt: Prompt): String = generate(
        Json.Default.encodeToString(prompt)
    )
}

class Gpt4oMini : AiModel {

    override fun generate(prompt: String): String {
        TODO("OpenAI call in generate()")
    }
}