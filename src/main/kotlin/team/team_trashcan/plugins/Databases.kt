package team.team_trashcan.plugins

import io.ktor.server.application.*
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction
import team.team_trashcan.data.CustomerTable
import team.team_trashcan.data.EmployeeTable
import team.team_trashcan.data.TicketTable

fun Application.configureDatabases() {
    val database = Database.connect(
        url = "jdbc:mariadb://localhost:3306/chatbot_db",
        driver = "org.mariadb.jdbc.Driver",
        user = "root",
        password = "password"
    )
    transaction(database) {
        SchemaUtils.create(CustomerTable, EmployeeTable, TicketTable)
    }
}