package xelagurd.socialdating.client

import retrofit2.Response
import xelagurd.socialdating.client.data.fake.FakeData
import xelagurd.socialdating.client.data.model.additional.AuthResponse
import xelagurd.socialdating.client.data.model.details.LoginDetails
import xelagurd.socialdating.client.data.model.details.RefreshTokenDetails
import xelagurd.socialdating.client.data.model.details.RegistrationDetails
import xelagurd.socialdating.client.data.remote.AuthApiService

class FakeAuthApiService : AuthApiService {
    override suspend fun loginUser(loginDetails: LoginDetails): Response<AuthResponse> =
        Response.success(AuthResponse(FakeData.mainUser, "", ""))

    override suspend fun registerUser(registrationDetails: RegistrationDetails): Response<AuthResponse> =
        Response.success(AuthResponse(FakeData.mainUser, "", ""))

    override suspend fun refreshToken(refreshTokenDetails: RefreshTokenDetails): Response<AuthResponse> =
        Response.success(AuthResponse(FakeData.mainUser, "", ""))
}