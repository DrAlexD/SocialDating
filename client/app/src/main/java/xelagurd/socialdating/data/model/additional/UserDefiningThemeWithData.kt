package xelagurd.socialdating.data.model.additional

import kotlinx.serialization.Serializable
import xelagurd.socialdating.data.model.UserDefiningTheme

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
)