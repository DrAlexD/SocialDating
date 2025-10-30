package xelagurd.socialdating.server.model.details

import jakarta.persistence.Column
import jakarta.validation.constraints.Min
import xelagurd.socialdating.server.model.UserStatement
import xelagurd.socialdating.server.model.enums.StatementReactionType

data class UserStatementDetails(
    @field:Column(nullable = false)
    var reactionType: StatementReactionType,

    @field:Min(value = 1)
    var userId: Int,

    @field:Min(value = 1)
    var statementId: Int
) {
    fun toUserStatement(): UserStatement =
        UserStatement(
            reactionType = reactionType,
            userId = userId,
            statementId = statementId
        )
}