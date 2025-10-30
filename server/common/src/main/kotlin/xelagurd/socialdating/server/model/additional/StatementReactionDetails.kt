package xelagurd.socialdating.server.model.additional

import xelagurd.socialdating.server.model.enums.StatementReactionType

data class StatementReactionDetails(
    val userOrUserCategoryId: Int,
    val categoryId: Int,
    val definingThemeId: Int,
    val statementId: Int,
    val reactionType: StatementReactionType,
    val isSupportDefiningTheme: Boolean
)