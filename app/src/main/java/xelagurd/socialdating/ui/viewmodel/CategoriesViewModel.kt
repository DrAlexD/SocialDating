package xelagurd.socialdating.ui.viewmodel

import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import xelagurd.socialdating.data.DataSource
import xelagurd.socialdating.ui.state.CategoriesUiState
import xelagurd.socialdating.ui.utils.toCategoryDetails

class CategoriesViewModel : ViewModel() {
    val uiState: StateFlow<CategoriesUiState> = DataSource.categories
        .map { list ->
            CategoriesUiState(list.map { it.toCategoryDetails() })
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(TIMEOUT_MILLIS),
            initialValue = CategoriesUiState()
        )

    companion object {
        private const val TIMEOUT_MILLIS = 5_000L
    }
}