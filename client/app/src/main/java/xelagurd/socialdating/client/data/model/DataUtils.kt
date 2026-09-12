package xelagurd.socialdating.client.data.model

import xelagurd.socialdating.client.data.model.additional.UserCategoryData
import xelagurd.socialdating.client.data.model.additional.UserDefiningThemeData

object DataUtils {

    const val TIMEOUT_MILLIS = 5_000L

    const val PAGE_SIZE = 30
    const val SIMILAR_USERS_PAGE_SIZE = 100

    // the number of the last entities which triggers loading of the next page when they become visible
    const val NEXT_PAGE_PREFETCH_COUNT = 10
    const val SIMILAR_USERS_NEXT_PAGE_PREFETCH_COUNT = 20

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
