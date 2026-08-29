package xelagurd.socialdating.client.data.model.additional

import kotlinx.serialization.Serializable
import xelagurd.socialdating.client.data.model.Statement
import xelagurd.socialdating.client.data.model.StatementDefiningTheme

@Serializable
data class StatementWithDefiningThemes(
    val id: Int,
    val text: String,
    val creatorUserId: Int,
    val definingThemes: List<DefiningThemeReactionDetails>
) {
    fun toStatement() =
        Statement(
            id = id,
            text = text,
            creatorUserId = creatorUserId
        )

    fun toStatementDefiningThemes() =
        definingThemes.map {
            StatementDefiningTheme(
                statementId = id,
                definingThemeId = it.definingThemeId
            )
        }
}
