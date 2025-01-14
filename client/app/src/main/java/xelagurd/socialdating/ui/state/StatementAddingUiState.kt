package xelagurd.socialdating.ui.state

import xelagurd.socialdating.data.model.DefiningTheme
import xelagurd.socialdating.data.model.additional.StatementDetails

data class StatementAddingUiState(
    val definingThemes: List<DefiningTheme> = listOf(),
    override val formDetails: StatementDetails = StatementDetails(),
    override val requestStatus: RequestStatus = RequestStatus.UNDEFINED
) : FormUiState