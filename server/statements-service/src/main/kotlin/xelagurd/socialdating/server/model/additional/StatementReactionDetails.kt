package xelagurd.socialdating.server.model.additional

import xelagurd.socialdating.server.model.enums.StatementReactionType

data class StatementReactionDetails(
    val userId: Int,
    val statementId: Int,
    val categoryId: Int,
    val definingThemeId: Int,
    val reactionType: StatementReactionType,
    val isSupportDefiningTheme: Boolean
) {
    fun toUserCategoryUpdateDetails() =
        UserCategoryUpdateDetails(
            userId = userId,
            categoryId = categoryId,
            definingThemeId = definingThemeId,
            reactionType = reactionType,
            isSupportDefiningTheme = isSupportDefiningTheme
        )
}