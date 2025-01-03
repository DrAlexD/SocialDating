package xelagurd.socialdating.ui.viewmodel

import java.io.IOException
import javax.inject.Inject
import kotlin.collections.map
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
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
    var internetStatus by mutableStateOf(InternetStatus.LOADING)
        private set

    private val categoryId: Int = checkNotNull(savedStateHandle[StatementsDestination.categoryId])

    val uiState: StateFlow<StatementsUiState> = localDefiningThemesRepository
        .getDefiningThemes(categoryId)
        .map { definingThemes ->
            val definingThemeIds = definingThemes.map { it.id }
            localStatementsRepository.getStatements(definingThemeIds)
        }
        .flatMapLatest { it }
        .map { StatementsUiState(it) }
        .stateIn(
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
                internetStatus = InternetStatus.LOADING

                localUsersRepository.insertUser(FakeDataSource.users[0]) // TODO: Remove after adding login screen

                delay(3000L) // FixMe: remove after implementing server

                val remoteDefiningThemes = remoteDefiningThemesRepository
                    .getDefiningThemes(categoryId)
                localDefiningThemesRepository.insertDefiningThemes(remoteDefiningThemes)

                val remoteDefiningThemeIds = remoteDefiningThemes.map { it.id }
                val remoteStatements = remoteStatementsRepository
                    .getStatements(remoteDefiningThemeIds)
                localStatementsRepository.insertStatements(remoteStatements)

                internetStatus = InternetStatus.ONLINE
            } catch (_: IOException) {
                localDefiningThemesRepository.insertDefiningThemes(FakeDataSource.definingThemes) // FixMe: remove after implementing server
                localStatementsRepository.insertStatements(FakeDataSource.statements) // FixMe: remove after implementing server

                internetStatus = InternetStatus.OFFLINE
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