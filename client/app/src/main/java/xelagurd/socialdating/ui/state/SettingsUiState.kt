package xelagurd.socialdating.ui.state

import xelagurd.socialdating.data.model.details.SettingsDetails

data class SettingsUiState(
    override val formDetails: SettingsDetails = SettingsDetails(),
    override val actionRequestStatus: RequestStatus = RequestStatus.UNDEFINED
) : FormUiState