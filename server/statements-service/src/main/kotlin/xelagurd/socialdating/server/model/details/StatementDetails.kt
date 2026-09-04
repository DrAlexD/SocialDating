package xelagurd.socialdating.server.model.details

import jakarta.validation.Valid
import jakarta.validation.constraints.NotEmpty
import jakarta.validation.constraints.Positive
import xelagurd.socialdating.server.model.DefaultDataProperties.STATEMENT_TEXT_LENGTH_MAX
import xelagurd.socialdating.server.model.DefaultDataProperties.STATEMENT_TEXT_LENGTH_MIN
import xelagurd.socialdating.server.model.Statement
import xelagurd.socialdating.server.model.StatementDefiningTheme
import xelagurd.socialdating.server.utils.LocalizationUtils.englishOrNull
import xelagurd.socialdating.server.utils.LocalizationUtils.russianOrNull
import xelagurd.socialdating.server.validation.TrimmedSize

data class StatementDetails(
    @field:TrimmedSize(min = STATEMENT_TEXT_LENGTH_MIN, max = STATEMENT_TEXT_LENGTH_MAX)
    val text: String,

    @field:NotEmpty
    @field:Valid
    val definingThemes: List<DefiningThemeReactionDetails>,

    @field:Positive
    val creatorUserId: Int
) {
    fun toStatement() =
        Statement(
            textEn = englishOrNull(text),
            textRu = russianOrNull(text),
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
