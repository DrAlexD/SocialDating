package xelagurd.socialdating.client.ui.form

import xelagurd.socialdating.client.data.model.details.StatementDetails

data class StatementFormData(
    val text: String = "",
    val isSupportDefiningTheme: Boolean? = null,
    val definingThemeId: Int? = null,
    val creatorUserId: Int? = null
) : FormData {
    val isValid
        get() = text.isNotBlank() && isSupportDefiningTheme != null && definingThemeId != null
                && creatorUserId != null

    fun toStatementDetails() =
        StatementDetails(
            text = text,
            isSupportDefiningTheme = isSupportDefiningTheme!!,
            definingThemeId = definingThemeId!!,
            creatorUserId = creatorUserId!!
        )
}