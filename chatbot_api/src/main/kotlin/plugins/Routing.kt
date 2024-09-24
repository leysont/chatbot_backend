package plugins

import io.github.smiley4.ktorswaggerui.SwaggerUI
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.webjars.*

fun Application.configureRouting() {
    install(Webjars) {
        path = "/webjars" //defaults to /webjars
    }
    install(IgnoreTrailingSlash)
    install(SwaggerUI) {
        swagger {
            swaggerUrl = "swagger-ui"
            forwardRoot = true
        }
        info {
            title = "Chatbot Backend API"
            version = "latest"
            description = "API for interacting with the database behind the support chatbot."
        }
        server {
            url = "http://localhost:55555"
            description = "Development Server"
        }
    }
    routing {
        get("/") {
            call.respondText("hello yes this is api")
        }
        get("/webjars") {
            call.respondText("<script src='/webjars/jquery/jquery.js'></script>", ContentType.Text.Html)
        }
    }
}
