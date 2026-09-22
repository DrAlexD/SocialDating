package xelagurd.socialdating.client.ui.state

import xelagurd.socialdating.client.data.model.Statement

data class StatementsUiState(
    val categoryId: Int = -1,
    override val entities: List<Statement> = listOf(),
    override val dataRequestStatus: RequestStatus = RequestStatus.UNDEFINED,
    override val nextPageRequestStatus: RequestStatus = RequestStatus.UNDEFINED,
    override val isLastPage: Boolean = false,
    override val actionRequestStatus: RequestStatus = RequestStatus.UNDEFINED,
    val reactingStatementIds: Set<Int> = setOf(),
    override val notification: String? = null
) : PagedDataListUiState, ActionRequestUiState
