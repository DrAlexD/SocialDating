package xelagurd.socialdating.client.ui.state

import xelagurd.socialdating.client.data.model.User

data class ProfileUiState(
    override val entity: User? = null,
    override val dataRequestStatus: RequestStatus = RequestStatus.UNDEFINED
) : DataEntityUiState