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
        Users.selectAll().forEach {
            println("${it[Users.id]}: ${it[Users.name]}, ${it[Users.age]}")
        }
    }
}