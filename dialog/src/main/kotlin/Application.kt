import bot.*
import bot.AssistantMessage.Intent.GatherInfo
import bot.Issue.Keys
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.routing.*
import kotlinx.coroutines.runBlocking
import plugins.configure

const val port = 8080

fun main() {
    embeddedServer(Netty, port = port, host = "0.0.0.0", module = Application::module)
        .start(wait = true)
}

fun Application.module() {
    configure(port)

    routing {
         configureEndpoints()

        runBlocking {
            testGreet()
            // testGatherInfo()
        }
    }
}

suspend fun testGreet() {
    val keyEnv: String? = System.getenv("OPENAI_API_KEY")
    val keyBad =
        "sk-svcacct-fOMDj1LVVCTLGds7pPPJZH6HzkYWJcu14ALoXv4zgrGeZbIXqoIMnRncWaFwP1X4JkzTT3BlbkFJ9WuvzXT7fa9DiNB6RdPQn3DzIWJjoqDwBeSeHzHdUpQIJnxdoiYFTq51e1YpHSPTXdQA"

    var gpt: AiModel = Gpt4oMini(keyBad)

    Prompts.setApiToken(keyBad)

    val context = Context(
        history = History().apply {
            add(SystemMessage(readBotConfigYaml()))
          },
        issue = Issue().apply {
            set(Keys.CustomerId.name, "5")
        },
    )

    val response = Prompts.GreetCustomer(context).execute()
    println("response:")
    println(response)
}

suspend fun testGatherInfo() {
    val keyEnv: String? = System.getenv("OPENAI_API_KEY")
    val keyBad =
        "sk-svcacct-fOMDj1LVVCTLGds7pPPJZH6HzkYWJcu14ALoXv4zgrGeZbIXqoIMnRncWaFwP1X4JkzTT3BlbkFJ9WuvzXT7fa9DiNB6RdPQn3DzIWJjoqDwBeSeHzHdUpQIJnxdoiYFTq51e1YpHSPTXdQA"

    var gpt: AiModel = Gpt4oMini(keyBad)

    Prompts.setApiToken(keyBad)

    val context = Context(
        history = History().apply {
            add(SystemMessage(readBotConfigYaml()))
            add(AssistantMessage("Hello I am your assistant. What can I help you with?", GatherInfo))
            add(UserMessage("visual studio code isn't starting"))
            add(AssistantMessage("I'm here to help! Can you tell me if you see any error messages when trying to start Visual Studio Code? Additionally, have there been any recent changes to your system that might have affected it?", GatherInfo))
            add(UserMessage("It just says something about executable not found"))
            add(AssistantMessage("Thank you for the information! It sounds like you're encountering an error related to the executable. Can you please confirm if you expected Visual Studio Code to start up without any issues?", GatherInfo))
            add(UserMessage("What else would i be expecting"))
            add(AssistantMessage("That makes sense! Just to clarify, let's summarize what you're experiencing. You mentioned Visual Studio Code isn't starting, and you're seeing an error message about the executable not being found. Is there anything specific you were doing or have recently changed before this issue occurred?\n", GatherInfo))
            add(UserMessage("Can you give me the issue as you have it at the moment? as json please"))

          },
        issue = Issue().apply {
            set(Keys.CustomerId.name, "5")
            set(Keys.CustomerName.name, "John")
        },
    )

    val response = Prompts.GatherInfo(context).execute()
    println("response:")
    println(response)
}
