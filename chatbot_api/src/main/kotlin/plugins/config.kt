package plugins

import io.github.smiley4.ktorswaggerui.SwaggerUI
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.json
import io.ktor.server.application.*
import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.plugins.contentnegotiation.ContentNegotiation
import io.ktor.server.plugins.openapi.openAPI
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.webjars.*

fun Application.configure(port: Int) {

    // Configure serialization
    install(ContentNegotiation) {
        json()
    }

    // Configure Webjars
    install(Webjars) {
        path = "/webjars" //defaults to /webjars
    }
    routing {
        get("/webjars") {
            call.respondText("<script src='/webjars/jquery/jquery.js'></script>", ContentType.Text.Html)
        }
    }

    // Configure Swagger/OpenAPI
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
            url = "http://localhost:$port"
            description = "Development Server"
        }
    }
    routing {
        openAPI(path = "openapi")
    }

    // Ignore trailing slashes
    install(IgnoreTrailingSlash)

}
