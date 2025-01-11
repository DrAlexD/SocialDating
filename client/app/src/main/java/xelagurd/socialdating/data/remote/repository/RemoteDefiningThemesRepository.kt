package xelagurd.socialdating.data.remote.repository

import javax.inject.Inject
import javax.inject.Singleton
import xelagurd.socialdating.data.remote.ApiService

@Singleton
class RemoteDefiningThemesRepository @Inject constructor(
    private val apiService: ApiService
) {
    suspend fun getDefiningThemes(categoryId: Int) =
        apiService.getDefiningThemes(categoryId)

    suspend fun getDefiningThemes(categoryIds: List<Int>) =
        apiService.getDefiningThemes(categoryIds)
}