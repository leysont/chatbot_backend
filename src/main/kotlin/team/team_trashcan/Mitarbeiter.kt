package team.team_trashcan

import kotlinx.serialization.Serializable
import org.jetbrains.exposed.sql.Table.Dual.varchar

@Serializable
class Mitarbeiter {
    val Id : Int? = null
    val Vorname: String? = null
    val Nachname: String? = null
    val Themenschwerpunkte: String? = null
}