package xelagurd.socialdating.ui.state

import xelagurd.socialdating.data.model.details.LoginDetails

data class LoginUiState(
    override val formDetails: LoginDetails = LoginDetails(),
    override val actionRequestStatus: RequestStatus = RequestStatus.UNDEFINED
) : FormUiState