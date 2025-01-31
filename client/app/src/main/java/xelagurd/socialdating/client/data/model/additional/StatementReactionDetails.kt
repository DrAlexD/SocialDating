package xelagurd.socialdating.client.data.model.additional

import kotlinx.serialization.Serializable
import xelagurd.socialdating.client.data.model.enums.StatementReactionType

@Serializable
data class StatementReactionDetails(
    val userId: Int,
    val categoryId: Int,
    val reactionType: StatementReactionType
)