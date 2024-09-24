package team.team_trashcan.bot

import kotlinx.serialization.Serializable

@Serializable
data class Info(
    var field: String,
    var value: String,
)