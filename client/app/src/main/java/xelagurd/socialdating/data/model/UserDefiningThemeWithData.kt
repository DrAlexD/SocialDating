package xelagurd.socialdating.data.model

import kotlinx.serialization.Serializable

@Serializable
data class UserDefiningThemeWithData(
    val id: Int,
    val value: Int,
    val interest: Int,
    val userCategoryId: Int,
    val definingThemeId: Int,
    val definingThemeName: String,
    val definingThemeFromOpinion: String,
    val definingThemeToOpinion: String
) {
    fun toUserDefiningTheme() =
        UserDefiningTheme(
            id = id,
            value = value,
            interest = interest,
            userCategoryId = userCategoryId,
            definingThemeId = definingThemeId
        )
}