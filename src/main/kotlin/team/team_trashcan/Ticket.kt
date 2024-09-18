package team.team_trashcan

import org.jetbrains.exposed.sql.Table



class Ticket : Table(){
    val Name= varchar("Name", 50)
    val Id = integer("Id").autoIncrement()
    val Kunde = integer("Customer")
    val Text= varchar("Description", length = 2000)
    val Mitarbeiter= integer("Employee").nullable()
    val Express = bool("Express")
    val Servicelevel = integer("Servicelevel")
    val Status = text("Status")

    override val primaryKey = PrimaryKey(Id)
}