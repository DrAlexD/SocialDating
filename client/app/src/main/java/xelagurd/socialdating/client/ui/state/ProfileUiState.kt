package xelagurd.socialdating.client.ui.state

import xelagurd.socialdating.client.data.model.User

data class ProfileUiState(
    val userId: Int = -1,
    val anotherUserId: Int = -1,
    override val entity: User? = null,
    override val dataRequestStatus: RequestStatus = RequestStatus.UNDEFINED
) : DataEntityUiState