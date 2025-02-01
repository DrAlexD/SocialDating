package xelagurd.socialdating.common.dto

data class StatementReaction(
    val userOrUserCategoryId: Int,
    val categoryId: Int,
    val definingThemeId: Int,
    val reactionType: StatementReactionType,
    val isSupportDefiningTheme: Boolean
)