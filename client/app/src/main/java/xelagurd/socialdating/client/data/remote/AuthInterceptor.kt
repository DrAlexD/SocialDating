package xelagurd.socialdating.client.data.remote

import java.util.concurrent.locks.ReentrantLock
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.concurrent.withLock
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import okhttp3.Interceptor
import okhttp3.Response
import xelagurd.socialdating.client.data.PreferencesRepository
import xelagurd.socialdating.client.data.local.repository.CommonLocalRepository
import xelagurd.socialdating.client.data.model.details.RefreshTokenDetails
import xelagurd.socialdating.client.data.remote.ApiUtils.UNAUTHORIZED
import xelagurd.socialdating.client.data.remote.ApiUtils.safeApiCall
import xelagurd.socialdating.client.ui.state.RequestStatus

@Singleton
class AuthInterceptor @Inject constructor(
    @param:ApplicationContext private val context: Context,
    private val preferencesRepository: PreferencesRepository,
    private val commonLocalRepository: CommonLocalRepository,
    private val authApiService: AuthApiService
) : Interceptor {

    private val refreshLock = ReentrantLock()

    override fun intercept(chain: Interceptor.Chain): Response {
        val accessToken = runBlocking { preferencesRepository.accessToken.first() }

        val response = chain.proceed(chain.authorizedRequest(accessToken))

        if (response.code != UNAUTHORIZED) return response

        refreshLock.withLock {
            val currentAccessToken = runBlocking { preferencesRepository.accessToken.first() }

            if (currentAccessToken != accessToken) {
                return chain.retryWith(response, currentAccessToken)
            }

            val refreshToken = runBlocking { preferencesRepository.refreshToken.first() }

            if (refreshToken.isEmpty()) return response

            val (refreshResponse, refreshStatus) = runBlocking {
                safeApiCall(context) {
                    authApiService.refreshToken(RefreshTokenDetails(refreshToken))
                }
            }

            if (refreshResponse != null) {
                runBlocking {
                    preferencesRepository.saveAccessToken(refreshResponse.accessToken)
                    preferencesRepository.saveRefreshToken(refreshResponse.refreshToken)
                }

                return chain.retryWith(response, refreshResponse.accessToken)
            }

            if (refreshStatus is RequestStatus.FAILURE) {
                runBlocking {
                    preferencesRepository.clearPreferences()
                    commonLocalRepository.clearData()
                }
            }
        }

        return response
    }

    private fun Interceptor.Chain.authorizedRequest(accessToken: String) =
        request()
            .newBuilder()
            .header("Authorization", "Bearer $accessToken")
            .build()

    private fun Interceptor.Chain.retryWith(unauthorizedResponse: Response, accessToken: String): Response {
        unauthorizedResponse.close()

        return proceed(authorizedRequest(accessToken))
    }
}
