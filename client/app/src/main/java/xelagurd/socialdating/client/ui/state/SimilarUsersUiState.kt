package xelagurd.socialdating.client.ui.state

import xelagurd.socialdating.client.data.model.ui.SimilarUserWithData

data class SimilarUsersUiState(
    override val entities: List<SimilarUserWithData> = listOf(),
    override val dataRequestStatus: RequestStatus = RequestStatus.UNDEFINED
) : DataListUiState