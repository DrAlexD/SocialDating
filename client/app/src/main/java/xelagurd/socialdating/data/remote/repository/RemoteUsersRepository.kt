package xelagurd.socialdating.data.remote.repository

import javax.inject.Inject
import javax.inject.Singleton
import xelagurd.socialdating.data.model.User
import xelagurd.socialdating.data.remote.ApiService

@Singleton
class RemoteUsersRepository @Inject constructor(
    private val apiService: ApiService
) {
    suspend fun getUser(userId: Int): User =
        apiService.getUser(userId)

    suspend fun logInUser(username: String, password: String): User =
        apiService.logInUser(username, password)
}