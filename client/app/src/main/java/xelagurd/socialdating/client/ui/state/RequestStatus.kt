package xelagurd.socialdating.client.ui.state

sealed class RequestStatus {
    object SUCCESS : RequestStatus()
    object LOADING : RequestStatus()
    data class ERROR(val errorText: String = "") : RequestStatus()
    data class FAILURE(val failureText: String = "") : RequestStatus()
    object UNDEFINED : RequestStatus()

    fun isAllowedRefresh() = this is ERROR || this is FAILURE
}