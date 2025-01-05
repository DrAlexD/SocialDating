package xelagurd.socialdating.ui.viewmodel

import java.io.IOException
import javax.inject.Inject
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import xelagurd.socialdating.data.fake.FakeDataSource
import xelagurd.socialdating.data.local.repository.LocalCategoriesRepository
import xelagurd.socialdating.data.remote.repository.RemoteCategoriesRepository
import xelagurd.socialdating.ui.state.CategoriesUiState
import xelagurd.socialdating.ui.state.InternetStatus

@HiltViewModel
class CategoriesViewModel @Inject constructor(
    private val remoteRepository: RemoteCategoriesRepository,
    private val localRepository: LocalCategoriesRepository
) : ViewModel() {
    private val internetStatusFlow = MutableStateFlow(InternetStatus.LOADING)
    private val categoriesFlow = localRepository.getCategories().distinctUntilChanged()

    val uiState = combine(categoriesFlow, internetStatusFlow) { categories, internetStatus ->
        CategoriesUiState(
            categories = categories,
            internetStatus = internetStatus
        )
    }.stateIn(
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
                internetStatusFlow.update { InternetStatus.LOADING }

                delay(3000L) // FixMe: remove after implementing server

                val remoteCategories = remoteRepository.getCategories()
                localRepository.insertCategories(remoteCategories)
                internetStatusFlow.update { InternetStatus.ONLINE }
            } catch (_: IOException) {
                localRepository.insertCategories(FakeDataSource.categories) // FixMe: remove after implementing server
                internetStatusFlow.update { InternetStatus.OFFLINE }
            }
        }
    }

    companion object {
        private const val TIMEOUT_MILLIS = 5_000L
    }
}