package team.team_trashcan

import org.jetbrains.exposed.sql.Table

class Customer : Table() {
    val Id = integer("Id").autoIncrement()
    val Name = varchar("Name", length = 50)
    val Servicelevel = integer("Servicelevel")
    val Notizen = varchar("Notes", length = 1000)
    override val primaryKey = PrimaryKey(Id)
}