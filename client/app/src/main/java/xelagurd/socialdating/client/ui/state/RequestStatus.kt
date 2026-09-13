package xelagurd.socialdating.client.ui.state

import xelagurd.socialdating.client.data.model.DataEntity

sealed class RequestStatus {
    data object SUCCESS : RequestStatus()
    data object LOADING : RequestStatus()
    data class FAILURE(val failureText: String = "") : RequestStatus()
    data class ERROR(val errorText: String = "") : RequestStatus()
    data object UNDEFINED : RequestStatus()

    fun isAllowedDataRefresh() =
        this !is LOADING && this !is UNDEFINED

    fun isAllowedActionRefresh(isBlockOnSuccess: Boolean = true) =
        this !is LOADING && (!isBlockOnSuccess || this !is SUCCESS)
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
