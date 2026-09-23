package xelagurd.socialdating.client.ui.state

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import xelagurd.socialdating.client.data.model.DataEntity

sealed class RequestStatus {
    data object SUCCESS : RequestStatus()
    data object LOADING : RequestStatus()
    data class FAILURE(val failureText: String = "") : RequestStatus()
    data class ERROR(val errorText: String = "") : RequestStatus()
    data object UNDEFINED : RequestStatus()

    fun isDataLoading() =
        this is LOADING || this is UNDEFINED

    fun isAllowedDataRefresh() =
        !isDataLoading()

    fun isAllowedActionRefresh() =
        this !is LOADING && this !is SUCCESS

    fun notificationText() =
        when (this) {
            is FAILURE -> failureText
            is ERROR -> errorText
            else -> null
        }
}

fun <T> List<T>.hideWhileLoading(dataRequestStatus: RequestStatus) =
    when (dataRequestStatus) {
        is RequestStatus.LOADING -> listOf()
        else -> this
    }

fun <T : DataEntity> T?.hideWhileLoading(dataRequestStatus: RequestStatus) =
    when (dataRequestStatus) {
        is RequestStatus.LOADING -> null
        else -> this
    }

fun MutableStateFlow<String?>.updateLoadingNotification(
    requestStatus: RequestStatus,
    isRequestedByUser: Boolean,
    isDataExist: Boolean
) {
    if (isRequestedByUser || isDataExist) {
        update { requestStatus.notificationText() }
    }
}

suspend fun MutableStateFlow<String?>.updatePageLoadingNotification(
    requestStatus: RequestStatus,
    isFirstPage: Boolean,
    isRequestedByUser: Boolean,
    isDataExist: suspend () -> Boolean
) {
    if (!isFirstPage) return

    updateLoadingNotification(
        requestStatus = requestStatus,
        isRequestedByUser = isRequestedByUser,
        isDataExist = isDataExist()
    )
}
