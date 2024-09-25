package team.trashcan.dialog

import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.server.testing.*
import plugins.configure
import kotlin.test.*

class ApplicationTest {
    @Test
    fun testRoot() = testApplication {
        val port = 55555
        application {
            configure(port)
        }
        client.get("/").apply {
            assertEquals(HttpStatusCode.OK, status)
            assertEquals("Hello World!", bodyAsText())
        }
    }
}
