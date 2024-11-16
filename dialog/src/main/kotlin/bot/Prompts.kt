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
        suspend fun execute(): Any?
    }

    @Serializable
    sealed class DefaultCommandPrompt : IPrompt {
        abstract val promptCommand: String
        abstract val context: Context
        // abstract val key: String
    }

    @Serializable
    class ExtractInfo(
        override val context: Context,
        override val promptCommand: String = "/extractInfo",
    ) : DefaultCommandPrompt() {
        override suspend fun execute(): Info = model.generateTo(this, context)
    }

    @Serializable
    class GatherInfo(
        override val context: Context,
        override val promptCommand: String = "/gatherInfo",
    ) : DefaultCommandPrompt() {
        override suspend fun execute(): String = model.generateTo(this, context)
    }

    @Serializable
    class RequestTicketConfirmation(
        override val context: Context,
        override val promptCommand: String = "/requestTicketConfirmation",
    ) : DefaultCommandPrompt() {
        override suspend fun execute(): String = model.generateTo(this, context)
    }

    @Serializable
    class GeneratePotentialSolutions(
        override val context: Context,
        override val promptCommand: String = "/GeneratePotentialSolutions",
    ) : DefaultCommandPrompt() {
        override suspend fun execute(): String = model.generateTo(this, context)
    }

    @Serializable
    class ExtractBooleanNullable(
        override val context: Context,
        override val promptCommand: String = "/extractBoolean",
    ) : DefaultCommandPrompt() {
        override suspend fun execute(): Boolean? = model.generateTo(this, context)
    }

    @Serializable
    data class AssignEmployee(
        override val context: Context,
        override val promptCommand: String = "/assignEmployee",
        val tags: List<String>,
        val employees: List<Employee>,
    ) : DefaultCommandPrompt() {
        override suspend fun execute(): Employee {
            val employeeId = model.generateTo<Int>(this, context)

            return employees.find {
                it.id == employeeId
            } ?: throw AiInvalidOutputException("Expected valid employee ID, got $employeeId.")
        }
    }

    @Serializable
    data class GreetCustomer(
        override val context: Context,
        override val promptCommand: String = "/greetCustomer",
    ) : DefaultCommandPrompt() {
        override suspend fun execute(): String = model.generateTo(this, context)
    }
}