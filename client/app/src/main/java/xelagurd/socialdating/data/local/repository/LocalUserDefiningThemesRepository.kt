package xelagurd.socialdating.data.local.repository

import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.Flow
import xelagurd.socialdating.data.local.dao.UserDefiningThemesDao
import xelagurd.socialdating.data.model.UserDefiningTheme
import xelagurd.socialdating.data.model.UserDefiningThemeWithData

@Singleton
class LocalUserDefiningThemesRepository @Inject constructor(
    private val userDefiningThemesDao: UserDefiningThemesDao
) {
    fun getUserDefiningThemes(userCategoryIds: List<Int>): Flow<List<UserDefiningThemeWithData>> =
        userDefiningThemesDao.getUserDefiningThemes(userCategoryIds)

    suspend fun insertUserDefiningThemes(userDefiningThemes: List<UserDefiningTheme>) =
        userDefiningThemesDao.insertUserDefiningThemes(userDefiningThemes)
}