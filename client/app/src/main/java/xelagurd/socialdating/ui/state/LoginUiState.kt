package xelagurd.socialdating.ui.state

import xelagurd.socialdating.data.model.additional.LoginDetails

data class LoginUiState(
    override val formDetails: LoginDetails = LoginDetails(),
    override val requestStatus: RequestStatus = RequestStatus.UNDEFINED
) : FormUiState