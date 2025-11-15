package xelagurd.socialdating.server

import xelagurd.socialdating.server.model.Category
import xelagurd.socialdating.server.model.UserCategory
import xelagurd.socialdating.server.model.details.CategoryDetails

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
        Category(id = 15, name = "RemoteCategory15")
    )

    val userCategories = listOf(
        UserCategory(
            id = 1, interest = 50, userId = 1, categoryId = 1, maintained = arrayOf(3)
        ),
        UserCategory(
            id = 2, interest = 25, userId = 1, categoryId = 2, notMaintained = arrayOf(4)
        ),
        UserCategory(
            id = 3, interest = 25, userId = 1, categoryId = 3, notMaintained = arrayOf(1)
        ),
        UserCategory(
            id = 4, interest = 50, userId = 2, categoryId = 1, maintained = arrayOf(3)
        ),
        UserCategory(
            id = 5, interest = 25, userId = 2, categoryId = 2, maintained = arrayOf(4)
        ),
        UserCategory(
            id = 6, interest = 25, userId = 2, categoryId = 3, notMaintained = arrayOf(1)
        ),
        UserCategory(
            id = 7, interest = 50, userId = 3, categoryId = 1, maintained = arrayOf(1), notMaintained = arrayOf(2)
        ),
        UserCategory(
            id = 8, interest = 25, userId = 3, categoryId = 2, notMaintained = arrayOf(4)
        ),
        UserCategory(
            id = 9, interest = 25, userId = 3, categoryId = 3, maintained = arrayOf(1)
        ),
    )

    fun List<Category>.toCategoriesWithNullIds() =
        this.map {
            Category(name = it.name)
        }

    fun List<UserCategory>.toUserCategoriesWithNullIds() =
        this.map {
            UserCategory(
                interest = it.interest,
                userId = it.userId,
                categoryId = it.categoryId,
                maintained = it.maintained,
                notMaintained = it.notMaintained
            )
        }
}