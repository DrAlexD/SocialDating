package xelagurd.socialdating.client.data.model.additional

import xelagurd.socialdating.client.data.model.DataEntity

data class UserDefiningThemeData(
    override val id: Int,
    val value: Int,
    val interest: Int,
    val categoryId: Int,
    val definingThemeId: Int,
    val definingThemeName: String,
    val definingThemeFromOpinion: String,
    val definingThemeToOpinion: String,
    val definingThemeNumberInCategory: Int,
    val definingThemeOrderNumber: Int
) : DataEntity