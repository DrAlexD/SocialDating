package xelagurd.socialdating.data.local.repository

import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.Flow
import xelagurd.socialdating.data.local.dao.UserCategoriesDao
import xelagurd.socialdating.data.model.UserCategory
import xelagurd.socialdating.data.model.UserCategoryWithData

@Singleton
class LocalUserCategoriesRepository @Inject constructor(
    private val userCategoriesDao: UserCategoriesDao
) {
    fun getUserCategories(userId: Int): Flow<List<UserCategoryWithData>> =
        userCategoriesDao.getUserCategories(userId)

    suspend fun insertUserCategories(userCategories: List<UserCategory>) =
        userCategoriesDao.insertUserCategories(userCategories)
}