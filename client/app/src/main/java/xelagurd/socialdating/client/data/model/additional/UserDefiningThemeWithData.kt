package xelagurd.socialdating.client.data.model.additional

import kotlinx.serialization.Serializable
import xelagurd.socialdating.client.data.model.DataEntity

@Serializable
data class UserDefiningThemeWithData(
    override val id: Int,
    val value: Int,
    val interest: Int,
    val userCategoryId: Int,
    val definingThemeId: Int,
    val definingThemeName: String,
    val definingThemeFromOpinion: String,
    val definingThemeToOpinion: String
) : DataEntity