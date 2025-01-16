package xelagurd.socialdating.ui.state

import xelagurd.socialdating.data.model.details.RegistrationDetails

data class RegistrationUiState(
    override val formDetails: RegistrationDetails = RegistrationDetails(),
    override val actionRequestStatus: RequestStatus = RequestStatus.UNDEFINED
) : FormUiState