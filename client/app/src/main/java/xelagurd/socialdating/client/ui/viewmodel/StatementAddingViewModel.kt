package xelagurd.socialdating.client.ui.viewmodel

import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.first
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
import xelagurd.socialdating.client.data.model.DefaultDataProperties.ID_MIN
import xelagurd.socialdating.client.data.model.Statement
import xelagurd.socialdating.client.data.model.StatementDefiningTheme
import xelagurd.socialdating.client.data.remote.ApiUtils.safeApiCall
import xelagurd.socialdating.client.data.remote.repository.RemoteStatementsRepository
import xelagurd.socialdating.client.ui.form.StatementFormData
import xelagurd.socialdating.client.ui.navigation.StatementAddingDestination
import xelagurd.socialdating.client.ui.state.RequestStatus
import xelagurd.socialdating.client.ui.state.StatementAddingUiState

@HiltViewModel
class StatementAddingViewModel @Inject constructor(
    @param:ApplicationContext private val context: Context,
    savedStateHandle: SavedStateHandle,
    private val preferencesRepository: PreferencesRepository,
    private val localDefiningThemesRepository: LocalDefiningThemesRepository,
    private val remoteStatementsRepository: RemoteStatementsRepository,
    private val localStatementsRepository: LocalStatementsRepository
) : ViewModel() {

    private val userId: Int = checkNotNull(savedStateHandle[StatementAddingDestination.userId])
    private val categoryId: Int = checkNotNull(savedStateHandle[StatementAddingDestination.categoryId])
    private val isOfflineMode = runBlocking { preferencesRepository.isOfflineMode.first() }

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

            localDefiningThemesRepository.getDefiningThemes(categoryId)
                .distinctUntilChanged()
                .collect { definingThemes ->
                    _uiState.update {
                        it.copy(
                            entities = definingThemes,
                            dataRequestStatus = RequestStatus.SUCCESS
                        )
                    }
                }
        }
    }

    // the added statement is placed after all the statements which are already loaded by the statements screen
    private suspend fun nextOrderNumber() =
        localStatementsRepository.getMaxOrderNumber(categoryId)?.plus(1) ?: ID_MIN

    fun updateUiState(statementFormData: StatementFormData) =
        _uiState.update {
            it.copy(formData = statementFormData)
        }

    fun addStatement() {
        viewModelScope.launch {
            if (!isOfflineMode) { // FixMe: remove after adding server hosting
                _uiState.update { it.copy(actionRequestStatus = RequestStatus.LOADING) }

                val statementFormData = uiState.value.formData

                val (statement, status) = safeApiCall(context) {
                    remoteStatementsRepository.addStatement(statementFormData.toStatementDetails())
                }

                if (statement != null) {
                    localStatementsRepository.insertStatements(
                        listOf(statement.toStatement(nextOrderNumber()))
                    )
                    localStatementsRepository.insertStatementDefiningThemes(
                        statement.toStatementDefiningThemes()
                    )
                }

                _uiState.update { it.copy(actionRequestStatus = status) }
            } else {
                _uiState.update { it.copy(actionRequestStatus = RequestStatus.LOADING) }
                val statementFormData = uiState.value.formData
                val newId = (localStatementsRepository.getStatements().first().maxOfOrNull { it.id } ?: 0) + 1
                localStatementsRepository.insertStatements(
                    listOf(
                        Statement(
                            id = newId,
                            text = statementFormData.text,
                            creatorUserId = statementFormData.creatorUserId!!,
                            orderNumber = nextOrderNumber()
                        )
                    )
                )
                localStatementsRepository.insertStatementDefiningThemes(
                    statementFormData.definingThemes.keys.map {
                        StatementDefiningTheme(statementId = newId, definingThemeId = it)
                    }
                )
                _uiState.update { it.copy(actionRequestStatus = RequestStatus.SUCCESS) }
            }
        }
    }
}