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
import xelagurd.socialdating.client.data.remote.repository.RemoteUsersRepository
import xelagurd.socialdating.client.data.safeApiCall

@Singleton
class AuthInterceptor @Inject constructor(
    @ApplicationContext private val context: Context,
    private val preferencesRepository: PreferencesRepository,
    private val authApiService: AuthApiService
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val accessToken = runBlocking { preferencesRepository.accessToken.first() }
        check(accessToken.isNotEmpty())

        val request = chain
            .request()
            .newBuilder()
            .header("Authorization", "Bearer $accessToken")
            .build()

        val response = chain.proceed(request)

        if (response.code == 401) {
            val refreshToken = runBlocking { preferencesRepository.refreshToken.first() }
            check(refreshToken.isNotEmpty())

            val (newAccessToken, _) = runBlocking {
                safeApiCall(context) {
                    authApiService.refresh(refreshToken)
                }
            }

            if (newAccessToken != null) {
                check(newAccessToken.isNotEmpty())
                runBlocking { preferencesRepository.saveAccessToken(newAccessToken) }

                val newRequest = chain
                    .request()
                    .newBuilder()
                    .header("Authorization", "Bearer $newAccessToken")
                    .build()

                return chain.proceed(newRequest)
            }
        }

        return response
    }
}
