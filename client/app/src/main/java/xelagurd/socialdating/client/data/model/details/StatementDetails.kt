package xelagurd.socialdating.client.data.model.details

import kotlinx.serialization.Serializable

@Serializable
data class StatementDetails(
    val text: String = "",
    val isSupportDefiningTheme: Boolean? = null,
    val definingThemeId: Int? = null,
    val creatorUserId: Int? = null
) : FormDetails {
    val isValid
        get() = text.isNotBlank() && isSupportDefiningTheme != null && definingThemeId != null
                && creatorUserId != null
}