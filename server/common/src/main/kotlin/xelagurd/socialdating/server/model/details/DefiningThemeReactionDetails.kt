package xelagurd.socialdating.server.model.details

import jakarta.validation.constraints.Positive

data class DefiningThemeReactionDetails(
    @field:Positive
    val definingThemeId: Int,

    val isSupportDefiningTheme: Boolean
)
