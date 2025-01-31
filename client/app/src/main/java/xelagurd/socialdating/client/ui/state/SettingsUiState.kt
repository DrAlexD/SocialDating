package xelagurd.socialdating.client.ui.state

import xelagurd.socialdating.client.data.model.details.SettingsDetails

data class SettingsUiState(
    override val formDetails: SettingsDetails = SettingsDetails(),
    override val actionRequestStatus: RequestStatus = RequestStatus.UNDEFINED
) : FormUiState