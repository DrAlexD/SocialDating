package xelagurd.socialdating.client.data.fake

import xelagurd.socialdating.client.data.fake.FakeData.categories
import xelagurd.socialdating.client.data.fake.FakeData.definingThemes
import xelagurd.socialdating.client.data.fake.FakeData.users
import xelagurd.socialdating.client.data.model.Category
import xelagurd.socialdating.client.data.model.DefiningTheme
import xelagurd.socialdating.client.data.model.UserCategory
import xelagurd.socialdating.client.data.model.UserDefiningTheme
import xelagurd.socialdating.client.data.model.additional.SimilarUser
import xelagurd.socialdating.client.data.model.ui.UserCategoryWithData
import xelagurd.socialdating.client.data.model.ui.UserDefiningThemeWithData

fun UserCategory.toUserCategoryWithData(category: Category?) =
    category?.let {
        UserCategoryWithData(
            id = id,
            interest = interest,
            userId = userId,
            categoryId = categoryId,
            categoryName = category.name
        )
    }

fun List<UserCategory>.toUserCategoriesWithData() =
    this.mapNotNull { userCategory ->
        userCategory.toUserCategoryWithData(
            categories.firstOrNull { it.id == userCategory.categoryId }
        )
    }

fun UserDefiningTheme.toUserDefiningThemeWithData(definingTheme: DefiningTheme?) =
    definingTheme?.let {
        UserDefiningThemeWithData(
            id = id,
            value = value,
            interest = interest,
            categoryId = definingTheme.categoryId,
            definingThemeId = definingThemeId,
            definingThemeName = definingTheme.name,
            definingThemeFromOpinion = definingTheme.fromOpinion,
            definingThemeToOpinion = definingTheme.toOpinion,
            definingThemeNumberInCategory = definingTheme.numberInCategory
        )
    }

fun List<UserDefiningTheme>.toUserDefiningThemesWithData() =
    this.mapNotNull { userDefiningTheme ->
        userDefiningTheme.toUserDefiningThemeWithData(
            definingThemes.firstOrNull { it.id == userDefiningTheme.definingThemeId }
        )
    }

fun List<SimilarUser>.toSimilarUsersWithData() =
    this.mapNotNull { similarUser ->
        similarUser.toSimilarUserWithData(
            users.firstOrNull { it.id == similarUser.id }
        )
    }