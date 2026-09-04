package xelagurd.socialdating.server.model.details

import jakarta.validation.constraints.Positive
import xelagurd.socialdating.server.model.UserStatement
import xelagurd.socialdating.server.model.enums.StatementReactionType

data class StatementReactionDetails(
    @field:Positive
    val userId: Int,

    @field:Positive
    val statementId: Int,

    val reactionType: StatementReactionType
) {
    fun toUserStatement() =
        UserStatement(
            reactionType = reactionType,
            userId = userId,
            statementId = statementId
        )
}
