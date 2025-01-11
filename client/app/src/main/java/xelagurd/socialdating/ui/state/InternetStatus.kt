package xelagurd.socialdating.ui.state

enum class InternetStatus {
    ONLINE,
    LOADING,
    OFFLINE;

    fun isAllowedRefresh() = this == OFFLINE

    fun isLoaded() = this != LOADING
}