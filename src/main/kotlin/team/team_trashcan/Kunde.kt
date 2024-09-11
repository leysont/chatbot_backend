package team.team_trashcan

import org.jetbrains.exposed.sql.Table.Dual.integer
import org.jetbrains.exposed.sql.Table.Dual.varchar


class Kunde {
    val Id = integer("IdKunde")
    val Name = varchar("Name", 32);
    val Servicelevel = integer("Servicelevel");
    val Notizen = varchar("Notizen", 1000);

    //override val primaryKey = PrimaryKey(Id)
}