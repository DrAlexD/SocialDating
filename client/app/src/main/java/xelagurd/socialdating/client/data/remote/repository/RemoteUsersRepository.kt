package xelagurd.socialdating.client.data.remote.repository

import javax.inject.Inject
import javax.inject.Singleton
import xelagurd.socialdating.client.data.model.details.LoginDetails
import xelagurd.socialdating.client.data.model.details.RegistrationDetails
import xelagurd.socialdating.client.data.remote.ApiService
import xelagurd.socialdating.client.data.remote.AuthApiService

@Singleton
class RemoteUsersRepository @Inject constructor(
    private val apiService: ApiService,
    private val authApiService: AuthApiService
) {
    suspend fun getUser(userId: Int) =
        apiService.getUser(userId)

    suspend fun getUsers(userIds: List<Int>) =
        apiService.getUsers(userIds)

    suspend fun loginUser(loginDetails: LoginDetails) =
        authApiService.loginUser(loginDetails)

    suspend fun registerUser(registrationDetails: RegistrationDetails) =
        authApiService.registerUser(registrationDetails)
}