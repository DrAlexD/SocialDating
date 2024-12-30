package xelagurd.socialdating.data.remote.repository

import javax.inject.Inject
import javax.inject.Singleton
import xelagurd.socialdating.data.model.UserDefiningTheme
import xelagurd.socialdating.data.remote.ApiService

@Singleton
class RemoteUserDefiningThemesRepository @Inject constructor(
    private val apiService: ApiService
) {
    suspend fun getUserDefiningThemes(userCategoryIds: List<Int>): List<UserDefiningTheme> =
        apiService.getUserDefiningThemes(userCategoryIds)
}