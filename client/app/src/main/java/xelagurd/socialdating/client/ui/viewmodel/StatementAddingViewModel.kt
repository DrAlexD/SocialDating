package xelagurd.socialdating.client.ui.viewmodel

import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import android.content.Context
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import xelagurd.socialdating.client.R
import xelagurd.socialdating.client.data.PreferencesRepository
import xelagurd.socialdating.client.data.fake.FakeDataSource
import xelagurd.socialdating.client.data.local.repository.LocalDefiningThemesRepository
import xelagurd.socialdating.client.data.local.repository.LocalStatementsRepository
import xelagurd.socialdating.client.data.model.details.StatementDetails
import xelagurd.socialdating.client.data.remote.repository.RemoteStatementsRepository
import xelagurd.socialdating.client.data.safeApiCall
import xelagurd.socialdating.client.ui.navigation.StatementAddingDestination
import xelagurd.socialdating.client.ui.state.RequestStatus
import xelagurd.socialdating.client.ui.state.StatementAddingUiState

@HiltViewModel
class StatementAddingViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    savedStateHandle: SavedStateHandle,
    private val localDefiningThemesRepository: LocalDefiningThemesRepository,
    private val remoteStatementsRepository: RemoteStatementsRepository,
    private val localStatementsRepository: LocalStatementsRepository,
    private val preferencesRepository: PreferencesRepository
) : ViewModel() {
    private val categoryId: Int =
        checkNotNull(savedStateHandle[StatementAddingDestination.categoryId])

    private val _uiState = MutableStateFlow(StatementAddingUiState())
    val uiState = _uiState.asStateFlow()

    init {
        initCurrentUserId()
        initDefiningThemes()
    }

    private fun initCurrentUserId() {
        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    formDetails = it.formDetails.copy(
                        creatorUserId = preferencesRepository.currentUserId.first()
                    )
                )
            }
        }
    }

    private fun initDefiningThemes() {
        viewModelScope.launch {
            _uiState.update { it.copy(dataRequestStatus = RequestStatus.LOADING) }

            val definingThemes = localDefiningThemesRepository.getDefiningThemes(categoryId).first()

            if (definingThemes.isNotEmpty()) {
                _uiState.update {
                    it.copy(
                        entities = definingThemes,
                        dataRequestStatus = RequestStatus.SUCCESS
                    )
                }
            } else {
                _uiState.update {
                    it.copy(
                        dataRequestStatus = RequestStatus.FAILURE(
                            failureText = context.getString(R.string.no_data)
                        )
                    )
                }
            }
        }
    }

    fun updateUiState(statementDetails: StatementDetails) {
        _uiState.update {
            it.copy(formDetails = statementDetails)
        }
    }

    fun statementAdding() {
        viewModelScope.launch {
            _uiState.update { it.copy(actionRequestStatus = RequestStatus.LOADING) }

            val statementDetails = uiState.value.formDetails

            val (statement, status) = safeApiCall(context) {
                remoteStatementsRepository.addStatement(statementDetails)
            }

            if (statement != null) {
                localStatementsRepository.insertStatement(statement)
            }

            if (status is RequestStatus.ERROR) { // FixMe: remove after implementing server
                if (!localStatementsRepository.getStatements().first().map { it.id }
                        .contains(FakeDataSource.newStatement.id)) {
                    localStatementsRepository.insertStatement(FakeDataSource.newStatement)
                }

                _uiState.update { it.copy(actionRequestStatus = RequestStatus.SUCCESS) }
            } else {
                _uiState.update { it.copy(actionRequestStatus = status) } // TODO: Move outside after implementing server
            }
        }
    }
}