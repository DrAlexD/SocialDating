package xelagurd.socialdating.ui.state

import xelagurd.socialdating.data.model.additional.UserCategoryWithData
import xelagurd.socialdating.data.model.additional.UserDefiningThemeWithData

data class ProfileStatisticsUiState(
    val userCategories: List<UserCategoryWithData> = listOf(),
    val userCategoryToDefiningThemes: Map<Int, List<UserDefiningThemeWithData>> = mapOf(),
    val internetStatus: InternetStatus = InternetStatus.LOADING
)