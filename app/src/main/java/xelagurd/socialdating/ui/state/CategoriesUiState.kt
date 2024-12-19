package xelagurd.socialdating.ui.state

import xelagurd.socialdating.ui.model.CategoryDetails

data class CategoriesUiState(
    val categories: List<CategoryDetails> = listOf()
)