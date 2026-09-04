package xelagurd.socialdating.client.data.model.details

import kotlinx.serialization.Serializable

@Serializable
data class StatementDetails(
    val text: String,
    val definingThemes: List<DefiningThemeReactionDetails>,
    val creatorUserId: Int
)
