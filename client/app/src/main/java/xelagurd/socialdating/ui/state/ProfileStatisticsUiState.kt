package xelagurd.socialdating.ui.state

import xelagurd.socialdating.data.model.additional.UserCategoryWithData
import xelagurd.socialdating.data.model.additional.UserDefiningThemeWithData

data class ProfileStatisticsUiState(
    override val entities: List<UserCategoryWithData> = listOf(),
    val entityIdToData: Map<Int, List<UserDefiningThemeWithData>> = mapOf(),
    override val internetStatus: InternetStatus = InternetStatus.LOADING
) : DataListUiState