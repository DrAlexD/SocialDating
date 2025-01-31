package xelagurd.socialdating.client.ui.state

import xelagurd.socialdating.client.data.model.details.RegistrationDetails

data class RegistrationUiState(
    override val formDetails: RegistrationDetails = RegistrationDetails(),
    override val actionRequestStatus: RequestStatus = RequestStatus.UNDEFINED
) : FormUiState