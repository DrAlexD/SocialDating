package xelagurd.socialdating.server.model.common

import xelagurd.socialdating.server.model.enums.StatementReactionType

data class UserDefiningThemesUpdateDetails(
    val userId: Int,
    val reactionType: StatementReactionType,
    val definingThemes: List<DefiningThemeReactionDetails>
)
