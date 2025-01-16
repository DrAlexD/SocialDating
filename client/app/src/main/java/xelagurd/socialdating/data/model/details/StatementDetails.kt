package xelagurd.socialdating.data.model.details

import kotlinx.serialization.Serializable

@Serializable
data class StatementDetails(
    val text: String = "",
    val isSupportDefiningTheme: Boolean? = null,
    val definingThemeId: Int? = null,
    val userId: Int? = null
) : FormDetails {
    val isValid
        get() = text.isNotBlank() && isSupportDefiningTheme != null && definingThemeId != null
                && userId != null
}