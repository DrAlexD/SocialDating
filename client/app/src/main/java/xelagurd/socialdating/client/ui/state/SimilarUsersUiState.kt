package xelagurd.socialdating.client.ui.state

import xelagurd.socialdating.client.data.model.Category
import xelagurd.socialdating.client.data.model.details.SimilarUsersFilterDetails
import xelagurd.socialdating.client.data.model.dto.SimilarUserDto
import xelagurd.socialdating.client.ui.form.SimilarUsersFilterFormData

data class SimilarUsersUiState(
    override val entities: List<SimilarUserDto> = listOf(),
    val categories: List<Category> = listOf(),
    val formData: SimilarUsersFilterFormData = SimilarUsersFilterFormData(),
    // the displayed similar users are found with this filter, the current one can be already changed
    val calculatedFilterDetails: SimilarUsersFilterDetails = SimilarUsersFilterDetails(),
    override val dataRequestStatus: RequestStatus = RequestStatus.NOT_REQUESTED,
    override val nextPageRequestStatus: RequestStatus = RequestStatus.UNDEFINED,
    override val isLastPage: Boolean = false,
    override val notification: String? = null
) : PagedDataListUiState {

    fun isCalculated() = dataRequestStatus !is RequestStatus.NOT_REQUESTED
}
