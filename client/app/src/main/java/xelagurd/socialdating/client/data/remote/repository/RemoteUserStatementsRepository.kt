package xelagurd.socialdating.client.data.remote.repository

import javax.inject.Inject
import javax.inject.Singleton
import xelagurd.socialdating.client.data.remote.ApiService

@Singleton
class RemoteUserStatementsRepository @Inject constructor(
    private val apiService: ApiService
) {
    suspend fun getUserStatements(userId: Int, definingThemeIds: List<Int>) =
        apiService.getUserStatements(userId, definingThemeIds)
}