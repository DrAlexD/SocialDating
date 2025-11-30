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
import xelagurd.socialdating.client.data.local.repository.LocalDefiningThemesRepository
import xelagurd.socialdating.client.data.local.repository.LocalStatementsRepository
import xelagurd.socialdating.client.data.model.DataUtils.TIMEOUT_MILLIS
import xelagurd.socialdating.client.data.model.Statement
import xelagurd.socialdating.client.data.model.additional.StatementReactionDetails
import xelagurd.socialdating.client.data.model.enums.StatementReactionType
import xelagurd.socialdating.client.data.remote.ApiUtils.safeApiCall
import xelagurd.socialdating.client.data.remote.repository.RemoteDefiningThemesRepository
import xelagurd.socialdating.client.data.remote.repository.RemoteStatementsRepository
import xelagurd.socialdating.client.ui.navigation.StatementsDestination
import xelagurd.socialdating.client.ui.state.RequestStatus
import xelagurd.socialdating.client.ui.state.StatementsUiState

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class StatementsViewModel @Inject constructor(
    @param:ApplicationContext private val context: Context,
    savedStateHandle: SavedStateHandle,
    private val preferencesRepository: PreferencesRepository,
    private val remoteStatementsRepository: RemoteStatementsRepository,
    private val localStatementsRepository: LocalStatementsRepository,
    private val remoteDefiningThemesRepository: RemoteDefiningThemesRepository,
    private val localDefiningThemesRepository: LocalDefiningThemesRepository
) : ViewModel() {

    private val userId: Int = checkNotNull(savedStateHandle[StatementsDestination.userId])

    private val categoryId: Int = checkNotNull(savedStateHandle[StatementsDestination.categoryId])
    private val isOfflineMode = runBlocking { preferencesRepository.isOfflineMode.first() }

    private val dataRequestStatusFlow = MutableStateFlow<RequestStatus>(RequestStatus.UNDEFINED)
    private val statementsFlow = localStatementsRepository.getStatements(categoryId)
        .distinctUntilChanged()

    val uiState = combine(statementsFlow, dataRequestStatusFlow) { statements, dataRequestStatus ->
        StatementsUiState(
            categoryId = categoryId,
            entities = statements,
            dataRequestStatus = dataRequestStatus
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
            dataRequestStatusFlow.update { RequestStatus.SUCCESS }
        }
    }

    fun getStatements() {
        viewModelScope.launch {
            var globalStatus: RequestStatus = RequestStatus.LOADING

            dataRequestStatusFlow.update { globalStatus }

            val (remoteDefiningThemes, statusDefiningThemes) = safeApiCall(context) {
                remoteDefiningThemesRepository.getDefiningThemes(categoryId)
            }

            if (remoteDefiningThemes != null) {
                localDefiningThemesRepository.insertDefiningThemes(remoteDefiningThemes)

                val remoteDefiningThemeIds = remoteDefiningThemes.map { it.id }
                val (remoteStatements, statusStatements) = safeApiCall(context) {
                    remoteStatementsRepository.getStatements(userId, remoteDefiningThemeIds)
                }

                if (remoteStatements != null) {
                    localStatementsRepository.replaceStatements(categoryId, remoteStatements)
                }

                globalStatus = statusStatements
            } else {
                globalStatus = statusDefiningThemes
            }

            dataRequestStatusFlow.update { globalStatus }
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
                            categoryId = categoryId,
                            definingThemeId = statement.definingThemeId,
                            reactionType = reactionType,
                            isSupportDefiningTheme = statement.isSupportDefiningTheme
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