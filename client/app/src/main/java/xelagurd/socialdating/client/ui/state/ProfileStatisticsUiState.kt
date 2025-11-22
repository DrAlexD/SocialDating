package xelagurd.socialdating.client.ui.state

import xelagurd.socialdating.client.data.model.additional.DetailedSimilarUser
import xelagurd.socialdating.client.data.model.ui.UserCategoryWithData
import xelagurd.socialdating.client.data.model.ui.UserDefiningThemeWithData

data class ProfileStatisticsUiState(
    override val entities: List<UserCategoryWithData> = listOf(),
    val entityIdToData: Map<Int, List<UserDefiningThemeWithData>> = mapOf(),
    val entitiesMask: DetailedSimilarUser? = null,
    override val dataRequestStatus: RequestStatus = RequestStatus.UNDEFINED
) : DataListUiState