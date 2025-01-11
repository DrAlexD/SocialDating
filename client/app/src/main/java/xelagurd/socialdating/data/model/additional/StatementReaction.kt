package xelagurd.socialdating.data.model.additional

import kotlinx.serialization.Serializable
import xelagurd.socialdating.data.model.enums.StatementReactionType

@Serializable
data class StatementReaction(
    val userId: Int,
    val statementId: Int,
    val reactionType: StatementReactionType
)