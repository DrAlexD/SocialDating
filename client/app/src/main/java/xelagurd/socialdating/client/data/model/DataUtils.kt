package xelagurd.socialdating.client.data.model

import xelagurd.socialdating.client.data.model.additional.UserCategoryData
import xelagurd.socialdating.client.data.model.additional.UserDefiningThemeData

object DataUtils {

    const val TIMEOUT_MILLIS = 5_000L

    fun List<UserCategory>.toUserCategoriesData(categories: List<Category>): List<UserCategoryData> {
        val categoriesById = categories.associateBy { it.id }
        return this
            .mapNotNull { it.toUserCategoryData(categoriesById[it.categoryId]) }
            .sortedWith(compareBy({ it.categoryOrderNumber }, { it.categoryId }))
    }

    fun List<UserDefiningTheme>.toUserDefiningThemesData(definingThemes: List<DefiningTheme>): List<UserDefiningThemeData> {
        val definingThemesById = definingThemes.associateBy { it.id }
        return this
            .mapNotNull { it.toUserDefiningThemeData(definingThemesById[it.definingThemeId]) }
            .sortedWith(compareBy({ it.definingThemeOrderNumber }, { it.definingThemeNumberInCategory }))
    }
}
