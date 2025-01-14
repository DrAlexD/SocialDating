package xelagurd.socialdating.ui.state

import xelagurd.socialdating.data.model.additional.RegistrationDetails

data class RegistrationUiState(
    override val formDetails: RegistrationDetails = RegistrationDetails(),
    override val requestStatus: RequestStatus = RequestStatus.UNDEFINED
) : FormUiState