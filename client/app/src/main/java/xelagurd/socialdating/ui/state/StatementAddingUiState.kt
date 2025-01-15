package xelagurd.socialdating.ui.state

import xelagurd.socialdating.data.model.DefiningTheme
import xelagurd.socialdating.data.model.additional.StatementDetails

data class StatementAddingUiState(
    override val entities: List<DefiningTheme> = listOf(),
    override val internetStatus: InternetStatus = InternetStatus.OFFLINE,
    override val formDetails: StatementDetails = StatementDetails(),
    override val requestStatus: RequestStatus = RequestStatus.UNDEFINED
) : FormUiState, DataListUiState