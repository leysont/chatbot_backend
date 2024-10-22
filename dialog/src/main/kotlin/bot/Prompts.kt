package bot

import kotlinx.serialization.Serializable
import models_dialog.Employee

object Prompts {

    private val model: AiModel = Gpt4oMini()

    interface IPrompt {
        fun execute(): Any?
    }

    @Serializable
    abstract class DefaultCommandPrompt(
    ) : IPrompt {
        abstract val promptCommand: String
        abstract val context: Context
    }

    @Serializable
    class ExtractInfo(
        override val context: Context,
        override val promptCommand: String = "/extractInfo",
    ) : DefaultCommandPrompt() {
        override fun execute(): Info = model.generateTo(this)
    }

    @Serializable
    class GatherInfo(
        override val context: Context,
        override val promptCommand: String = "/gatherInfo",
    ) : DefaultCommandPrompt() {
        override fun execute(): String = model.generateTo(this)
    }

    @Serializable
    class RequestTicketConfirmation(
        override val context: Context,
        override val promptCommand: String = "/requestTicketConfirmation",
    ) : DefaultCommandPrompt() {
        override fun execute(): String = model.generateTo(this)
    }

    @Serializable
    class GeneratePotentialSolutions(
        override val context: Context,
        override val promptCommand: String = "/GeneratePotentialSolutions",
    ) : DefaultCommandPrompt() {
        override fun execute(): String = model.generateTo(this)
    }

    @Serializable
    class ExtractBooleanNullable(
        override val context: Context,
        override val promptCommand: String = "/extractBoolean",
    ) : DefaultCommandPrompt() {
        override fun execute(): Boolean? = model.generateTo(this)
    }

    @Serializable
    data class AssignEmployee(
        override val context: Context,
        override val promptCommand: String = "/assignEmployee",
        val tags: List<String>,
        val employees: List<Employee>,
    ) : DefaultCommandPrompt() {
        override fun execute(): Employee {
            val employeeId = model.generateTo<Int>(this)

            return employees.find {
                it.id == employeeId
            } ?: throw AiInvalidOutputException("Expected valid employee ID, got $employeeId.")
        }
    }
}