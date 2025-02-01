package xelagurd.socialdating.server.model.details

import xelagurd.socialdating.server.model.additional.StatementReaction
import xelagurd.socialdating.server.model.enums.StatementReactionType

data class StatementReactionDetails(
    val userId: Int,
    val categoryId: Int,
    val reactionType: StatementReactionType
) {
    fun toStatementReaction(definingThemeId: Int, isSupportDefiningTheme: Boolean) =
        StatementReaction(
            userOrUserCategoryId = userId,
            categoryId = categoryId,
            definingThemeId = definingThemeId,
            reactionType = reactionType,
            isSupportDefiningTheme = isSupportDefiningTheme
        )
}