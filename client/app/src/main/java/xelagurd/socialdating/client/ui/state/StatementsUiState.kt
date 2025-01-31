package xelagurd.socialdating.client.ui.state

import xelagurd.socialdating.client.data.model.Statement

data class StatementsUiState(
    val categoryId: Int = -1,
    override val entities: List<Statement> = listOf(),
    override val dataRequestStatus: RequestStatus = RequestStatus.UNDEFINED
) : DataListUiState