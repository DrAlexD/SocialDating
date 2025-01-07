package xelagurd.socialdating.ui.state

import xelagurd.socialdating.data.model.additional.RegistrationDetails

data class RegistrationUiState(
    val registrationDetails: RegistrationDetails = RegistrationDetails(),
    val requestStatus: RequestStatus = RequestStatus.UNDEFINED
)