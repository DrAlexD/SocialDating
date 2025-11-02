package xelagurd.socialdating.client.ui.state

import xelagurd.socialdating.client.ui.form.LoginFormData

data class LoginUiState(
    override val formData: LoginFormData = LoginFormData(),
    override val actionRequestStatus: RequestStatus = RequestStatus.UNDEFINED
) : FormUiState