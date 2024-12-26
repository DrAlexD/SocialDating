package xelagurd.socialdating.data.network.repository

import javax.inject.Inject
import javax.inject.Singleton
import xelagurd.socialdating.data.model.DefiningTheme
import xelagurd.socialdating.data.network.ApiService

@Singleton
class RemoteDefiningThemesRepository @Inject constructor(
    private val apiService: ApiService
) {
    suspend fun getDefiningThemes(categoryId: Int): List<DefiningTheme> =
        apiService.getDefiningThemes(categoryId)
}