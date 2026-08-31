package xelagurd.socialdating.client.ui.form

import xelagurd.socialdating.client.data.model.DefaultDataProperties.STATEMENT_TEXT_LENGTH_MAX
import xelagurd.socialdating.client.data.model.DefaultDataProperties.STATEMENT_TEXT_LENGTH_MIN
import xelagurd.socialdating.client.data.model.DefaultDataProperties.isValidId
import xelagurd.socialdating.client.data.model.DefaultDataProperties.isValidText
import xelagurd.socialdating.client.data.model.additional.DefiningThemeReactionDetails
import xelagurd.socialdating.client.data.model.details.StatementDetails

data class StatementFormData(
    val text: String = "",
    val definingThemes: Map<Int, Boolean?> = mapOf(),
    val creatorUserId: Int? = null
) : FormData {
    val isValid
        get() = text.isValidText(STATEMENT_TEXT_LENGTH_MIN, STATEMENT_TEXT_LENGTH_MAX) && definingThemes.isNotEmpty()
                && definingThemes.values.all { it != null } && definingThemes.keys.all { it.isValidId() }
                && creatorUserId != null && creatorUserId.isValidId()

    fun toggleDefiningTheme(definingThemeId: Int) =
        copy(
            definingThemes = when {
                definingThemes.containsKey(definingThemeId) -> definingThemes - definingThemeId
                else -> definingThemes + (definingThemeId to null)
            }
        )

    fun updateDefiningThemeOpinion(definingThemeId: Int, isSupportDefiningTheme: Boolean) =
        copy(definingThemes = definingThemes + (definingThemeId to isSupportDefiningTheme))

    fun toStatementDetails() =
        StatementDetails(
            text = text,
            definingThemes = definingThemes.map { (definingThemeId, isSupportDefiningTheme) ->
                DefiningThemeReactionDetails(
                    definingThemeId = definingThemeId,
                    isSupportDefiningTheme = isSupportDefiningTheme!!
                )
            },
            creatorUserId = creatorUserId!!
        )
}
