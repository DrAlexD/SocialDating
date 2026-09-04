package xelagurd.socialdating.client.data.model.details

import kotlinx.serialization.Serializable
import xelagurd.socialdating.client.data.model.enums.StatementReactionType

@Serializable
data class StatementReactionDetails(
    val userId: Int,
    val statementId: Int,
    val reactionType: StatementReactionType
)
