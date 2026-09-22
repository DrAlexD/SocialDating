package xelagurd.socialdating.client.ui.viewmodel

import javax.inject.Inject
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import android.content.Context
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import xelagurd.socialdating.client.data.PreferencesRepository
import xelagurd.socialdating.client.data.fake.FakeData
import xelagurd.socialdating.client.data.model.DataUtils.TIMEOUT_MILLIS
import xelagurd.socialdating.client.data.model.dto.SimilarUserDto
import xelagurd.socialdating.client.data.remote.ApiUtils.offlineModeStatus
import xelagurd.socialdating.client.data.remote.ApiUtils.safeApiCall
import xelagurd.socialdating.client.data.remote.repository.RemoteUserCategoriesRepository
import xelagurd.socialdating.client.ui.navigation.SimilarUsersDestination
import xelagurd.socialdating.client.ui.state.RequestStatus
import xelagurd.socialdating.client.ui.state.SimilarUsersUiState
import xelagurd.socialdating.client.ui.state.hideWhileLoading
import xelagurd.socialdating.client.ui.state.updatePageLoadingNotification

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class SimilarUsersViewModel @Inject constructor(
    @param:ApplicationContext private val context: Context,
    savedStateHandle: SavedStateHandle,
    private val preferencesRepository: PreferencesRepository,
    private val remoteUserCategoriesRepository: RemoteUserCategoriesRepository
) : ViewModel() {

    private val userId: Int = checkNotNull(savedStateHandle[SimilarUsersDestination.userId])
    private val isOfflineMode = runBlocking { preferencesRepository.isOfflineMode.first() }

    private val dataRequestStatusFlow = MutableStateFlow<RequestStatus>(RequestStatus.UNDEFINED)
    private val nextPageRequestStatusFlow = MutableStateFlow<RequestStatus>(RequestStatus.UNDEFINED)
    private val isLastPageFlow = MutableStateFlow(false)
    private val notificationFlow = MutableStateFlow<String?>(null)
    private val similarUsersFlow = MutableStateFlow<List<SimilarUserDto>>(listOf())

    // the paging session state, it is dropped on every screen entry and refresh
    private var nextCursor: String? = null

    val uiState = combine(
        similarUsersFlow,
        dataRequestStatusFlow,
        nextPageRequestStatusFlow,
        isLastPageFlow,
        notificationFlow
    ) { similarUsers, dataRequestStatus, nextPageRequestStatus, isLastPage, notification ->
        SimilarUsersUiState(
            entities = similarUsers.hideWhileLoading(dataRequestStatus),
            dataRequestStatus = dataRequestStatus,
            nextPageRequestStatus = nextPageRequestStatus,
            isLastPage = isLastPage,
            notification = notification
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(TIMEOUT_MILLIS),
        initialValue = SimilarUsersUiState()
    )

    init {
        if (!isOfflineMode) { // FixMe: remove after adding server hosting
            loadSimilarUsers(isRequestedByUser = false)
        } else {
            similarUsersFlow.update { FakeData.similarUsers }
            isLastPageFlow.update { true }
            dataRequestStatusFlow.update { offlineModeStatus(context) }
        }

        viewModelScope.launch {
            preferencesRepository.languageChanges.collect { loadSimilarUsers(isRequestedByUser = false) }
        }
    }

    fun onNotificationShown() = notificationFlow.update { null }

    fun getSimilarUsers() = loadSimilarUsers(isRequestedByUser = true)

    private fun loadSimilarUsers(isRequestedByUser: Boolean) {
        if (isOfflineMode) { // FixMe: remove after adding server hosting
            if (isRequestedByUser) {
                notificationFlow.update { offlineModeStatus(context).notificationText() }
            }
            return
        }

        nextCursor = null
        isLastPageFlow.update { false }
        nextPageRequestStatusFlow.update { RequestStatus.UNDEFINED }

        getSimilarUsersPage(isFirstPage = true, isRequestedByUser = isRequestedByUser)
    }

    fun getNextSimilarUsers() {
        if (isOfflineMode) return // FixMe: remove after adding server hosting
        if (isLastPageFlow.value) return
        if (dataRequestStatusFlow.value !is RequestStatus.SUCCESS) return
        if (nextPageRequestStatusFlow.value is RequestStatus.LOADING) return

        getSimilarUsersPage(isFirstPage = false, isRequestedByUser = false)
    }

    private fun getSimilarUsersPage(isFirstPage: Boolean, isRequestedByUser: Boolean) {
        viewModelScope.launch {
            updateRequestStatus(isFirstPage, RequestStatus.LOADING)

            val (similarUsersPage, status) = safeApiCall(context) {
                remoteUserCategoriesRepository.getSimilarUsers(userId, cursor = nextCursor)
            }

            when {
                similarUsersPage != null -> {
                    similarUsersFlow.update {
                        when {
                            isFirstPage -> similarUsersPage.content
                            // the similarity is recalculated on every request, so a user whose one has grown
                            // between the pages can be returned again, and the list requires unique ids
                            else -> (it + similarUsersPage.content).distinctBy { similarUser -> similarUser.id }
                        }
                    }

                    nextCursor = similarUsersPage.nextCursor
                    isLastPageFlow.update { similarUsersPage.nextCursor == null }
                }
                // no content means that there are no similar users to load anymore
                status is RequestStatus.SUCCESS -> {
                    if (isFirstPage) similarUsersFlow.update { listOf() }
                    isLastPageFlow.update { true }
                }
            }

            updateRequestStatus(isFirstPage, status)
            notificationFlow.updatePageLoadingNotification(
                requestStatus = status,
                isFirstPage = isFirstPage,
                isRequestedByUser = isRequestedByUser,
                isDataExist = { similarUsersFlow.value.isNotEmpty() }
            )
        }
    }

    private fun updateRequestStatus(isFirstPage: Boolean, requestStatus: RequestStatus) {
        when {
            isFirstPage -> dataRequestStatusFlow.update { requestStatus }
            else -> nextPageRequestStatusFlow.update { requestStatus }
        }
    }
}
