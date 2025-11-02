package xelagurd.socialdating.client.ui.state

import xelagurd.socialdating.client.ui.form.RegistrationFormData

data class RegistrationUiState(
    override val formData: RegistrationFormData = RegistrationFormData(),
    override val actionRequestStatus: RequestStatus = RequestStatus.UNDEFINED
) : FormUiState