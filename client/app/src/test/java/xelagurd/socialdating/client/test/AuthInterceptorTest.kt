package xelagurd.socialdating.client.test

import java.io.IOException
import kotlinx.coroutines.flow.flowOf
import android.content.Context
import io.mockk.Runs
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.verify
import junit.framework.TestCase.assertEquals
import okhttp3.Interceptor
import okhttp3.Protocol
import okhttp3.Request
import okhttp3.Response
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.Before
import org.junit.Test
import xelagurd.socialdating.client.data.PreferencesRepository
import xelagurd.socialdating.client.data.fake.FakeData
import xelagurd.socialdating.client.data.local.repository.CommonLocalRepository
import xelagurd.socialdating.client.data.model.additional.AuthResponse
import xelagurd.socialdating.client.data.remote.ApiUtils.UNAUTHORIZED
import xelagurd.socialdating.client.data.remote.AuthApiService
import xelagurd.socialdating.client.data.remote.AuthInterceptor
import retrofit2.Response as RetrofitResponse

class AuthInterceptorTest {

    private val context = mockk<Context>(relaxed = true)
    private val preferencesRepository = mockk<PreferencesRepository>(relaxed = true)
    private val commonLocalRepository = mockk<CommonLocalRepository>()
    private val authApiService = mockk<AuthApiService>()
    private val chain = mockk<Interceptor.Chain>()

    private lateinit var authInterceptor: AuthInterceptor

    private val authorizationHeader = "Authorization"
    private val successCode = 200

    private val accessToken = "accessToken"
    private val newAccessToken = "newAccessToken"
    private val refreshToken = "refreshToken"
    private val newRefreshToken = "newRefreshToken"

    private val authResponse = AuthResponse(FakeData.users[0], newAccessToken, newRefreshToken)

    private val request = Request.Builder().url("http://localhost/").build()
    private val requests = mutableListOf<Request>()

    private val successResponse = response(successCode)
    private val unauthorizedResponse = response(UNAUTHORIZED)

    @Before
    fun setup() {
        every { chain.request() } returns request

        authInterceptor = AuthInterceptor(
            context,
            preferencesRepository,
            commonLocalRepository,
            authApiService
        )
    }

    @Test
    fun authInterceptor_actualToken_successResponseWithActualToken() {
        mockTokens()
        every { chain.proceed(capture(requests)) } returns successResponse

        val result = authInterceptor.intercept(chain)

        assertEquals(successResponse, result)
        assertEquals("Bearer $accessToken", requests.single().header(authorizationHeader))

        coVerify(exactly = 0) { authApiService.refreshToken(any()) }
    }

    @Test
    fun authInterceptor_tokenRefreshedByAnotherRequest_successResponseWithNewToken() {
        every { preferencesRepository.accessToken } returnsMany listOf(flowOf(accessToken), flowOf(newAccessToken))
        every { chain.proceed(capture(requests)) } returnsMany listOf(unauthorizedResponse, successResponse)

        val result = authInterceptor.intercept(chain)

        assertEquals(successResponse, result)
        assertEquals("Bearer $accessToken", requests[0].header(authorizationHeader))
        assertEquals("Bearer $newAccessToken", requests[1].header(authorizationHeader))

        coVerify(exactly = 0) { authApiService.refreshToken(any()) }
    }

    @Test
    fun authInterceptor_refreshTokenWithInternet_successResponseWithNewToken() {
        mockTokens()
        every { chain.proceed(capture(requests)) } returnsMany listOf(unauthorizedResponse, successResponse)
        coEvery { authApiService.refreshToken(any()) } returns RetrofitResponse.success(authResponse)

        val result = authInterceptor.intercept(chain)

        assertEquals(successResponse, result)
        assertEquals("Bearer $accessToken", requests[0].header(authorizationHeader))
        assertEquals("Bearer $newAccessToken", requests[1].header(authorizationHeader))

        coVerify(exactly = 1) { authApiService.refreshToken(any()) }
        coVerify(exactly = 1) { preferencesRepository.saveAccessToken(newAccessToken) }
        coVerify(exactly = 1) { preferencesRepository.saveRefreshToken(newRefreshToken) }
    }

    @Test
    fun authInterceptor_refreshTokenWithWrongData_unauthorizedResponseWithClearedData() {
        mockTokens()
        mockClearedData()
        every { chain.proceed(capture(requests)) } returns unauthorizedResponse
        coEvery { authApiService.refreshToken(any()) } returns
                RetrofitResponse.error(UNAUTHORIZED, UNAUTHORIZED.toString().toResponseBody())

        val result = authInterceptor.intercept(chain)

        assertEquals(unauthorizedResponse, result)
        assertEquals("Bearer $accessToken", requests.single().header(authorizationHeader))

        coVerify(exactly = 1) { authApiService.refreshToken(any()) }
        coVerify(exactly = 1) { preferencesRepository.clearPreferences() }
        coVerify(exactly = 1) { commonLocalRepository.clearData() }
    }

    @Test
    fun authInterceptor_refreshTokenWithoutInternet_unauthorizedResponseWithClearedData() {
        mockTokens()
        mockClearedData()
        every { chain.proceed(capture(requests)) } returns unauthorizedResponse
        coEvery { authApiService.refreshToken(any()) } throws IOException()

        val result = authInterceptor.intercept(chain)

        assertEquals(unauthorizedResponse, result)

        verify(exactly = 1) { chain.proceed(any()) }
        coVerify(exactly = 1) { preferencesRepository.clearPreferences() }
        coVerify(exactly = 1) { commonLocalRepository.clearData() }
    }

    private fun mockTokens() {
        every { preferencesRepository.accessToken } returns flowOf(accessToken)
        every { preferencesRepository.refreshToken } returns flowOf(refreshToken)
    }

    private fun mockClearedData() {
        coEvery { commonLocalRepository.clearData() } just Runs
    }

    private fun response(code: Int) =
        Response.Builder()
            .request(request)
            .protocol(Protocol.HTTP_1_1)
            .code(code)
            .message(code.toString())
            .build()
}