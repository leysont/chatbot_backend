package bot

import kotlinx.serialization.Serializable
import models.Employee

object Prompts {
    lateinit var model: AiModel

    fun setApiToken(token: String) {
        model = Gpt4oMini(token)
    }

    @Serializable
    sealed interface IPrompt {
        val context: Context

        suspend fun execute(): Any?
    }

    @Serializable
    sealed interface ICommandPrompt : IPrompt {
        val promptCommand: String
        // abstract val key: String
    }

    @Serializable
    class CustomPrompt(
        override val context: Context
    ) : IPrompt {
        override suspend fun execute(): String = model.generateTo(this)

    }

    @Serializable
    class ExtractInfo(
        override val context: Context,
        override val promptCommand: String = "/extractInfo",
    ) : ICommandPrompt {
        override suspend fun execute(): Info = model.generateTo(this)
    }

    @Serializable
    class GatherInfo(
        override val context: Context,
        override val promptCommand: String = "/gatherInfo",
    ) : ICommandPrompt {
        override suspend fun execute(): String = model.generateTo(this)
    }

    @Serializable
    class RequestTicketConfirmation(
        override val context: Context,
        override val promptCommand: String = "/requestTicketConfirmation",
    ) : ICommandPrompt {
        override suspend fun execute(): String = model.generateTo(this)
    }

    @Serializable
    class GeneratePotentialSolutions(
        override val context: Context,
        override val promptCommand: String = "/GeneratePotentialSolutions",
    ) : ICommandPrompt {
        override suspend fun execute(): String = model.generateTo(this)
    }

    @Serializable
    class ExtractBooleanNullable(
        override val context: Context,
        override val promptCommand: String = "/extractBoolean",
    ) : ICommandPrompt {
        override suspend fun execute(): Boolean? = model.generateTo(this)
    }

    @Serializable
    data class AssignEmployee(
        override val context: Context,
        override val promptCommand: String = "/assignEmployee",
        val tags: List<String>,
        val employees: List<Employee>,
    ) : ICommandPrompt {
        override suspend fun execute(): Employee {
            val employeeId = model.generateTo<Int>(this)

            return employees.find {
                it.id == employeeId
            } ?: throw AiInvalidOutputException("Expected valid employee ID, got $employeeId.")
        }
    }

    @Serializable
    data class GreetCustomer(
        override val context: Context,
        override val promptCommand: String = "/greetCustomer",
    ) : ICommandPrompt {
        override suspend fun execute(): String = model.generateTo(this)
    }
}