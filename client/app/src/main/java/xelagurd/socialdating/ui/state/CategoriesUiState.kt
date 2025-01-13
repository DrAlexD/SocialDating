package xelagurd.socialdating.ui.state

import xelagurd.socialdating.data.model.Category

data class CategoriesUiState(
    override val entities: List<Category> = listOf(),
    override val internetStatus: InternetStatus = InternetStatus.LOADING
) : DataListUiState