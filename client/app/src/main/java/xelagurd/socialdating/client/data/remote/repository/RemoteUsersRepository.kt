package xelagurd.socialdating.client.data.remote.repository

import javax.inject.Inject
import javax.inject.Singleton
import xelagurd.socialdating.client.data.model.details.LoginDetails
import xelagurd.socialdating.client.data.model.details.RegistrationDetails
import xelagurd.socialdating.client.data.remote.ApiService

@Singleton
class RemoteUsersRepository @Inject constructor(
    private val apiService: ApiService
) {
    suspend fun getUser(userId: Int) =
        apiService.getUser(userId)

    suspend fun loginUser(loginDetails: LoginDetails) =
        apiService.loginUser(loginDetails)

    suspend fun registerUser(registrationDetails: RegistrationDetails) =
        apiService.registerUser(registrationDetails)
}