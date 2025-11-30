package xelagurd.socialdating.server.model.additional

import jakarta.validation.constraints.Positive
import xelagurd.socialdating.server.model.UserStatement
import xelagurd.socialdating.server.model.common.UserCategoryUpdateDetails
import xelagurd.socialdating.server.model.enums.StatementReactionType

data class StatementReactionDetails(
    @field:Positive
    val userId: Int,

    @field:Positive
    val statementId: Int,

    @field:Positive
    val categoryId: Int,

    @field:Positive
    val definingThemeId: Int,

    val reactionType: StatementReactionType,

    val isSupportDefiningTheme: Boolean
) {
    fun toUserStatement() =
        UserStatement(
            reactionType = reactionType,
            userId = userId,
            statementId = statementId
        )

    fun toUserCategoryUpdateDetails() =
        UserCategoryUpdateDetails(
            userId = userId,
            categoryId = categoryId,
            definingThemeId = definingThemeId,
            reactionType = reactionType,
            isSupportDefiningTheme = isSupportDefiningTheme
        )
}