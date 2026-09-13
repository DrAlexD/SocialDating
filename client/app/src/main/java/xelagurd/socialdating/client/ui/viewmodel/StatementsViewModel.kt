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
import xelagurd.socialdating.client.data.local.repository.CommonLocalRepository
import xelagurd.socialdating.client.data.local.repository.LocalStatementsRepository
import xelagurd.socialdating.client.data.model.DataUtils.TIMEOUT_MILLIS
import xelagurd.socialdating.client.data.model.DefaultDataProperties.ID_MIN
import xelagurd.socialdating.client.data.model.DefiningTheme
import xelagurd.socialdating.client.data.model.Statement
import xelagurd.socialdating.client.data.model.details.StatementReactionDetails
import xelagurd.socialdating.client.data.model.enums.StatementReactionType
import xelagurd.socialdating.client.data.remote.ApiUtils.offlineModeStatus
import xelagurd.socialdating.client.data.remote.ApiUtils.safeApiCall
import xelagurd.socialdating.client.data.remote.repository.RemoteDefiningThemesRepository
import xelagurd.socialdating.client.data.remote.repository.RemoteStatementsRepository
import xelagurd.socialdating.client.ui.navigation.StatementsDestination
import xelagurd.socialdating.client.ui.state.RequestStatus
import xelagurd.socialdating.client.ui.state.StatementsUiState
import xelagurd.socialdating.client.ui.state.hideWhileLoading

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class StatementsViewModel @Inject constructor(
    @param:ApplicationContext private val context: Context,
    savedStateHandle: SavedStateHandle,
    private val preferencesRepository: PreferencesRepository,
    private val remoteStatementsRepository: RemoteStatementsRepository,
    private val localStatementsRepository: LocalStatementsRepository,
    private val remoteDefiningThemesRepository: RemoteDefiningThemesRepository,
    private val commonLocalRepository: CommonLocalRepository
) : ViewModel() {

    private val userId: Int = checkNotNull(savedStateHandle[StatementsDestination.userId])

    private val categoryId: Int = checkNotNull(savedStateHandle[StatementsDestination.categoryId])
    private val isOfflineMode = runBlocking { preferencesRepository.isOfflineMode.first() }

    private val dataRequestStatusFlow = MutableStateFlow<RequestStatus>(RequestStatus.UNDEFINED)
    private val nextPageRequestStatusFlow = MutableStateFlow<RequestStatus>(RequestStatus.UNDEFINED)
    private val isLastPageFlow = MutableStateFlow(false)
    private val statementsFlow = localStatementsRepository.getStatements(categoryId)
        .distinctUntilChanged()

    // the paging session state, it is dropped on every screen entry, so the statements order is a new one
    private var definingThemes: List<DefiningTheme>? = null
    private var nextCursor: String? = null
    private var nextOrderNumber = ID_MIN

    val uiState = combine(
        statementsFlow,
        dataRequestStatusFlow,
        nextPageRequestStatusFlow,
        isLastPageFlow
    ) { statements, dataRequestStatus, nextPageRequestStatus, isLastPage ->
        StatementsUiState(
            categoryId = categoryId,
            entities = statements.hideWhileLoading(dataRequestStatus),
            dataRequestStatus = dataRequestStatus,
            nextPageRequestStatus = nextPageRequestStatus,
            isLastPage = isLastPage
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(TIMEOUT_MILLIS),
        initialValue = StatementsUiState()
    )

    init {
        if (!isOfflineMode) { // FixMe: remove after adding server hosting
            getStatements()
        } else {
            isLastPageFlow.update { true }
            dataRequestStatusFlow.update { offlineModeStatus(context) }
        }

        viewModelScope.launch {
            preferencesRepository.languageChanges.collect { getStatements() }
        }
    }

    fun getStatements() {
        if (isOfflineMode) return // FixMe: remove after adding server hosting

        definingThemes = null
        nextCursor = null
        nextOrderNumber = ID_MIN
        isLastPageFlow.update { false }
        nextPageRequestStatusFlow.update { RequestStatus.UNDEFINED }

        getStatementsPage(isFirstPage = true)
    }

    fun getNextStatements() {
        if (isOfflineMode) return // FixMe: remove after adding server hosting
        if (isLastPageFlow.value) return
        if (dataRequestStatusFlow.value !is RequestStatus.SUCCESS) return
        if (nextPageRequestStatusFlow.value is RequestStatus.LOADING) return

        getStatementsPage(isFirstPage = false)
    }

    private fun getStatementsPage(isFirstPage: Boolean) {
        viewModelScope.launch {
            updateRequestStatus(isFirstPage, RequestStatus.LOADING)

            val (remoteDefiningThemes, statusDefiningThemes) = definingThemes
                ?.let { it to RequestStatus.SUCCESS }
                ?: safeApiCall(context) {
                    remoteDefiningThemesRepository.getDefiningThemes(categoryId = categoryId)
                }

            if (remoteDefiningThemes == null) {
                updateRequestStatus(isFirstPage, statusDefiningThemes)
                return@launch
            }

            definingThemes = remoteDefiningThemes

            val (statementsPage, statusStatements) = safeApiCall(context) {
                remoteStatementsRepository.getStatements(
                    currentUserId = userId,
                    definingThemeIds = remoteDefiningThemes.map { it.id },
                    cursor = nextCursor
                )
            }

            when {
                statementsPage != null -> {
                    commonLocalRepository.updateStatementsScreenData(
                        remoteDefiningThemes,
                        categoryId,
                        statementsPage.content,
                        nextOrderNumber,
                        isFirstPage
                    )

                    nextOrderNumber += statementsPage.content.size
                    nextCursor = statementsPage.nextCursor
                    isLastPageFlow.update { statementsPage.nextCursor == null }
                }
                // no content means that there are no statements to load anymore
                statusStatements is RequestStatus.SUCCESS -> isLastPageFlow.update { true }
            }

            updateRequestStatus(isFirstPage, statusStatements)
        }
    }

    private fun updateRequestStatus(isFirstPage: Boolean, requestStatus: RequestStatus) {
        when {
            isFirstPage -> dataRequestStatusFlow.update { requestStatus }
            else -> nextPageRequestStatusFlow.update { requestStatus }
        }
    }

    fun onStatementReactionClick(statement: Statement, reactionType: StatementReactionType) {
        viewModelScope.launch {
            if (!isOfflineMode) { // FixMe: remove after adding server hosting
                val (_, status) = safeApiCall(context) {
                    remoteStatementsRepository.processStatementReaction(
                        StatementReactionDetails(
                            userId = userId,
                            statementId = statement.id,
                            reactionType = reactionType
                        )
                    )
                }

                if (status is RequestStatus.SUCCESS) {
                    localStatementsRepository.deleteStatement(statement)
                }

                // TODO: implement action on error
            } else {
                localStatementsRepository.deleteStatement(statement)
            }
        }
    }
}
