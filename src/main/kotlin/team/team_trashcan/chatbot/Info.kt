package team.team_trashcan.chatbot

import kotlinx.serialization.Serializable

@Serializable
data class Info(
    var field: String,
    var value: String,
)