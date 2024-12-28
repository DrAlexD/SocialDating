package xelagurd.socialdating.data.network.repository

import javax.inject.Inject
import javax.inject.Singleton
import xelagurd.socialdating.data.model.User
import xelagurd.socialdating.data.network.ApiService

@Singleton
class RemoteUsersRepository @Inject constructor(
    private val apiService: ApiService
) {
    suspend fun getUser(userId: Int): User =
        apiService.getUser(userId)
}