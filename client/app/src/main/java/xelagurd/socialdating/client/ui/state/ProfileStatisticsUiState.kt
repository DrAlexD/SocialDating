package xelagurd.socialdating.client.ui.state

import xelagurd.socialdating.client.data.model.additional.UserCategoryWithData
import xelagurd.socialdating.client.data.model.additional.UserDefiningThemeWithData

data class ProfileStatisticsUiState(
    override val entities: List<UserCategoryWithData> = listOf(),
    val entityIdToData: Map<Int, List<UserDefiningThemeWithData>> = mapOf(),
    override val dataRequestStatus: RequestStatus = RequestStatus.UNDEFINED
) : DataListUiState