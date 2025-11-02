package xelagurd.socialdating.client.ui.state

data class SettingsUiState(
    override val actionRequestStatus: RequestStatus = RequestStatus.UNDEFINED
) : ActionRequestUiState