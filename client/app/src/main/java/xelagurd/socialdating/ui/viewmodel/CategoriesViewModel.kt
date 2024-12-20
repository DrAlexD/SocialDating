package xelagurd.socialdating.ui.viewmodel

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import androidx.lifecycle.ViewModel
import xelagurd.socialdating.data.fake.FakeDataSource
import xelagurd.socialdating.ui.state.CategoriesUiState

class CategoriesViewModel : ViewModel() {
    private val _uiState = MutableStateFlow<CategoriesUiState>(
        CategoriesUiState(FakeDataSource.categories)
    )
    val uiState: StateFlow<CategoriesUiState> = _uiState.asStateFlow()
}