package xelagurd.socialdating.data.fake

import xelagurd.socialdating.data.model.UserCategory
import xelagurd.socialdating.data.model.UserDefiningTheme
import xelagurd.socialdating.data.model.additional.UserCategoryWithData
import xelagurd.socialdating.data.model.additional.UserDefiningThemeWithData

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
        userCategoryId = userCategoryId,
        definingThemeId = definingThemeId,
        definingThemeName = "theme",
        definingThemeToOpinion = "Yes",
        definingThemeFromOpinion = "No"
    )

fun List<UserDefiningTheme>.toUserDefiningThemesWithData() =
    this.map { it.toUserDefiningThemeWithData() }