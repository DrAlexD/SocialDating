package xelagurd.socialdating.client.test

import java.io.IOException
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.test.runTest
import android.content.Context
import io.mockk.every
import io.mockk.mockk
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertNull
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.Before
import org.junit.Test
import retrofit2.HttpException
import retrofit2.Response
import xelagurd.socialdating.client.R
import xelagurd.socialdating.client.data.remote.ApiUtils.BAD_REQUEST
import xelagurd.socialdating.client.data.remote.ApiUtils.safeApiCall
import xelagurd.socialdating.client.ui.state.RequestStatus

@OptIn(ExperimentalCoroutinesApi::class)
class ApiUtilsTest {

    private val context = mockk<Context>(relaxed = true)

    private val serverError = "Server error"
    private val noInternetConnection = "No internet connection"
    private val unknownError = "Unknown error"

    private val serverErrorCode = 500
    private val unknownCode = 600

    private val data = "data"
    private val errorBody = "errorBody"

    @Before
    fun setup() {
        every { context.getString(R.string.server_error) } returns serverError
        every { context.getString(R.string.no_internet_connection) } returns noInternetConnection
        every { context.getString(R.string.unknown_error) } returns unknownError
    }

    @Test
    fun apiUtils_successResponse_dataWithSuccessStatus() = runTest {
        val (data, status) = safeApiCall(context) { Response.success(this@ApiUtilsTest.data) }

        assertEquals(this@ApiUtilsTest.data, data)
        assertEquals(RequestStatus.SUCCESS, status)
    }

    @Test
    fun apiUtils_successResponseWithEmptyBody_nullDataWithSuccessStatus() = runTest {
        val (data, status) = safeApiCall(context) { Response.success<String>(null) }

        assertNull(data)
        assertEquals(RequestStatus.SUCCESS, status)
    }

    @Test
    fun apiUtils_delayedSuccessResponse_dataWithSuccessStatus() = runTest {
        val (data, status) = safeApiCall(context) {
            delay(1)
            Response.success(this@ApiUtilsTest.data)
        }

        assertEquals(this@ApiUtilsTest.data, data)
        assertEquals(RequestStatus.SUCCESS, status)
    }

    @Test
    fun apiUtils_badRequestResponse_nullDataWithFailureStatus() = runTest {
        val (data, status) = safeApiCall(context) { errorResponse(BAD_REQUEST) }

        assertNull(data)
        assertEquals(RequestStatus.FAILURE(errorBody), status)
    }

    @Test
    fun apiUtils_unknownCodeResponse_nullDataWithFailureStatus() = runTest {
        val (data, status) = safeApiCall(context) { errorResponse(unknownCode) }

        assertNull(data)
        assertEquals(RequestStatus.FAILURE(errorBody), status)
    }

    @Test
    fun apiUtils_serverErrorResponse_nullDataWithErrorStatus() = runTest {
        val (data, status) = safeApiCall(context) { errorResponse(serverErrorCode) }

        assertNull(data)
        assertEquals(RequestStatus.ERROR(serverError), status)
    }

    @Test
    fun apiUtils_errorResponseWithoutErrorBody_nullDataWithErrorStatus() = runTest {
        val (data, status) = safeApiCall(context) { errorResponseWithoutErrorBody() }

        assertNull(data)
        assertEquals(RequestStatus.ERROR(serverError), status)
    }

    @Test
    fun apiUtils_ioException_nullDataWithNoInternetErrorStatus() = runTest {
        val (data, status) = safeApiCall<String>(context) { throw IOException() }

        assertNull(data)
        assertEquals(RequestStatus.ERROR(noInternetConnection), status)
    }

    @Test
    fun apiUtils_httpException_nullDataWithNoInternetErrorStatus() = runTest {
        val (data, status) = safeApiCall<String>(context) { throw HttpException(errorResponse(BAD_REQUEST)) }

        assertNull(data)
        assertEquals(RequestStatus.ERROR(noInternetConnection), status)
    }

    @Test
    fun apiUtils_unknownException_nullDataWithUnknownErrorStatus() = runTest {
        val (data, status) = safeApiCall<String>(context) { throw IllegalStateException() }

        assertNull(data)
        assertEquals(RequestStatus.ERROR(unknownError), status)
    }

    private fun errorResponse(code: Int): Response<String> =
        Response.error(code, errorBody.toResponseBody())

    private fun errorResponseWithoutErrorBody(): Response<String> {
        val response = mockk<Response<String>>()

        every { response.isSuccessful } returns false
        every { response.code() } returns BAD_REQUEST
        every { response.errorBody() } returns null

        return response
    }
}