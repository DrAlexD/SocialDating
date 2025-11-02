package xelagurd.socialdating.client.data.model.details

import kotlinx.serialization.Serializable

@Serializable
data class StatementDetails(
    val text: String,
    val isSupportDefiningTheme: Boolean,
    val definingThemeId: Int,
    val creatorUserId: Int
)