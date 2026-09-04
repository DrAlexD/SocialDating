package xelagurd.socialdating.server

import xelagurd.socialdating.server.model.Category
import xelagurd.socialdating.server.model.UserCategory
import xelagurd.socialdating.server.model.details.CategoryDetails
import xelagurd.socialdating.server.model.enums.AppLanguage.ENGLISH

object FakeCategoriesData {
    val categoriesDetails = listOf(
        CategoryDetails(nameEn = "RemoteCategory1", nameRu = "УдалённаяКатегория1"),
        CategoryDetails(nameEn = "RemoteCategory2", nameRu = "УдалённаяКатегория2"),
        CategoryDetails(nameEn = "RemoteCategory3", nameRu = "УдалённаяКатегория3"),
        CategoryDetails(nameEn = "RemoteCategory4", nameRu = "УдалённаяКатегория4"),
        CategoryDetails(nameEn = "RemoteCategory5", nameRu = "УдалённаяКатегория5")
    )

    val categories = listOf(
        Category(id = 1, nameEn = "RemoteCategory1", nameRu = "УдалённаяКатегория1", orderNumber = 1),
        Category(id = 2, nameEn = "RemoteCategory2", nameRu = "УдалённаяКатегория2", orderNumber = 2),
        Category(id = 3, nameEn = "RemoteCategory3", nameRu = "УдалённаяКатегория3", orderNumber = 3),
        Category(id = 4, nameEn = "RemoteCategory4", nameRu = "УдалённаяКатегория4", orderNumber = 4),
        Category(id = 5, nameEn = "RemoteCategory5", nameRu = "УдалённаяКатегория5", orderNumber = 5),
        Category(id = 6, nameEn = "RemoteCategory6", nameRu = "УдалённаяКатегория6", orderNumber = 6),
        Category(id = 7, nameEn = "RemoteCategory7", nameRu = "УдалённаяКатегория7", orderNumber = 7),
        Category(id = 8, nameEn = "RemoteCategory8", nameRu = "УдалённаяКатегория8", orderNumber = 8),
        Category(id = 9, nameEn = "RemoteCategory9", nameRu = "УдалённаяКатегория9", orderNumber = 9),
        Category(id = 10, nameEn = "RemoteCategory10", nameRu = "УдалённаяКатегория10", orderNumber = 10),
        Category(id = 11, nameEn = "RemoteCategory11", nameRu = "УдалённаяКатегория11", orderNumber = 11),
        Category(id = 12, nameEn = "RemoteCategory12", nameRu = "УдалённаяКатегория12", orderNumber = 12),
        Category(id = 13, nameEn = "RemoteCategory13", nameRu = "УдалённаяКатегория13", orderNumber = 13),
        Category(id = 14, nameEn = "RemoteCategory14", nameRu = "УдалённаяКатегория14", orderNumber = 14),
        Category(id = 15, nameEn = "RemoteCategory15", nameRu = "УдалённаяКатегория15", orderNumber = 15)
    )

    val categoryResponses = categories.map { it.toCategoryResponse(ENGLISH) }

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
        )
    )

    fun List<Category>.toCategoriesWithNullIds() =
        this.map {
            Category(nameEn = it.nameEn, nameRu = it.nameRu, orderNumber = it.orderNumber)
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

    fun List<Category>.filterByIds(categoryIds: List<Int>) =
        this.filter { it.id in categoryIds }

    fun List<UserCategory>.filterByUserId(userId: Int) =
        this.filter { it.userId == userId }

}