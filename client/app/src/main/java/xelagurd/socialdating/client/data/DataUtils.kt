package xelagurd.socialdating.client.data

import java.io.IOException
import android.content.Context
import retrofit2.HttpException
import retrofit2.Response
import xelagurd.socialdating.client.R
import xelagurd.socialdating.client.ui.state.RequestStatus

suspend fun <T> safeApiCall(
    context: Context,
    apiCall: suspend () -> Response<T>
): Pair<T?, RequestStatus> {
    var data: T? = null
    var newStatus: RequestStatus

    try {
        val response = apiCall()

        newStatus = if (response.isSuccessful) {
            response.body()?.let { responseData ->
                RequestStatus.SUCCESS.also { data = responseData }
            } ?: RequestStatus.ERROR(context.getString(R.string.server_error))
        } else {
            when (response.code()) {
                404 -> RequestStatus.FAILURE(context.getString(R.string.no_data))
                500 -> RequestStatus.ERROR(context.getString(R.string.server_error))

                else -> response.errorBody()?.let {
                    RequestStatus.FAILURE(it.string())
                } ?: RequestStatus.ERROR(context.getString(R.string.server_error))
            }
        }
    } catch (e: Exception) {
        newStatus = when (e) {
            is IOException, is HttpException -> RequestStatus.ERROR(context.getString(R.string.no_internet_connection))
            else -> RequestStatus.ERROR(context.getString(R.string.unknown_error))
        }
    }

    return data to newStatus
}