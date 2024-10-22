package models_dialog

import kotlinx.serialization.Serializable

/**
 * Represents a customer in the system with an ID, name, service level, and notes.
 *
 * @property serverId The unique identifier for the customer.
 * @property supporterRoleID The Roleid of the supporter at the discord server
 */
@Serializable
data class Server (
    val serverId: Int? = null,
    val supporterRoleID: Int? = null,
)