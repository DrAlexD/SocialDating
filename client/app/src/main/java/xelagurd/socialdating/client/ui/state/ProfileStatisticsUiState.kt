package xelagurd.socialdating.client.ui.state

import xelagurd.socialdating.client.data.model.additional.UserCategoryData
import xelagurd.socialdating.client.data.model.additional.UserDefiningThemeData
import xelagurd.socialdating.client.data.model.dto.DetailedSimilarUserDto

data class ProfileStatisticsUiState(
    val userId: Int = -1,
    val anotherUserId: Int = -1,
    override val entities: List<UserCategoryData> = listOf(),
    val entityIdToData: Map<Int, List<UserDefiningThemeData>> = mapOf(),
    val entitiesMask: DetailedSimilarUserDto? = null,
    override val dataRequestStatus: RequestStatus = RequestStatus.UNDEFINED
) : DataListUiState