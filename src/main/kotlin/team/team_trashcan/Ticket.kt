package team.team_trashcan

import kotlinx.serialization.Serializable
import org.jetbrains.exposed.sql.Table.Dual.varchar
import org.jetbrains.exposed.sql.booleanParam
import team.team_trashcan.plugins.UserService.Users.integer

@Serializable
class Ticket {
    val Name: String? = null
    val Id: Int? = null
    val Kunde: Kunde? = null
    val Text: String? = null
    val Mitarbeiter: Mitarbeiter? = null
    val Express = false
    val Servicelevel = Kunde?.Servicelevel
}