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
import kotlinx.coroutines.runBlocking
import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import xelagurd.socialdating.client.data.PreferencesRepository
import xelagurd.socialdating.client.data.local.repository.LocalCategoriesRepository
import xelagurd.socialdating.client.data.model.DataUtils.TIMEOUT_MILLIS
import xelagurd.socialdating.client.data.remote.ApiUtils.safeApiCall
import xelagurd.socialdating.client.data.remote.repository.RemoteCategoriesRepository
import xelagurd.socialdating.client.ui.state.CategoriesUiState
import xelagurd.socialdating.client.ui.state.RequestStatus

@HiltViewModel
class CategoriesViewModel @Inject constructor(
    @param:ApplicationContext private val context: Context,
    private val preferencesRepository: PreferencesRepository,
    private val remoteCategoriesRepository: RemoteCategoriesRepository,
    private val localCategoriesRepository: LocalCategoriesRepository
) : ViewModel() {

    private val isOfflineMode = runBlocking { preferencesRepository.isOfflineMode.first() }

    private val dataRequestStatusFlow = MutableStateFlow<RequestStatus>(RequestStatus.UNDEFINED)
    private val categoriesFlow = localCategoriesRepository.getCategories().distinctUntilChanged()

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
        if (!isOfflineMode) { // FixMe: remove after adding server hosting
            getCategories()
        } else {
            dataRequestStatusFlow.update { RequestStatus.SUCCESS }
        }
    }

    fun getCategories() {
        viewModelScope.launch {
            dataRequestStatusFlow.update { RequestStatus.LOADING }

            val (remoteCategories, status) = safeApiCall(context) {
                remoteCategoriesRepository.getCategories()
            }

            if (remoteCategories != null) {
                localCategoriesRepository.insertCategories(remoteCategories)
            }

            dataRequestStatusFlow.update { status }
        }
    }
}