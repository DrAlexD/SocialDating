package xelagurd.socialdating.client.ui.viewmodel

import java.io.IOException
import javax.inject.Inject
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import android.content.Context
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import retrofit2.HttpException
import xelagurd.socialdating.client.data.PreferencesRepository
import xelagurd.socialdating.client.data.fake.FakeDataSource
import xelagurd.socialdating.client.data.local.repository.LocalDefiningThemesRepository
import xelagurd.socialdating.client.data.local.repository.LocalStatementsRepository
import xelagurd.socialdating.client.data.model.Statement
import xelagurd.socialdating.client.data.model.additional.StatementReactionDetails
import xelagurd.socialdating.client.data.model.enums.StatementReactionType
import xelagurd.socialdating.client.data.remote.repository.RemoteDefiningThemesRepository
import xelagurd.socialdating.client.data.remote.repository.RemoteStatementsRepository
import xelagurd.socialdating.client.data.safeApiCall
import xelagurd.socialdating.client.ui.navigation.StatementsDestination
import xelagurd.socialdating.client.ui.state.RequestStatus
import xelagurd.socialdating.client.ui.state.StatementsUiState

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class StatementsViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    savedStateHandle: SavedStateHandle,
    private val remoteStatementsRepository: RemoteStatementsRepository,
    private val localStatementsRepository: LocalStatementsRepository,
    private val remoteDefiningThemesRepository: RemoteDefiningThemesRepository,
    private val localDefiningThemesRepository: LocalDefiningThemesRepository,
    private val preferencesRepository: PreferencesRepository
) : ViewModel() {
    private var currentUserId: Int? = null

    private val categoryId: Int = checkNotNull(savedStateHandle[StatementsDestination.categoryId])

    private val dataRequestStatusFlow = MutableStateFlow<RequestStatus>(RequestStatus.UNDEFINED)
    private val statementsFlow = localDefiningThemesRepository.getDefiningThemes(categoryId)
        .distinctUntilChanged()
        .flatMapLatest { definingThemes ->
            val definingThemeIds = definingThemes.map { it.id }.distinct()
            localStatementsRepository.getStatements(definingThemeIds)
                .distinctUntilChanged()
        }

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
        initCurrentUserId()
        getStatements()
    }

    private fun initCurrentUserId() {
        viewModelScope.launch {
            currentUserId = preferencesRepository.currentUserId.first()
        }
    }

    fun getStatements() {
        viewModelScope.launch {
            var globalStatus: RequestStatus = RequestStatus.LOADING

            dataRequestStatusFlow.update { globalStatus }

            val (remoteDefiningThemes, statusDefiningThemes) = safeApiCall(context) {
                remoteDefiningThemesRepository.getDefiningThemes(listOf(categoryId))
            }

            if (remoteDefiningThemes != null) {
                localDefiningThemesRepository.insertDefiningThemes(remoteDefiningThemes)

                val remoteDefiningThemeIds = remoteDefiningThemes.map { it.id }
                val (remoteStatements, statusStatements) = safeApiCall(context) {
                    remoteStatementsRepository.getStatements(remoteDefiningThemeIds)
                }

                if (remoteStatements != null) {
                    localStatementsRepository.insertStatements(remoteStatements)
                }

                globalStatus = statusStatements
            } else {
                globalStatus = statusDefiningThemes
            }

            if (globalStatus != RequestStatus.SUCCESS) { // FixMe: remove after implementing server
                if (localDefiningThemesRepository.getDefiningThemes().first().isEmpty()) {
                    localDefiningThemesRepository.insertDefiningThemes(FakeDataSource.definingThemes)
                }
                if (localStatementsRepository.getStatements().first().isEmpty()) {
                    localStatementsRepository.insertStatements(FakeDataSource.statements)
                }
            }

            dataRequestStatusFlow.update { globalStatus }
        }
    }

    fun onStatementReactionClick(statement: Statement, reactionType: StatementReactionType) {
        if (currentUserId != null) {
            viewModelScope.launch {
                try {
                    remoteStatementsRepository.addStatementReaction(
                        statement.id,
                        StatementReactionDetails(
                            userOrUserCategoryId = currentUserId!!,
                            categoryId = categoryId,
                            definingThemeId = statement.definingThemeId,
                            reactionType = reactionType,
                            isSupportDefiningTheme = statement.isSupportDefiningTheme
                        )
                    )
                } catch (e: Exception) {
                    when (e) {
                        is IOException, is HttpException -> {
                            // TODO: implement handler
                        }

                        else -> throw e
                    }
                }
            }
        }
    }

    companion object {
        private const val TIMEOUT_MILLIS = 5_000L
    }
}