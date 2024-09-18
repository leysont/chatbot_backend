import java.sql.Connection
import java.sql.DriverManager
import java.sql.SQLException
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.selectAll

fun connectToDatabase() {
    Database.connect(
        url = "0.0.0.0:55066:53099",
        //driver = "org.mariadb.jdbc.Driver",
        user = "root",
        password = "chatbot"
    )



fun queryDatabase() {
    transaction {
        Customer.selectAll().forEach {
            println("${it[Customer.Id]}: ${it[Customer.Name]}, ${it[Customer.Servicelevel]}")
        }
    }
}