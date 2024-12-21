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
import xelagurd.socialdating.data.local.LocalCategoriesRepository
import xelagurd.socialdating.data.network.RemoteCategoriesRepository
import xelagurd.socialdating.ui.state.CategoriesUiState

@HiltViewModel
class CategoriesViewModel @Inject constructor(
    private val remoteRepository: RemoteCategoriesRepository,
    private val localRepository: LocalCategoriesRepository
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

                val remoteCategories = remoteRepository.getCategories()
                remoteCategories.forEach { localRepository.insertCategory(it) }

                CategoriesUiState.Success(
                    categories = remoteCategories,
                    isRemoteData = true
                )
            } catch (_: Exception) {
                try {
                    val localCategories = localRepository.getCategories()

                    if (localCategories.isNotEmpty()) {
                        CategoriesUiState.Success(
                            categories = localCategories,
                            isRemoteData = false
                        )
                    } else {
                        FakeDataSource.categories.forEach { localRepository.insertCategory(it) }
                        CategoriesUiState.Error
                    }
                } catch (_: Exception) {
                    FakeDataSource.categories.forEach { localRepository.insertCategory(it) }
                    CategoriesUiState.Error
                }
            }
        }
    }
}