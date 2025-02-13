package xelagurd.socialdating.client.data.remote

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.Query
import xelagurd.socialdating.client.data.model.additional.AuthResponse
import xelagurd.socialdating.client.data.model.details.LoginDetails
import xelagurd.socialdating.client.data.model.details.RegistrationDetails

interface AuthApiService {
    @POST("users/auth/login")
    suspend fun loginUser(@Body loginDetails: LoginDetails): Response<AuthResponse>

    @POST("users/auth/register")
    suspend fun registerUser(@Body registrationDetails: RegistrationDetails): Response<AuthResponse>

    @POST("users/auth/refresh")
    suspend fun refresh(@Query("refreshToken") refreshToken: String): Response<String>
}