package xelagurd.socialdating.client.ui.state

import xelagurd.socialdating.client.data.model.details.LoginDetails

data class LoginUiState(
    override val formDetails: LoginDetails = LoginDetails(),
    override val actionRequestStatus: RequestStatus = RequestStatus.UNDEFINED
) : FormUiState