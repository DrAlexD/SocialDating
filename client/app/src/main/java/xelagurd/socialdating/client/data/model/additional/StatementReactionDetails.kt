package xelagurd.socialdating.client.data.model.additional

import kotlinx.serialization.Serializable
import xelagurd.socialdating.client.data.model.enums.StatementReactionType

@Serializable
data class StatementReactionDetails(
    val userId: Int,
    val statementId: Int,
    val categoryId: Int,
    val definingThemeId: Int,
    val reactionType: StatementReactionType,
    val isSupportDefiningTheme: Boolean
)