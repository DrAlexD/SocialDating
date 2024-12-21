package xelagurd.socialdating.data.local

import javax.inject.Inject
import javax.inject.Singleton
import xelagurd.socialdating.data.model.Category

@Singleton
class LocalCategoriesRepository @Inject constructor(
    private val categoriesDao: CategoriesDao
) {
    suspend fun getCategories(): List<Category> = categoriesDao.getCategories()

    suspend fun insertCategory(category: Category) = categoriesDao.insertCategory(category)
}