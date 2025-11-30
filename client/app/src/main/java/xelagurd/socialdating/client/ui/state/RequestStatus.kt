package xelagurd.socialdating.client.ui.state

sealed class RequestStatus {
    data object SUCCESS : RequestStatus()
    data object LOADING : RequestStatus()
    data class FAILURE(val failureText: String = "") : RequestStatus()
    data class ERROR(val errorText: String = "") : RequestStatus()
    data object UNDEFINED : RequestStatus()

    fun isAllowedRefresh() = this is SUCCESS || this is FAILURE || this is ERROR
}