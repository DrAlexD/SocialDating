package xelagurd.socialdating.ui.state

import xelagurd.socialdating.data.model.Statement

data class StatementsUiState(
    val categoryId: Int = -1,
    override val entities: List<Statement> = listOf(),
    override val internetStatus: InternetStatus = InternetStatus.LOADING
) : DataListUiState