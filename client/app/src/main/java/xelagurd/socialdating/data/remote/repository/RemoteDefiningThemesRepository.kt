package xelagurd.socialdating.data.remote.repository

import javax.inject.Inject
import javax.inject.Singleton
import xelagurd.socialdating.data.model.DefiningTheme
import xelagurd.socialdating.data.remote.ApiService

@Singleton
class RemoteDefiningThemesRepository @Inject constructor(
    private val apiService: ApiService
) {
    suspend fun getDefiningThemes(categoryId: Int): List<DefiningTheme> =
        apiService.getDefiningThemes(categoryId)

    suspend fun getDefiningThemes(categoryIds: List<Int>): List<DefiningTheme> =
        apiService.getDefiningThemes(categoryIds)
}