package xelagurd.socialdating.client.data.model.details

import kotlinx.serialization.Serializable
import xelagurd.socialdating.client.data.model.additional.DefiningThemeReactionDetails

@Serializable
data class StatementDetails(
    val text: String,
    val definingThemes: List<DefiningThemeReactionDetails>,
    val creatorUserId: Int
)
