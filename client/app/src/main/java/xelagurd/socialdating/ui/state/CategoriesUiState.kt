package xelagurd.socialdating.ui.state

import xelagurd.socialdating.data.model.Category

sealed interface CategoriesUiState {
    data class Success(
        val categories: List<Category> = listOf(),
        val isRemoteData: Boolean
    ) : CategoriesUiState

    object Loading : CategoriesUiState
    object Error : CategoriesUiState

    fun getCurrentStatus() = when (this) {
        is Success -> if (isRemoteData) Status.ONLINE else Status.OFFLINE
        is Loading -> Status.LOADING
        is Error -> Status.OFFLINE
    }

    fun isAllowedRefresh() = getCurrentStatus() == Status.OFFLINE
}