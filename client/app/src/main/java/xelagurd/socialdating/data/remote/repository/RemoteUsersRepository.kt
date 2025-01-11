package xelagurd.socialdating.data.remote.repository

import javax.inject.Inject
import javax.inject.Singleton
import xelagurd.socialdating.data.model.additional.LoginDetails
import xelagurd.socialdating.data.model.additional.RegistrationDetails
import xelagurd.socialdating.data.remote.ApiService

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