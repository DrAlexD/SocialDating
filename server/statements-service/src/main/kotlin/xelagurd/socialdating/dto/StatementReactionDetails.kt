package xelagurd.socialdating.dto

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