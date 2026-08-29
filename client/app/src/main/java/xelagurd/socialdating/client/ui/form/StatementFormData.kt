package xelagurd.socialdating.client.ui.form

import xelagurd.socialdating.client.data.model.additional.DefiningThemeReactionDetails
import xelagurd.socialdating.client.data.model.details.StatementDetails

data class StatementFormData(
    val text: String = "",
    val definingThemes: Map<Int, Boolean?> = mapOf(),
    val creatorUserId: Int? = null
) : FormData {
    val isValid
        get() = text.isNotBlank() && definingThemes.isNotEmpty()
                && definingThemes.values.all { it != null } && creatorUserId != null

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
