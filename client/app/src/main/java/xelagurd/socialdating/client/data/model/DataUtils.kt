package xelagurd.socialdating.client.data.model

import xelagurd.socialdating.client.data.model.additional.SimilarUser
import xelagurd.socialdating.client.data.model.ui.SimilarUserWithData
import xelagurd.socialdating.client.data.model.ui.UserCategoryWithData
import xelagurd.socialdating.client.data.model.ui.UserDefiningThemeWithData

object DataUtils {

    const val TIMEOUT_MILLIS = 5_000L

    fun List<UserCategory>.toUserCategoriesWithData(categories: List<Category>): List<UserCategoryWithData> {
        val categoriesById = categories.associateBy { it.id }
        return this.mapNotNull { it.toUserCategoryWithData(categoriesById[it.categoryId]) }
    }

    fun List<UserDefiningTheme>.toUserDefiningThemesWithData(definingThemes: List<DefiningTheme>): List<UserDefiningThemeWithData> {
        val definingThemesById = definingThemes.associateBy { it.id }
        return this.mapNotNull { it.toUserDefiningThemeWithData(definingThemesById[it.definingThemeId]) }
    }

    fun List<SimilarUser>.toSimilarUsersWithData(users: List<User>): List<SimilarUserWithData> {
        val usersById = users.associateBy { it.id }
        return this.mapNotNull { it.toSimilarUserWithData(usersById[it.id]) }
    }
}
