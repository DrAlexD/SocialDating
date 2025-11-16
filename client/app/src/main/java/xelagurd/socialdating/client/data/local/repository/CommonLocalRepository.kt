package xelagurd.socialdating.client.data.local.repository

import javax.inject.Inject
import javax.inject.Singleton
import androidx.room.Transaction
import xelagurd.socialdating.client.data.local.dao.StatementsDao
import xelagurd.socialdating.client.data.local.dao.UserCategoriesDao
import xelagurd.socialdating.client.data.local.dao.UserDefiningThemesDao
import xelagurd.socialdating.client.data.local.dao.UsersDao

@Singleton
class CommonLocalRepository @Inject constructor(
    private val statementsDao: StatementsDao,
    private val userDefiningThemesDao: UserDefiningThemesDao,
    private val userCategoriesDao: UserCategoriesDao,
    private val usersDao: UsersDao
) {

    @Transaction
    suspend fun clearData() {
        statementsDao.deleteAll()
        userDefiningThemesDao.deleteAll()
        userCategoriesDao.deleteAll()
        usersDao.deleteAll()
    }

}