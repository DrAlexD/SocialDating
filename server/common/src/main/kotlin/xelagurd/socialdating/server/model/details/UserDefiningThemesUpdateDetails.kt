package xelagurd.socialdating.server.model.details

import jakarta.validation.Valid
import jakarta.validation.constraints.NotEmpty
import jakarta.validation.constraints.Positive
import xelagurd.socialdating.server.model.enums.StatementReactionType

data class UserDefiningThemesUpdateDetails(
    @field:Positive
    val userId: Int,

    val reactionType: StatementReactionType,

    @field:NotEmpty
    @field:Valid
    val definingThemes: List<DefiningThemeReactionDetails>
)
