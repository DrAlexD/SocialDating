package xelagurd.socialdating.ui.viewmodel

import java.io.IOException
import javax.inject.Inject
import kotlin.collections.map
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import xelagurd.socialdating.data.fake.FakeDataSource
import xelagurd.socialdating.data.local.repository.LocalDefiningThemesRepository
import xelagurd.socialdating.data.local.repository.LocalStatementsRepository
import xelagurd.socialdating.data.local.repository.LocalUsersRepository
import xelagurd.socialdating.data.model.StatementReactionType
import xelagurd.socialdating.data.remote.repository.RemoteDefiningThemesRepository
import xelagurd.socialdating.data.remote.repository.RemoteStatementsRepository
import xelagurd.socialdating.ui.navigation.StatementsDestination
import xelagurd.socialdating.ui.state.InternetStatus
import xelagurd.socialdating.ui.state.StatementsUiState

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class StatementsViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val remoteStatementsRepository: RemoteStatementsRepository,
    private val localStatementsRepository: LocalStatementsRepository,
    private val remoteDefiningThemesRepository: RemoteDefiningThemesRepository,
    private val localDefiningThemesRepository: LocalDefiningThemesRepository,
    private val localUsersRepository: LocalUsersRepository
) : ViewModel() {
    private val categoryId: Int = checkNotNull(savedStateHandle[StatementsDestination.categoryId])

    private val internetStatusFlow = MutableStateFlow(InternetStatus.LOADING)
    private val statementsFlow = localDefiningThemesRepository.getDefiningThemes(categoryId)
        .distinctUntilChanged()
        .flatMapLatest { definingThemes ->
            val definingThemeIds = definingThemes.map { it.id }.distinct()
            localStatementsRepository.getStatements(definingThemeIds)
                .distinctUntilChanged()
        }

    val uiState = combine(statementsFlow, internetStatusFlow) { statements, internetStatus ->
        StatementsUiState(
            statements = statements,
            internetStatus = internetStatus
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(TIMEOUT_MILLIS),
        initialValue = StatementsUiState()
    )

    init {
        getStatements()
    }

    fun getStatements() {
        viewModelScope.launch {
            try {
                internetStatusFlow.update { InternetStatus.LOADING }

                localUsersRepository.insertUser(FakeDataSource.users[0]) // TODO: Remove after adding login screen

                delay(3000L) // FixMe: remove after implementing server

                val remoteDefiningThemes = remoteDefiningThemesRepository
                    .getDefiningThemes(categoryId)
                localDefiningThemesRepository.insertDefiningThemes(remoteDefiningThemes)

                val remoteDefiningThemeIds = remoteDefiningThemes.map { it.id }
                val remoteStatements = remoteStatementsRepository
                    .getStatements(remoteDefiningThemeIds)
                localStatementsRepository.insertStatements(remoteStatements)

                internetStatusFlow.update { InternetStatus.ONLINE }
            } catch (_: IOException) {
                localDefiningThemesRepository.insertDefiningThemes(FakeDataSource.definingThemes) // FixMe: remove after implementing server
                localStatementsRepository.insertStatements(FakeDataSource.statements) // FixMe: remove after implementing server

                internetStatusFlow.update { InternetStatus.OFFLINE }
            }
        }
    }

    fun onStatementReactionClick(statementId: Int, reactionType: StatementReactionType) {
        viewModelScope.launch {
            try {
                remoteStatementsRepository.postStatementReaction(
                    1, // TODO: Remove after adding login screen
                    statementId,
                    reactionType
                )
            } catch (_: IOException) {
                //
            }
        }
    }

    companion object {
        private const val TIMEOUT_MILLIS = 5_000L
    }
}