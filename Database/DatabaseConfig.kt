import org.jetbrains.exposed.sql.Database

object DatabaseConfig {
    fun connect() {
        Database.connect(
            url = "jdbc:mysql://localhost:3306/deine_datenbank",
            driver = "com.mysql.cj.jdbc.Driver",
            user = "root",
            password = "dein_passwort"
        )
    }
}