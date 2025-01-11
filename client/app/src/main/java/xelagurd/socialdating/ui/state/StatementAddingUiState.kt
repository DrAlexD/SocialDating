package xelagurd.socialdating.ui.state

import xelagurd.socialdating.data.model.DefiningTheme
import xelagurd.socialdating.data.model.additional.StatementDetails

data class StatementAddingUiState(
    val definingThemes: List<DefiningTheme> = listOf(),
    val statementDetails: StatementDetails = StatementDetails(),
    val requestStatus: RequestStatus = RequestStatus.UNDEFINED
)