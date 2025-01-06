package xelagurd.socialdating.ui.state

data class LoginUiState(
    val loginDetails: LoginDetails = LoginDetails(),
    val requestStatus: RequestStatus = RequestStatus.UNDEFINED
)

data class LoginDetails(
    val username: String = "",
    val password: String = ""
) {
    val isValid
        get() = username.isNotBlank() && password.isNotBlank()
}