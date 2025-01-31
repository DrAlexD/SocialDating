package xelagurd.socialdating.client.ui.state

import xelagurd.socialdating.client.data.model.DefiningTheme
import xelagurd.socialdating.client.data.model.details.StatementDetails

data class StatementAddingUiState(
    override val entities: List<DefiningTheme> = listOf(),
    override val dataRequestStatus: RequestStatus = RequestStatus.UNDEFINED,
    override val formDetails: StatementDetails = StatementDetails(),
    override val actionRequestStatus: RequestStatus = RequestStatus.UNDEFINED
) : FormUiState, DataListUiState