package xelagurd.socialdating.client.data.local.repository

import javax.inject.Inject
import javax.inject.Singleton
import xelagurd.socialdating.client.data.local.dao.UserCategoriesDao
import xelagurd.socialdating.client.data.model.UserCategory

@Singleton
class LocalUserCategoriesRepository @Inject constructor(
    private val userCategoriesDao: UserCategoriesDao
) {
    fun getUserCategories(userId: Int) =
        userCategoriesDao.getUserCategories(userId)

    suspend fun insertUserCategories(userCategories: List<UserCategory>) =
        userCategoriesDao.insertUserCategories(userCategories)
}