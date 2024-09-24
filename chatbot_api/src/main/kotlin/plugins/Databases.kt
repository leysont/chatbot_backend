package plugins

import data.CustomerTable
import data.EmployeeTable
import data.TicketTable
import io.ktor.server.application.*
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction

fun Application.configureDatabase() {

    // Add database connection
    // (as defined in docker-compose.yml)
    val database = Database.connect(
        url = "jdbc:mariadb://localhost:3306/chatbot_db",
        driver = "org.mariadb.jdbc.Driver",
        user = "root",
        password = "password"
    )

    // Create tables
    transaction(database) {
        SchemaUtils.create(CustomerTable, EmployeeTable, TicketTable)
    }
}