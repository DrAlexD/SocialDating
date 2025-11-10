package xelagurd.socialdating.server

import xelagurd.socialdating.server.model.Category
import xelagurd.socialdating.server.model.UserCategory
import xelagurd.socialdating.server.model.additional.UserCategoryUpdateDetails
import xelagurd.socialdating.server.model.details.CategoryDetails
import xelagurd.socialdating.server.model.enums.StatementReactionType.FULL_MAINTAIN

object FakeCategoriesData {
    val categoriesDetails = listOf(
        CategoryDetails(name = "RemoteCategory1"),
        CategoryDetails(name = "RemoteCategory2"),
        CategoryDetails(name = "RemoteCategory3"),
        CategoryDetails(name = "RemoteCategory4"),
        CategoryDetails(name = "RemoteCategory5")
    )

    val categories = listOf(
        Category(id = 1, name = "RemoteCategory1"),
        Category(id = 2, name = "RemoteCategory2"),
        Category(id = 3, name = "RemoteCategory3"),
        Category(id = 4, name = "RemoteCategory4"),
        Category(id = 5, name = "RemoteCategory5"),
        Category(id = 6, name = "RemoteCategory6"),
        Category(id = 7, name = "RemoteCategory7"),
        Category(id = 8, name = "RemoteCategory8"),
        Category(id = 9, name = "RemoteCategory9"),
        Category(id = 10, name = "RemoteCategory10"),
        Category(id = 11, name = "RemoteCategory11"),
        Category(id = 12, name = "RemoteCategory12"),
        Category(id = 13, name = "RemoteCategory13"),
        Category(id = 14, name = "RemoteCategory14"),
        Category(id = 15, name = "RemoteCategory15"),
        Category(id = 16, name = "RemoteCategory16"),
        Category(id = 17, name = "RemoteCategory17"),
        Category(id = 18, name = "RemoteCategory18"),
        Category(id = 19, name = "RemoteCategory19")
    )

    val userCategories = listOf(
        UserCategory(id = 1, interest = 75, userId = 1, categoryId = 1),
        UserCategory(id = 2, interest = 75, userId = 1, categoryId = 2),
        UserCategory(id = 3, interest = 75, userId = 1, categoryId = 3),
        UserCategory(id = 4, interest = 75, userId = 2, categoryId = 2)
    )

    val userCategoryUpdateDetails =
        UserCategoryUpdateDetails(
            userId = 1,
            categoryId = 1,
            definingThemeId = 1,
            reactionType = FULL_MAINTAIN,
            isSupportDefiningTheme = true
        )

    fun List<Category>.withNullIds() =
        this.map {
            Category(name = it.name)
        }

}