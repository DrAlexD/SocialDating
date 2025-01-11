package xelagurd.socialdating.ui.viewmodel

import java.io.IOException
import javax.inject.Inject
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import xelagurd.socialdating.PreferencesRepository
import xelagurd.socialdating.data.fake.FAKE_SERVER_LATENCY
import xelagurd.socialdating.data.fake.FakeDataSource
import xelagurd.socialdating.data.local.repository.LocalDefiningThemesRepository
import xelagurd.socialdating.data.local.repository.LocalStatementsRepository
import xelagurd.socialdating.data.model.additional.StatementDetails
import xelagurd.socialdating.data.remote.repository.RemoteStatementsRepository
import xelagurd.socialdating.ui.navigation.StatementAddingDestination
import xelagurd.socialdating.ui.state.StatementAddingUiState
import xelagurd.socialdating.ui.state.RequestStatus

@HiltViewModel
class StatementAddingViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val localDefiningThemesRepository: LocalDefiningThemesRepository,
    private val remoteStatementsRepository: RemoteStatementsRepository,
    private val localStatementsRepository: LocalStatementsRepository,
    private val preferencesRepository: PreferencesRepository
) : ViewModel() {
    private val categoryId: Int = checkNotNull(savedStateHandle[StatementAddingDestination.categoryId])

    private val _uiState = MutableStateFlow(StatementAddingUiState())
    val uiState = _uiState.asStateFlow()

    init {
        initCurrentUserId()
        initDefiningThemes()
    }

    fun initCurrentUserId() {
        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    statementDetails = it.statementDetails.copy(
                        userId = preferencesRepository.currentUserId.first()
                    )
                )
            }
        }
    }

    fun initDefiningThemes() {
        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    definingThemes = localDefiningThemesRepository.getDefiningThemes(categoryId)
                        .first()
                )
            }
        }
    }

    fun updateUiState(statementDetails: StatementDetails) {
        _uiState.update {
            it.copy(statementDetails = statementDetails)
        }
    }

    fun statementAdding() {
        viewModelScope.launch {
            try {
                _uiState.update { it.copy(requestStatus = RequestStatus.LOADING) }

                delay(FAKE_SERVER_LATENCY) // FixMe: remove after implementing server

                val statementDetails = uiState.value.statementDetails
                val statement = remoteStatementsRepository.statementAdding(statementDetails)

                if (statement != null) {
                    localStatementsRepository.insertStatement(statement)

                    _uiState.update { it.copy(requestStatus = RequestStatus.SUCCESS) }
                } else {
                    _uiState.update { it.copy(requestStatus = RequestStatus.FAILED) }
                }
            } catch (_: IOException) {
                localStatementsRepository.insertStatement(FakeDataSource.newStatement) // TODO: remove after implementing server

                _uiState.update { it.copy(requestStatus = RequestStatus.SUCCESS) } // TODO: Change to ERROR after implementing server
            }
        }
    }
}