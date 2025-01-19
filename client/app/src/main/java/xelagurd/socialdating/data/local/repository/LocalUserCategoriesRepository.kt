package xelagurd.socialdating.data.local.repository

import javax.inject.Inject
import javax.inject.Singleton
import xelagurd.socialdating.data.local.dao.UserCategoriesDao
import xelagurd.socialdating.data.model.UserCategory

@Singleton
class LocalUserCategoriesRepository @Inject constructor(
    private val userCategoriesDao: UserCategoriesDao
) {
    fun getUserCategories() =
        userCategoriesDao.getUserCategories()

    fun getUserCategories(userId: Int) =
        userCategoriesDao.getUserCategories(userId)

    suspend fun insertUserCategories(userCategories: List<UserCategory>) =
        userCategoriesDao.insertUserCategories(userCategories)
}