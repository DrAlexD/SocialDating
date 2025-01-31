package xelagurd.socialdating.client.ui.state

import xelagurd.socialdating.client.data.model.Category

data class CategoriesUiState(
    override val entities: List<Category> = listOf(),
    override val dataRequestStatus: RequestStatus = RequestStatus.UNDEFINED
) : DataListUiState