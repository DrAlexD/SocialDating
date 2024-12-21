package xelagurd.socialdating.ui.viewmodel

import javax.inject.Inject
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import xelagurd.socialdating.data.fake.FakeDataSource
import xelagurd.socialdating.data.local.LocalCategoriesRepository
import xelagurd.socialdating.data.network.RemoteCategoriesRepository
import xelagurd.socialdating.ui.state.CategoriesUiState
import xelagurd.socialdating.ui.state.InternetStatus

@HiltViewModel
class CategoriesViewModel @Inject constructor(
    private val remoteRepository: RemoteCategoriesRepository,
    private val localRepository: LocalCategoriesRepository
) : ViewModel() {
    var internetStatus by mutableStateOf(InternetStatus.LOADING)
        private set

    val uiState: StateFlow<CategoriesUiState> = localRepository.getCategories()
        .map { CategoriesUiState(it) }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(TIMEOUT_MILLIS),
            initialValue = CategoriesUiState()
        )

    init {
        getCategories()
    }

    fun getCategories() {
        viewModelScope.launch {
            try {
                internetStatus = InternetStatus.LOADING

                delay(3000L) // FixMe: remove after implementing server

                val remoteCategories = remoteRepository.getCategories()
                localRepository.insertCategories(remoteCategories)
                internetStatus = InternetStatus.ONLINE
            } catch (_: Exception) {
                localRepository.insertCategories(FakeDataSource.categories) // FixMe: remove after implementing server
                internetStatus = InternetStatus.OFFLINE
            }
        }
    }

    companion object {
        private const val TIMEOUT_MILLIS = 5_000L
    }
}