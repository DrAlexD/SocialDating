package xelagurd.socialdating.server.model.details

import jakarta.validation.Valid
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotEmpty
import jakarta.validation.constraints.Positive
import jakarta.validation.constraints.Size
import xelagurd.socialdating.server.model.DefaultDataProperties.LENGTH_MAX
import xelagurd.socialdating.server.model.Statement
import xelagurd.socialdating.server.model.StatementDefiningTheme
import xelagurd.socialdating.server.model.common.DefiningThemeReactionDetails

data class StatementDetails(
    @field:NotBlank
    @field:Size(max = LENGTH_MAX)
    val text: String,

    @field:NotEmpty
    @field:Valid
    val definingThemes: List<DefiningThemeReactionDetails>,

    @field:Positive
    val creatorUserId: Int
) {
    fun toStatement() =
        Statement(
            text = text,
            creatorUserId = creatorUserId
        )

    fun toStatementDefiningThemes(statementId: Int) =
        definingThemes.map {
            StatementDefiningTheme(
                statementId = statementId,
                definingThemeId = it.definingThemeId,
                isSupportDefiningTheme = it.isSupportDefiningTheme
            )
        }
}
