package xelagurd.socialdating.client.data.model.additional

import kotlinx.serialization.Serializable
import xelagurd.socialdating.client.data.model.DefaultDataProperties.ID_MIN
import xelagurd.socialdating.client.data.model.DefaultDataProperties.isValidId
import xelagurd.socialdating.client.data.model.enums.StatementReactionType

@Serializable
data class StatementReactionDetails(
    val userId: Int,
    val statementId: Int,
    val reactionType: StatementReactionType
) {
    init {
        require(userId.isValidId()) { "UserId must be at least $ID_MIN" }
        require(statementId.isValidId()) { "StatementId must be at least $ID_MIN" }
    }
}
