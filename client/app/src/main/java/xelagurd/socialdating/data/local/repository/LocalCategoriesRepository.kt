package xelagurd.socialdating.data.local.repository

import javax.inject.Inject
import javax.inject.Singleton
import xelagurd.socialdating.data.local.dao.CategoriesDao
import xelagurd.socialdating.data.model.Category

@Singleton
class LocalCategoriesRepository @Inject constructor(
    private val categoriesDao: CategoriesDao
) {
    fun getCategories() =
        categoriesDao.getCategories()

    suspend fun insertCategories(categories: List<Category>) =
        categoriesDao.insertCategories(categories)
}