package xelagurd.socialdating.ui.viewmodel

import java.io.IOException
import java.lang.Exception
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
import retrofit2.HttpException
import xelagurd.socialdating.PreferencesRepository
import xelagurd.socialdating.R
import xelagurd.socialdating.data.fake.FakeDataSource
import xelagurd.socialdating.data.local.repository.LocalDefiningThemesRepository
import xelagurd.socialdating.data.local.repository.LocalStatementsRepository
import xelagurd.socialdating.data.model.details.StatementDetails
import xelagurd.socialdating.data.remote.repository.RemoteStatementsRepository
import xelagurd.socialdating.ui.navigation.StatementAddingDestination
import xelagurd.socialdating.ui.state.RequestStatus
import xelagurd.socialdating.ui.state.StatementAddingUiState

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
                        userId = preferencesRepository.currentUserId.first()
                    )
                )
            }
        }
    }

    private fun initDefiningThemes() {
        viewModelScope.launch {
            _uiState.update { it.copy(dataRequestStatus = RequestStatus.LOADING) }

            val definingThemes = localDefiningThemesRepository.getDefiningThemes(categoryId).first()
            _uiState.update { it.copy(entities = definingThemes) }

            if (definingThemes.isNotEmpty()) {
                _uiState.update { it.copy(dataRequestStatus = RequestStatus.SUCCESS) }
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
            try {
                _uiState.update { it.copy(actionRequestStatus = RequestStatus.LOADING) }

                val statementDetails = uiState.value.formDetails
                val statement = remoteStatementsRepository.addStatement(statementDetails)

                if (statement != null) {
                    localStatementsRepository.insertStatement(statement)

                    _uiState.update { it.copy(actionRequestStatus = RequestStatus.SUCCESS) }
                } else {
                    _uiState.update {
                        it.copy(
                            actionRequestStatus = RequestStatus.FAILURE(
                                failureText = context.getString(R.string.failed_add_statement)
                            )
                        )
                    }
                }
            } catch (e: Exception) {
                when (e) {
                    is IOException, is HttpException -> {
                        if (!localStatementsRepository.getStatements().first().map { it.id }
                                .contains(FakeDataSource.newStatement.id)) {
                            localStatementsRepository.insertStatement(FakeDataSource.newStatement) // TODO: remove after implementing server
                        }

                        _uiState.update { it.copy(actionRequestStatus = RequestStatus.SUCCESS) } // TODO: Change to ERROR after implementing
                    }

                    else -> throw e
                }
            }
        }
    }
}