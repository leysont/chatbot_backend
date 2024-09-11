package team.team_trashcan

import org.jetbrains.exposed.sql.Table.Dual.varchar
import org.jetbrains.exposed.sql.booleanParam
import team.team_trashcan.plugins.UserService.Users.integer

class Ticket {
    val Name = varchar("Name", 255);
    val Id = integer ("ID");
    val Kunde = integer ("KundenID");
    val Text = varchar("Text", 1000);
    val Mitarbeiter = integer ("MitarbeiterID");
    val Express = booleanParam(false);
    val Servicelevel = integer ("Servicelevel");
}