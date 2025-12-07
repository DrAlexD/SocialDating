package xelagurd.socialdating.client.data.local.repository

import javax.inject.Inject
import javax.inject.Singleton
import xelagurd.socialdating.client.data.local.dao.CategoriesDao
import xelagurd.socialdating.client.data.model.Category

@Singleton
class LocalCategoriesRepository @Inject constructor(
    private val categoriesDao: CategoriesDao
) {
    fun getCategories() =
        categoriesDao.getCategories()

    suspend fun insertCategories(categories: List<Category>) =
        categoriesDao.insertCategories(categories)

    suspend fun deleteAll() =
        categoriesDao.deleteAll()
}