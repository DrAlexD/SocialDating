package xelagurd.socialdating.data.local.repository

import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.Flow
import xelagurd.socialdating.data.local.dao.CategoriesDao
import xelagurd.socialdating.data.model.Category

@Singleton
class LocalCategoriesRepository @Inject constructor(
    private val categoriesDao: CategoriesDao
) {
    fun getCategories(): Flow<List<Category>> = categoriesDao.getCategories()

    suspend fun insertCategories(categories: List<Category>) =
        categoriesDao.insertCategories(categories)
}