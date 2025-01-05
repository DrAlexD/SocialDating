package xelagurd.socialdating.ui.state

import xelagurd.socialdating.data.model.Statement

data class StatementsUiState(
    val statements: List<Statement> = listOf(),
    val internetStatus: InternetStatus = InternetStatus.LOADING
)