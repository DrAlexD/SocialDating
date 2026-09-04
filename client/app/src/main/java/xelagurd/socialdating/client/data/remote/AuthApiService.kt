package xelagurd.socialdating.client.data.remote

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST
import xelagurd.socialdating.client.data.model.details.LoginDetails
import xelagurd.socialdating.client.data.model.details.RefreshTokenDetails
import xelagurd.socialdating.client.data.model.details.RegistrationDetails
import xelagurd.socialdating.client.data.model.dto.AuthDto

interface AuthApiService {
    @POST("users/auth/login")
    suspend fun loginUser(@Body loginDetails: LoginDetails): Response<AuthDto>

    @POST("users/auth/register")
    suspend fun registerUser(@Body registrationDetails: RegistrationDetails): Response<AuthDto>

    @POST("users/auth/refresh-token")
    suspend fun refreshToken(@Body refreshTokenDetails: RefreshTokenDetails): Response<AuthDto>
}