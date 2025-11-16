package xelagurd.socialdating.client.data.remote

import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import okhttp3.Interceptor
import okhttp3.Response
import xelagurd.socialdating.client.data.PreferencesRepository
import xelagurd.socialdating.client.data.local.repository.CommonLocalRepository
import xelagurd.socialdating.client.data.model.details.RefreshTokenDetails

@Singleton
class AuthInterceptor @Inject constructor(
    @param:ApplicationContext private val context: Context,
    private val preferencesRepository: PreferencesRepository,
    private val commonLocalRepository: CommonLocalRepository,
    private val authApiService: AuthApiService
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val accessToken = runBlocking { preferencesRepository.accessToken.first() }

        val request = chain
            .request()
            .newBuilder()
            .header("Authorization", "Bearer $accessToken")
            .build()

        val response = chain.proceed(request)

        if (response.code == UNAUTHORIZED) {
            val refreshToken = runBlocking { preferencesRepository.refreshToken.first() }

            val (refreshResponse, _) = runBlocking {
                safeApiCall(context) {
                    authApiService.refreshToken(RefreshTokenDetails(refreshToken))
                }
            }

            if (refreshResponse != null) {
                runBlocking {
                    preferencesRepository.saveAccessToken(refreshResponse.accessToken)
                    preferencesRepository.saveRefreshToken(refreshResponse.refreshToken)
                }

                val newRequest = chain
                    .request()
                    .newBuilder()
                    .header("Authorization", "Bearer ${refreshResponse.accessToken}")
                    .build()

                return chain.proceed(newRequest)
            } else {
                runBlocking {
                    preferencesRepository.clearPreferences()
                    commonLocalRepository.clearData()
                }
            }
        }

        return response
    }
}
