package xelagurd.socialdating.ui.state

import xelagurd.socialdating.data.model.Statement

data class StatementsUiState(
    val categoryId: Int = -1,
    val statements: List<Statement> = listOf(),
    val internetStatus: InternetStatus = InternetStatus.LOADING
)