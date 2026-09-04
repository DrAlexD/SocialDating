package xelagurd.socialdating.client.ui.state

import xelagurd.socialdating.client.data.model.dto.SimilarUserDto

data class SimilarUsersUiState(
    override val entities: List<SimilarUserDto> = listOf(),
    override val dataRequestStatus: RequestStatus = RequestStatus.UNDEFINED
) : DataListUiState