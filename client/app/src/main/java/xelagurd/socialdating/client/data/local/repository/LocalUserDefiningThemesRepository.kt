package xelagurd.socialdating.client.data.local.repository

import javax.inject.Inject
import javax.inject.Singleton
import xelagurd.socialdating.client.data.local.dao.UserDefiningThemesDao
import xelagurd.socialdating.client.data.model.UserDefiningTheme

@Singleton
class LocalUserDefiningThemesRepository @Inject constructor(
    private val userDefiningThemesDao: UserDefiningThemesDao
) {
    fun getUserDefiningThemes() =
        userDefiningThemesDao.getUserDefiningThemes()

    fun getUserDefiningThemes(userCategoryIds: List<Int>) =
        userDefiningThemesDao.getUserDefiningThemes(userCategoryIds)

    suspend fun insertUserDefiningThemes(userDefiningThemes: List<UserDefiningTheme>) =
        userDefiningThemesDao.insertUserDefiningThemes(userDefiningThemes)
}