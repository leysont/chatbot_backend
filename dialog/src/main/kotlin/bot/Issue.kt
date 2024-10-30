package bot

import bot.Issue.Keys.*
import bot.Prompts.AssignEmployee
import data.Repos
import data.Repos.Employees
import kotlinx.serialization.Serializable
import models.Customer
import models.Ticket

@Serializable
class Issue {

    private val map = mutableMapOf<String, String?>()

    operator fun set(key: String, value: String?) {
        map[key] = value
    }

    operator fun get(key: String): String? {
        return map[key]
    }

    enum class Keys {
        TicketTitle,
        TicketId,
        TicketTags,
        CustomerId,
        CustomerName,
        IssueExpectation,
        IssueExperience,
        Express,
    }

    fun isComplete(): Boolean = !map[TicketTitle.name].isNullOrBlank()
            && !map[TicketId.name].isNullOrBlank()
            && !map[TicketTags.name].isNullOrBlank()
            && !map[CustomerId.name].isNullOrBlank()
            && !map[CustomerName.name].isNullOrBlank()
            && !map[IssueExpectation.name].isNullOrBlank()
            && !map[IssueExperience.name].isNullOrBlank()
            && !map[Express.name].isNullOrBlank()

    suspend fun toTicket(context: Context): Ticket? {

        val id = map[TicketId.name]!!.toIntOrNull()
        val title = map[TicketTitle.name]!!

        val customerId = map[CustomerId.name]!!.toInt()
        val customerName = map[CustomerName.name]!!
        val customer = Repos.Customers.get(customerName.toIntOrNull()!!)
            ?: Repos.Customers.add(Customer(customerId, customerName))

        val issueExperience = map[IssueExperience.name]!!
        val issueExpectation = map[IssueExpectation.name]!!

        val tags = map[TicketTags.name]!!.split(",").map { it.trim() }
        val employee = AssignEmployee(
            context = context,
            tags = tags,
            employees = Employees.getAll() ?: return null
        ).execute()

        val express = map[Express.name]!!.contains("true")

        return Ticket(
            id = id,
            title = title,
            customer = customer,
            issueExperience = issueExperience,
            issueExpectation = issueExpectation,
            employee = employee,
            express = express,
            tags = tags,
        )
    }
}