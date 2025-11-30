package xelagurd.socialdating.client.data.local.repository

import javax.inject.Inject
import javax.inject.Singleton
import androidx.room.Transaction
import xelagurd.socialdating.client.data.fake.FakeData
import xelagurd.socialdating.client.data.local.dao.CategoriesDao
import xelagurd.socialdating.client.data.local.dao.DefiningThemesDao
import xelagurd.socialdating.client.data.local.dao.StatementsDao
import xelagurd.socialdating.client.data.local.dao.UserCategoriesDao
import xelagurd.socialdating.client.data.local.dao.UserDefiningThemesDao
import xelagurd.socialdating.client.data.local.dao.UsersDao

@Singleton
class CommonLocalRepository @Inject constructor(
    private val usersDao: UsersDao,
    private val categoriesDao: CategoriesDao,
    private val definingThemesDao: DefiningThemesDao,
    private val userCategoriesDao: UserCategoriesDao,
    private val userDefiningThemesDao: UserDefiningThemesDao,
    private val statementsDao: StatementsDao
) {

    @Transaction
    suspend fun clearData() {
        statementsDao.deleteAll()
        userDefiningThemesDao.deleteAll()
        userCategoriesDao.deleteAll()
        definingThemesDao.deleteAll() // FixMe: remove after adding server hosting
        categoriesDao.deleteAll() // FixMe: remove after adding server hosting
        usersDao.deleteAll()
    }

    @Transaction
    suspend fun initOfflineModeData() { // FixMe: remove after adding server hosting
        usersDao.insertUser(FakeData.users[0])
        categoriesDao.insertCategories(FakeData.categories)
        definingThemesDao.insertDefiningThemes(FakeData.definingThemes)
        userCategoriesDao.insertUserCategories(FakeData.userCategories)
        userDefiningThemesDao.insertUserDefiningThemes(FakeData.userDefiningThemes)
        statementsDao.insertStatements(FakeData.statements)
    }
}