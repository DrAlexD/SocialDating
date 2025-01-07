package xelagurd.socialdating.ui.state

import xelagurd.socialdating.data.model.additional.LoginDetails

data class LoginUiState(
    val loginDetails: LoginDetails = LoginDetails(),
    val requestStatus: RequestStatus = RequestStatus.UNDEFINED
)