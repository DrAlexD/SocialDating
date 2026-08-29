package xelagurd.socialdating.client.data.model.additional

import kotlinx.serialization.Serializable

@Serializable
data class DefiningThemeReactionDetails(
    val definingThemeId: Int,
    val isSupportDefiningTheme: Boolean
)
