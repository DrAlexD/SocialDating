package xelagurd.socialdating.client.data.model.details

import kotlinx.serialization.Serializable

@Serializable
data class DefiningThemeReactionDetails(
    val definingThemeId: Int,
    val isSupportDefiningTheme: Boolean
)
