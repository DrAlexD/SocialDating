package xelagurd.socialdating.ui.state

enum class RequestStatus {
    SUCCESS,
    LOADING,
    ERROR,
    FAILED,
    UNDEFINED;

    fun isAllowedRefresh() = this == ERROR || this == FAILED
}