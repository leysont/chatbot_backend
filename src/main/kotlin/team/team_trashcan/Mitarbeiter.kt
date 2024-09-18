package team.team_trashcan

import org.jetbrains.exposed.sql.Table


class Employee : Table(){
    val Id = integer("Id").autoIncrement()
    val Vorname = varchar("Firstname", length = 50)
    val Nachname = varchar("Lastname", length = 50)
    val Themenschwerpunkte= varchar("Topics", length = 200)

    override val primaryKey = PrimaryKey(Id)
}