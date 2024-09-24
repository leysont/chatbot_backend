plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "0.8.0"
}

rootProject.name = "chatbot_backend"

include("chatbot_bot")
include("chatbot_api")
