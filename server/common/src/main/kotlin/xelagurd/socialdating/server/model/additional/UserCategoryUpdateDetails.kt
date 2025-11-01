package xelagurd.socialdating.server.model.additional

import xelagurd.socialdating.server.model.enums.StatementReactionType

data class UserCategoryUpdateDetails(
    val userId: Int,
    val categoryId: Int,
    val definingThemeId: Int,
    val reactionType: StatementReactionType,
    val isSupportDefiningTheme: Boolean
) {
    fun toUserDefiningThemeUpdateDetails() =
        UserDefiningThemeUpdateDetails(
            userId = userId,
            definingThemeId = definingThemeId,
            reactionType = reactionType,
            isSupportDefiningTheme = isSupportDefiningTheme
        )
}