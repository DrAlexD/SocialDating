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
import xelagurd.socialdating.data.fake.FAKE_SERVER_LATENCY
import xelagurd.socialdating.data.fake.FakeDataSource
import xelagurd.socialdating.data.local.repository.LocalCategoriesRepository
import xelagurd.socialdating.data.remote.repository.RemoteCategoriesRepository
import xelagurd.socialdating.ui.state.CategoriesUiState
import xelagurd.socialdating.ui.state.RequestStatus

@HiltViewModel
class CategoriesViewModel @Inject constructor(
    private val remoteRepository: RemoteCategoriesRepository,
    private val localRepository: LocalCategoriesRepository
) : ViewModel() {
    private val dataRequestStatusFlow = MutableStateFlow(RequestStatus.UNDEFINED)
    private val categoriesFlow = localRepository.getCategories().distinctUntilChanged()

    val uiState = combine(categoriesFlow, dataRequestStatusFlow) { categories, dataRequestStatus ->
        CategoriesUiState(
            entities = categories,
            dataRequestStatus = dataRequestStatus
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
                dataRequestStatusFlow.update { RequestStatus.LOADING }

                delay(FAKE_SERVER_LATENCY) // FixMe: remove after implementing server

                val remoteCategories = remoteRepository.getCategories()
                localRepository.insertCategories(remoteCategories)

                dataRequestStatusFlow.update { RequestStatus.SUCCESS }
            } catch (_: IOException) {
                localRepository.insertCategories(FakeDataSource.categories) // FixMe: remove after implementing server
                dataRequestStatusFlow.update { RequestStatus.ERROR }
            }
        }
    }

    companion object {
        private const val TIMEOUT_MILLIS = 5_000L
    }
}