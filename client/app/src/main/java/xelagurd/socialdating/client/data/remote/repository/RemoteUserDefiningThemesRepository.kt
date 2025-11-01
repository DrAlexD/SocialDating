package xelagurd.socialdating.client.data.remote.repository

import javax.inject.Inject
import javax.inject.Singleton
import xelagurd.socialdating.client.data.remote.ApiService

@Singleton
class RemoteUserDefiningThemesRepository @Inject constructor(
    private val apiService: ApiService
) {
    suspend fun getUserDefiningThemes(userId: Int) =
        apiService.getUserDefiningThemes(userId)
}