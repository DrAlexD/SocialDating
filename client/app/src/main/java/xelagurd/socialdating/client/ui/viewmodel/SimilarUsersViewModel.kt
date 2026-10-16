package xelagurd.socialdating.client.ui.viewmodel

import javax.inject.Inject
import kotlinx.coroutines.ExperimentalCoroutinesApi
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
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import xelagurd.socialdating.client.data.PreferencesRepository
import xelagurd.socialdating.client.data.fake.FakeData
import xelagurd.socialdating.client.data.local.repository.LocalCategoriesRepository
import xelagurd.socialdating.client.data.model.DataUtils.TIMEOUT_MILLIS
import xelagurd.socialdating.client.data.model.details.SimilarUsersFilterDetails
import xelagurd.socialdating.client.data.model.dto.SimilarUserDto
import xelagurd.socialdating.client.data.remote.ApiUtils.offlineModeStatus
import xelagurd.socialdating.client.data.remote.ApiUtils.safeApiCall
import xelagurd.socialdating.client.data.remote.repository.RemoteUserCategoriesRepository
import xelagurd.socialdating.client.ui.form.SimilarUsersFilterFormData
import xelagurd.socialdating.client.ui.form.toSimilarUsersFilterFormData
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
    private val remoteUserCategoriesRepository: RemoteUserCategoriesRepository,
    localCategoriesRepository: LocalCategoriesRepository
) : ViewModel() {

    private val userId: Int = checkNotNull(savedStateHandle[SimilarUsersDestination.userId])
    private val isOfflineMode = runBlocking { preferencesRepository.isOfflineMode.first() }

    // the similar users are calculated only on the user request
    private val dataRequestStatusFlow = MutableStateFlow<RequestStatus>(RequestStatus.NOT_REQUESTED)
    private val isDataRequestedByPullFlow = MutableStateFlow(false)
    private val nextPageRequestStatusFlow = MutableStateFlow<RequestStatus>(RequestStatus.UNDEFINED)
    private val isLastPageFlow = MutableStateFlow(false)
    private val notificationFlow = MutableStateFlow<String?>(null)
    private val similarUsersFlow = MutableStateFlow<List<SimilarUserDto>>(listOf())
    private val categoriesFlow = localCategoriesRepository.getCategories().distinctUntilChanged()
    private val formDataFlow = MutableStateFlow(
        runBlocking { preferencesRepository.similarUsersFilterDetails.first() }.toSimilarUsersFilterFormData()
    )

    // the paging session state, it is dropped on every screen entry and calculation
    private var nextCursor: String? = null

    // the filter can be changed after the calculation, but the next pages have to be loaded with the calculated one,
    // which is the filter of the displayed result, so it is kept by a failed calculation over the displayed users
    private val calculatedFilterDetailsFlow = MutableStateFlow(formDataFlow.value.toSimilarUsersFilterDetails())

    // the typed combine is limited by five flows, so the paging state is combined separately
    private val pagedSimilarUsersUiStateFlow = combine(
        similarUsersFlow,
        dataRequestStatusFlow,
        isDataRequestedByPullFlow,
        nextPageRequestStatusFlow,
        isLastPageFlow
    ) { similarUsers, dataRequestStatus, isDataRequestedByPull, nextPageRequestStatus, isLastPage ->
        SimilarUsersUiState(
            entities = similarUsers.hideWhileLoading(dataRequestStatus),
            dataRequestStatus = dataRequestStatus,
            isDataRequestedByPull = isDataRequestedByPull,
            nextPageRequestStatus = nextPageRequestStatus,
            isLastPage = isLastPage
        )
    }

    val uiState = combine(
        pagedSimilarUsersUiStateFlow,
        categoriesFlow,
        formDataFlow,
        calculatedFilterDetailsFlow,
        notificationFlow
    ) { pagedSimilarUsersUiState, categories, formData, calculatedFilterDetails, notification ->
        pagedSimilarUsersUiState.copy(
            categories = categories,
            formData = formData,
            calculatedFilterDetails = calculatedFilterDetails,
            notification = notification
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(TIMEOUT_MILLIS),
        initialValue = SimilarUsersUiState(formData = formDataFlow.value)
    )

    init {
        viewModelScope.launch {
            preferencesRepository.languageChanges.collect {
                // the calculated similar users contain the localized texts, so they are recalculated
                // with the same filter, but the similar users are not calculated without the user request
                if (dataRequestStatusFlow.value !is RequestStatus.NOT_REQUESTED) {
                    loadSimilarUsers(
                        filterDetails = calculatedFilterDetailsFlow.value,
                        isRequestedByUser = false,
                        isRequestedByPull = false
                    )
                }
            }
        }
    }

    fun onNotificationShown() = notificationFlow.update { null }

    fun updateUiState(similarUsersFilterFormData: SimilarUsersFilterFormData) {
        formDataFlow.update { similarUsersFilterFormData }

        // the incorrect filter is not saved, so the last correct one is restored on the next screen entry
        if (similarUsersFilterFormData.isValid) {
            viewModelScope.launch {
                preferencesRepository.saveSimilarUsersFilterDetails(
                    similarUsersFilterFormData.toSimilarUsersFilterDetails()
                )
            }
        }
    }

    fun calculateSimilarUsers() {
        if (dataRequestStatusFlow.value is RequestStatus.LOADING) return

        // the incorrect filter can't be sent, its error is displayed in the filter instead
        val similarUsersFilterFormData = formDataFlow.value
        if (!similarUsersFilterFormData.isValid) return

        loadSimilarUsers(
            filterDetails = similarUsersFilterFormData.toSimilarUsersFilterDetails(),
            isRequestedByUser = true,
            isRequestedByPull = false
        )
    }

    fun refreshSimilarUsers() {
        if (dataRequestStatusFlow.value is RequestStatus.LOADING) return

        // the displayed similar users are refreshed, so the filter changed after their calculation is not applied
        loadSimilarUsers(
            filterDetails = calculatedFilterDetailsFlow.value,
            isRequestedByUser = true,
            isRequestedByPull = true
        )
    }

    private fun loadSimilarUsers(
        filterDetails: SimilarUsersFilterDetails,
        isRequestedByUser: Boolean,
        isRequestedByPull: Boolean
    ) {
        if (isOfflineMode) { // FixMe: remove after adding server hosting
            similarUsersFlow.update { FakeData.filterSimilarUsers(filterDetails) }
            calculatedFilterDetailsFlow.update { filterDetails }
            isLastPageFlow.update { true }
            dataRequestStatusFlow.update { offlineModeStatus(context) }
            if (isRequestedByUser) {
                notificationFlow.update { offlineModeStatus(context).notificationText() }
            }
            return
        }

        nextCursor = null
        isLastPageFlow.update { false }
        nextPageRequestStatusFlow.update { RequestStatus.UNDEFINED }
        isDataRequestedByPullFlow.update { isRequestedByPull }

        getSimilarUsersPage(filterDetails = filterDetails, isFirstPage = true, isRequestedByUser = isRequestedByUser)
    }

    fun getNextSimilarUsers() {
        if (isOfflineMode) return // FixMe: remove after adding server hosting
        if (isLastPageFlow.value) return
        if (dataRequestStatusFlow.value !is RequestStatus.SUCCESS) return
        if (nextPageRequestStatusFlow.value is RequestStatus.LOADING) return

        getSimilarUsersPage(
            filterDetails = calculatedFilterDetailsFlow.value,
            isFirstPage = false,
            isRequestedByUser = false
        )
    }

    private fun getSimilarUsersPage(
        filterDetails: SimilarUsersFilterDetails,
        isFirstPage: Boolean,
        isRequestedByUser: Boolean
    ) {
        viewModelScope.launch {
            updateRequestStatus(isFirstPage, RequestStatus.LOADING)

            val (similarUsersPage, status) = safeApiCall(context) {
                remoteUserCategoriesRepository.getSimilarUsers(userId, filterDetails, nextCursor)
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

            val isDataExist = similarUsersFlow.value.isNotEmpty()

            // the displayed similar users are replaced only by the successful first page, otherwise they stay
            // found with the previous filter, while without them the result of this filter is the displayed error
            if (isFirstPage && (status is RequestStatus.SUCCESS || !isDataExist)) {
                calculatedFilterDetailsFlow.update { filterDetails }
            }

            updateRequestStatus(isFirstPage, status)
            notificationFlow.updatePageLoadingNotification(
                requestStatus = status,
                isFirstPage = isFirstPage,
                isRequestedByUser = isRequestedByUser,
                isDataExist = { isDataExist }
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
