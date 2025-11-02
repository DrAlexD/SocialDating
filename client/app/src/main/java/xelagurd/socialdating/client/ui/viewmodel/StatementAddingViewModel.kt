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
import xelagurd.socialdating.client.data.fake.FakeData
import xelagurd.socialdating.client.data.local.repository.LocalDefiningThemesRepository
import xelagurd.socialdating.client.data.local.repository.LocalStatementsRepository
import xelagurd.socialdating.client.data.remote.repository.RemoteStatementsRepository
import xelagurd.socialdating.client.data.remote.safeApiCall
import xelagurd.socialdating.client.ui.form.StatementFormData
import xelagurd.socialdating.client.ui.navigation.StatementAddingDestination
import xelagurd.socialdating.client.ui.state.RequestStatus
import xelagurd.socialdating.client.ui.state.StatementAddingUiState

@HiltViewModel
class StatementAddingViewModel @Inject constructor(
    @param:ApplicationContext private val context: Context,
    savedStateHandle: SavedStateHandle,
    private val localDefiningThemesRepository: LocalDefiningThemesRepository,
    private val remoteStatementsRepository: RemoteStatementsRepository,
    private val localStatementsRepository: LocalStatementsRepository
) : ViewModel() {
    private val userId: Int = checkNotNull(savedStateHandle[StatementAddingDestination.userId])

    private val categoryId: Int =
        checkNotNull(savedStateHandle[StatementAddingDestination.categoryId])

    private val _uiState = MutableStateFlow(
        StatementAddingUiState(formData = StatementFormData(creatorUserId = userId))
    )
    val uiState = _uiState.asStateFlow()

    init {
        initDefiningThemes()
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

    fun updateUiState(statementFormData: StatementFormData) {
        _uiState.update {
            it.copy(formData = statementFormData)
        }
    }

    fun statementAdding() {
        viewModelScope.launch {
            _uiState.update { it.copy(actionRequestStatus = RequestStatus.LOADING) }

            val statementFormDetails = uiState.value.formData

            var (statement, status) = safeApiCall(context) {
                remoteStatementsRepository.addStatement(statementFormDetails.toStatementDetails())
            }

            if (statement != null) {
                localStatementsRepository.insertStatement(statement)
            }

            if (status is RequestStatus.ERROR) { // FixMe: remove after adding server hosting
                if (!localStatementsRepository.getStatements().first().map { it.id }
                        .contains(FakeData.newStatement.id)) {
                    localStatementsRepository.insertStatement(FakeData.newStatement)
                }

                status = RequestStatus.SUCCESS
            }

            _uiState.update { it.copy(actionRequestStatus = status) }
        }
    }
}