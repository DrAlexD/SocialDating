package xelagurd.socialdating.client.data.model.ui

import xelagurd.socialdating.client.data.model.DataEntity

data class UserDefiningThemeWithData(
    override val id: Int,
    val value: Int,
    val interest: Int,
    val categoryId: Int,
    val definingThemeId: Int,
    val definingThemeName: String,
    val definingThemeFromOpinion: String,
    val definingThemeToOpinion: String
) : DataEntity