package xelagurd.socialdating.client

import retrofit2.Response
import xelagurd.socialdating.client.data.fake.FakeDataSource
import xelagurd.socialdating.client.data.model.additional.AuthResponse
import xelagurd.socialdating.client.data.model.details.LoginDetails
import xelagurd.socialdating.client.data.model.details.RefreshTokenDetails
import xelagurd.socialdating.client.data.model.details.RegistrationDetails
import xelagurd.socialdating.client.data.remote.AuthApiService

class FakeAuthApiService : AuthApiService {
    override suspend fun loginUser(loginDetails: LoginDetails): Response<AuthResponse> =
        Response.success(AuthResponse(FakeDataSource.users[0], "", ""))

    override suspend fun registerUser(registrationDetails: RegistrationDetails): Response<AuthResponse> =
        Response.success(AuthResponse(FakeDataSource.users[0], "", ""))

    override suspend fun refreshToken(refreshTokenDetails: RefreshTokenDetails): Response<AuthResponse> =
        Response.success(AuthResponse(FakeDataSource.users[0], "", ""))
}