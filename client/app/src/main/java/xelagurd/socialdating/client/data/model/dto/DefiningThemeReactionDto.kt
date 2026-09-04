package xelagurd.socialdating.client.data.model.dto

import kotlinx.serialization.Serializable

@Serializable
data class DefiningThemeReactionDto(
    val definingThemeId: Int,
    val isSupportDefiningTheme: Boolean
)
