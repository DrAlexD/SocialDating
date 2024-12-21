package xelagurd.socialdating.ui.viewmodel

import javax.inject.Inject
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import xelagurd.socialdating.data.fake.FakeDataSource
import xelagurd.socialdating.data.network.RemoteCategoriesRepository
import xelagurd.socialdating.ui.state.CategoriesUiState

@HiltViewModel
class CategoriesViewModel @Inject constructor(
    private val remoteRepository: RemoteCategoriesRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow<CategoriesUiState>(CategoriesUiState.Loading)
    val uiState: StateFlow<CategoriesUiState> = _uiState.asStateFlow()

    init {
        getCategories()
    }

    fun getCategories() {
        viewModelScope.launch {
            _uiState.value = CategoriesUiState.Loading
            _uiState.value = try {
                delay(3000L)
                CategoriesUiState.Success(
                    categories = remoteRepository.getCategories(),
                    isRemoteData = true
                )
            } catch (_: Exception) {
                CategoriesUiState.Success(
                    categories = FakeDataSource.categories,
                    isRemoteData = false
                )
            }
        }
    }
}