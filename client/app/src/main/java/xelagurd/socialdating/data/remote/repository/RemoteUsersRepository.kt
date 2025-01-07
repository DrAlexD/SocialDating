package xelagurd.socialdating.data.remote.repository

import javax.inject.Inject
import javax.inject.Singleton
import xelagurd.socialdating.data.model.User
import xelagurd.socialdating.data.model.additional.LoginDetails
import xelagurd.socialdating.data.model.additional.RegistrationDetails
import xelagurd.socialdating.data.remote.ApiService

@Singleton
class RemoteUsersRepository @Inject constructor(
    private val apiService: ApiService
) {
    suspend fun getUser(userId: Int): User? =
        apiService.getUser(userId)

    suspend fun loginUser(loginDetails: LoginDetails): User? =
        apiService.loginUser(loginDetails)

    suspend fun registerUser(registrationDetails: RegistrationDetails): User? =
        apiService.registerUser(registrationDetails)
}