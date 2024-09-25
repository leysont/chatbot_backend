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
//    val database = Database.connect(
////        url = "jdbc:mariadb://localhost:3306/support_data",
//        url = "jdbc:mariadb://storage.database:3306/support_data",
//        driver = "org.mariadb.jdbc.Driver",
//        user = "root",
//        password = "password"
//    )
    val database = connectToDatabase()

    // Create tables
    transaction(database) {
        SchemaUtils.create(CustomerTable, EmployeeTable, TicketTable)
    }
}

fun connectToDatabase(): Database? {
    val maxRetries = 5
    var attempts = 0
    var database: Database? = null

    while (attempts < maxRetries && database == null) {
        try {
            // Attempt to connect to the database
            val connection = Database.connect(
                url = "jdbc:mariadb://storage.database:3306/support_data",
                driver = "org.mariadb.jdbc.Driver",
                user = "adira",
                password = "adiras_secret"
            )
            database = connection
        } catch (e: Exception) {
            attempts++
            println("Connection attempt $attempts failed. Retrying...")
            Thread.sleep(2000) // Wait before retrying
        }
    }

    if (database == null) {
        println("Failed to connect to the database after $maxRetries attempts.")
        // Handle the failure (e.g., exit the application, raise an alert, etc.)
    }

    return database
}