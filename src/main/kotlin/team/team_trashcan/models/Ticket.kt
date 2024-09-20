package team.team_trashcan.models

import kotlinx.serialization.Serializable
import team.team_trashcan.data.ServiceLevel

/**
 * Represents a ticket in the system.
 *
 * @property id The unique identifier for the ticket.
 * @property title The title of the ticket.
 * @property customer The customer who reported the issue.
 * @property issueDescription A detailed description of the issue.
 * @property employee The employee assigned to handle the ticket.
 * @property express Indicates whether the ticket is marked as express.
 * @property serviceLevel The service level of the ticket, defaults to the customer's service level.
 */
@Serializable
data class Ticket(
    val id: Int,
    val title: String,
    val customer: Customer,
    val issueDescription: String,
    val employee: Employee,
    val express: Boolean = false,
) {
    val serviceLevel: ServiceLevel = customer.serviceLevel
}