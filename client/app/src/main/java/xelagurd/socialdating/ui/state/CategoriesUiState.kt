package xelagurd.socialdating.ui.state

import xelagurd.socialdating.data.model.Category

data class CategoriesUiState(
    val categories: List<Category> = listOf(),
    val internetStatus: InternetStatus = InternetStatus.LOADING
)