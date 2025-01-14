package xelagurd.socialdating.ui.state

import xelagurd.socialdating.data.model.additional.SettingsDetails

data class SettingsUiState(
    override val formDetails: SettingsDetails = SettingsDetails(),
    override val requestStatus: RequestStatus = RequestStatus.UNDEFINED
) : FormUiState