package xelagurd.socialdating.client.ui.viewmodel

import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import xelagurd.socialdating.client.data.fake.FakeDataSource
import xelagurd.socialdating.client.data.local.repository.LocalCategoriesRepository
import xelagurd.socialdating.client.data.remote.repository.RemoteCategoriesRepository
import xelagurd.socialdating.client.data.safeApiCall
import xelagurd.socialdating.client.ui.state.CategoriesUiState
import xelagurd.socialdating.client.ui.state.RequestStatus

@HiltViewModel
class CategoriesViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val remoteRepository: RemoteCategoriesRepository,
    private val localRepository: LocalCategoriesRepository
) : ViewModel() {
    private val dataRequestStatusFlow = MutableStateFlow<RequestStatus>(RequestStatus.UNDEFINED)
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
            dataRequestStatusFlow.update { RequestStatus.LOADING }

            val (remoteCategories, status) = safeApiCall(context) {
                remoteRepository.getCategories()
            }

            if (remoteCategories != null) {
                localRepository.insertCategories(remoteCategories)
            } else { // FixMe: remove after implementing server
                if (localRepository.getCategories().first().isEmpty()) {
                    localRepository.insertCategories(FakeDataSource.categories)
                }
            }

            dataRequestStatusFlow.update { status }
        }
    }

    companion object {
        private const val TIMEOUT_MILLIS = 5_000L
    }
}