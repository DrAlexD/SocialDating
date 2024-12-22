package xelagurd.socialdating.ui.viewmodel

import javax.inject.Inject
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
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
import okio.IOException
import xelagurd.socialdating.data.fake.FakeDataSource
import xelagurd.socialdating.data.local.repository.LocalStatementsRepository
import xelagurd.socialdating.data.network.repository.RemoteStatementsRepository
import xelagurd.socialdating.ui.navigation.StatementsDestination
import xelagurd.socialdating.ui.state.StatementsUiState
import xelagurd.socialdating.ui.state.InternetStatus

@HiltViewModel
class StatementsViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val remoteRepository: RemoteStatementsRepository,
    private val localRepository: LocalStatementsRepository
) : ViewModel() {
    var internetStatus by mutableStateOf(InternetStatus.LOADING)
        private set

    private val categoryId: Int = checkNotNull(savedStateHandle[StatementsDestination.categoryId])

    val uiState: StateFlow<StatementsUiState> = localRepository.getStatements(categoryId)
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

                delay(3000L) // FixMe: remove after implementing server

                val remoteStatements = remoteRepository.getStatements(categoryId)
                localRepository.insertStatements(remoteStatements)
                internetStatus = InternetStatus.ONLINE
            } catch (_: IOException) {
                localRepository.insertStatements(FakeDataSource.statements) // FixMe: remove after implementing server
                internetStatus = InternetStatus.OFFLINE
            }
        }
    }

    companion object {
        private const val TIMEOUT_MILLIS = 5_000L
    }
}