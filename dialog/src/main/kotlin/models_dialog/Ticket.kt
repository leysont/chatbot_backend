package models_dialog

import kotlinx.serialization.Serializable

/**
 * Represents a support ticket in the system.
 *
 * @property id The unique identifier for the ticket. This value is nullable and defaults to null.
 * @property title A brief title summarizing the issue of the ticket.
 * @property customer The customer who reported the issue. This is an instance of the Customer class.
 * @property issueExperience A detailed description of what the customer experienced.
 * @property issueExpectation A detailed description of what the customer expected instead of the issue.
 * @property employee The employee assigned to handle the ticket. This is an instance of the Employee class.
 * @property express Indicates whether the ticket is marked as express (requires urgent attention). Defaults to false.
 * @property tags A list of tags associated with the ticket. Defaults to an empty list.
 * @property serviceLevel The service level of the ticket, inherited from the customer's service level.
 */
@Serializable
data class Ticket(
    val id: Int? = null,
    val title: String,
    val customer: Customer,
    val issueExperience: String,
    val issueExpectation: String,
    val employee: Employee,
    val express: Boolean = false,
    val tags: List<String> = listOf(),
) {
    val serviceLevel: ServiceLevel = customer.serviceLevel
}