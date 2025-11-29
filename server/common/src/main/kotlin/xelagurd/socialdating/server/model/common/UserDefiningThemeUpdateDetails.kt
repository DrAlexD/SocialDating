package xelagurd.socialdating.server.model.common

import xelagurd.socialdating.server.model.enums.StatementReactionType

data class UserDefiningThemeUpdateDetails(
    val userId: Int,
    val definingThemeId: Int,
    val reactionType: StatementReactionType,
    val isSupportDefiningTheme: Boolean
)