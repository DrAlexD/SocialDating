package xelagurd.socialdating.server.model.details

import jakarta.validation.constraints.Positive
import xelagurd.socialdating.server.model.UserStatement
import xelagurd.socialdating.server.model.enums.StatementReactionType

data class UserStatementDetails(
    var reactionType: StatementReactionType,

    @field:Positive
    var userId: Int,

    @field:Positive
    var statementId: Int
) {
    fun toUserStatement() =
        UserStatement(
            reactionType = reactionType,
            userId = userId,
            statementId = statementId
        )
}