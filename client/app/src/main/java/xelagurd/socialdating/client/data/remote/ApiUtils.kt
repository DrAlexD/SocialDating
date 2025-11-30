package xelagurd.socialdating.client.data.remote

import java.io.IOException
import android.content.Context
import retrofit2.HttpException
import retrofit2.Response
import xelagurd.socialdating.client.R
import xelagurd.socialdating.client.ui.state.RequestStatus

object ApiUtils {

    const val BAD_REQUEST = 400
    const val UNAUTHORIZED = 401

    suspend fun <T> safeApiCall(
        context: Context,
        apiCall: suspend () -> Response<T>
    ): Pair<T?, RequestStatus> {
        var data: T? = null
        var newStatus: RequestStatus

        try {
            val response = apiCall()

            val errorBody = response.errorBody()
            newStatus = when {
                response.isSuccessful -> RequestStatus.SUCCESS.also { data = response.body() }
                !response.isServerError() && errorBody != null -> RequestStatus.FAILURE(errorBody.string())
                else -> RequestStatus.ERROR(context.getString(R.string.server_error))
            }
        } catch (e: Exception) {
            newStatus = when (e) {
                is IOException, is HttpException -> RequestStatus.ERROR(context.getString(R.string.no_internet_connection))
                else -> RequestStatus.ERROR(context.getString(R.string.unknown_error))
            }
        }

        return data to newStatus
    }

    private fun <T> Response<T>.isServerError() = this.code() in 500..599
}