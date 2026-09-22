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
import xelagurd.socialdating.client.data.remote.ApiUtils.offlineModeStatus
import xelagurd.socialdating.client.data.remote.ApiUtils.safeApiCall
import xelagurd.socialdating.client.data.remote.repository.RemoteCategoriesRepository
import xelagurd.socialdating.client.ui.state.CategoriesUiState
import xelagurd.socialdating.client.ui.state.RequestStatus
import xelagurd.socialdating.client.ui.state.updateLoadingNotification
import xelagurd.socialdating.client.ui.state.hideWhileLoading

@HiltViewModel
class CategoriesViewModel @Inject constructor(
    @param:ApplicationContext private val context: Context,
    private val preferencesRepository: PreferencesRepository,
    private val remoteCategoriesRepository: RemoteCategoriesRepository,
    private val localCategoriesRepository: LocalCategoriesRepository
) : ViewModel() {

    private val isOfflineMode = runBlocking { preferencesRepository.isOfflineMode.first() }

    private val dataRequestStatusFlow = MutableStateFlow<RequestStatus>(RequestStatus.UNDEFINED)
    private val notificationFlow = MutableStateFlow<String?>(null)
    private val categoriesFlow = localCategoriesRepository.getCategories().distinctUntilChanged()

    val uiState = combine(
        categoriesFlow,
        dataRequestStatusFlow,
        notificationFlow
    ) { categories, dataRequestStatus, notification ->
        CategoriesUiState(
            entities = categories.hideWhileLoading(dataRequestStatus),
            dataRequestStatus = dataRequestStatus,
            notification = notification
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(TIMEOUT_MILLIS),
        initialValue = CategoriesUiState()
    )

    init {
        if (!isOfflineMode) { // FixMe: remove after adding server hosting
            loadCategories(isRequestedByUser = false)
        } else {
            dataRequestStatusFlow.update { offlineModeStatus(context) }
        }

        viewModelScope.launch {
            preferencesRepository.languageChanges.collect { loadCategories(isRequestedByUser = false) }
        }
    }

    fun onNotificationShown() = notificationFlow.update { null }

    fun getCategories() = loadCategories(isRequestedByUser = true)

    private fun loadCategories(isRequestedByUser: Boolean) {
        if (isOfflineMode) { // FixMe: remove after adding server hosting
            if (isRequestedByUser) {
                notificationFlow.update { offlineModeStatus(context).notificationText() }
            }
            return
        }

        viewModelScope.launch {
            dataRequestStatusFlow.update { RequestStatus.LOADING }

            val (remoteCategories, status) = safeApiCall(context) {
                remoteCategoriesRepository.getCategories()
            }

            if (remoteCategories != null) {
                localCategoriesRepository.insertCategories(remoteCategories)
            }

            dataRequestStatusFlow.update { status }
            notificationFlow.updateLoadingNotification(
                requestStatus = status,
                isRequestedByUser = isRequestedByUser,
                isDataExist = categoriesFlow.first().isNotEmpty()
            )
        }
    }
}