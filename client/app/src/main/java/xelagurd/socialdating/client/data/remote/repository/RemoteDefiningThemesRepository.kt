package xelagurd.socialdating.client.data.remote.repository

import javax.inject.Inject
import javax.inject.Singleton
import xelagurd.socialdating.client.data.remote.ApiService

@Singleton
class RemoteDefiningThemesRepository @Inject constructor(
    private val apiService: ApiService
) {
    suspend fun getDefiningThemes(definingThemeIds: List<Int>? = null, categoryId: Int? = null) =
        apiService.getDefiningThemes(definingThemeIds, categoryId)
}