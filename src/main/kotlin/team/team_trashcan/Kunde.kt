package team.team_trashcan

import kotlinx.serialization.Serializable
import org.jetbrains.exposed.sql.Table.Dual.integer
import org.jetbrains.exposed.sql.Table.Dual.varchar

@Serializable
class Kunde {
    val Id : Int? = null
    val Name : String? = null
    val Servicelevel = 1
    val Notizen : String? = null
}