package xelagurd.socialdating.client.data.remote

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST
import xelagurd.socialdating.client.data.model.additional.AuthResponse
import xelagurd.socialdating.client.data.model.details.LoginDetails
import xelagurd.socialdating.client.data.model.details.RefreshTokenDetails
import xelagurd.socialdating.client.data.model.details.RegistrationDetails

interface AuthApiService {
    @POST("users/auth/login")
    suspend fun loginUser(@Body loginDetails: LoginDetails): Response<AuthResponse>

    @POST("users/auth/register")
    suspend fun registerUser(@Body registrationDetails: RegistrationDetails): Response<AuthResponse>

    @POST("users/auth/refresh-token")
    suspend fun refreshToken(@Body refreshTokenDetails: RefreshTokenDetails): Response<AuthResponse>
}