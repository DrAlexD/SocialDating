package xelagurd.socialdating.client.data.fake

import xelagurd.socialdating.client.data.model.UserCategory
import xelagurd.socialdating.client.data.model.UserDefiningTheme
import xelagurd.socialdating.client.data.model.additional.UserCategoryWithData
import xelagurd.socialdating.client.data.model.additional.UserDefiningThemeWithData

fun UserCategory.toUserCategoryWithData() =
    UserCategoryWithData(
        id = id,
        interest = interest,
        userId = userId,
        categoryId = categoryId,
        categoryName = "category"
    )

fun List<UserCategory>.toUserCategoriesWithData() = this.map { it.toUserCategoryWithData() }

fun UserDefiningTheme.toUserDefiningThemeWithData() =
    UserDefiningThemeWithData(
        id = id,
        value = value,
        interest = interest,
        categoryId = 1,
        definingThemeId = definingThemeId,
        definingThemeName = "theme",
        definingThemeToOpinion = "Yes",
        definingThemeFromOpinion = "No"
    )

fun List<UserDefiningTheme>.toUserDefiningThemesWithData() =
    this.map { it.toUserDefiningThemeWithData() }