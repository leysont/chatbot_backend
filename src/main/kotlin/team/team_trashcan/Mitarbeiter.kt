package team.team_trashcan

import org.jetbrains.exposed.sql.Table.Dual.varchar

class Mitarbeiter {
    val Vorname = varchar("Vorname",32);
    val Nachname = varchar("Nachname",32);
    val Themenschwerpunkte = varchar("Themenschwerpunkte", 1000);
}