package xelagurd.socialdating.ui.viewmodel

import java.io.IOException
import java.lang.Exception
import javax.inject.Inject
import kotlin.collections.map
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
import xelagurd.socialdating.PreferencesRepository
import xelagurd.socialdating.R
import xelagurd.socialdating.data.fake.FakeDataSource
import xelagurd.socialdating.data.local.repository.LocalDefiningThemesRepository
import xelagurd.socialdating.data.local.repository.LocalStatementsRepository
import xelagurd.socialdating.data.model.additional.StatementReaction
import xelagurd.socialdating.data.model.enums.StatementReactionType
import xelagurd.socialdating.data.remote.repository.RemoteDefiningThemesRepository
import xelagurd.socialdating.data.remote.repository.RemoteStatementsRepository
import xelagurd.socialdating.ui.navigation.StatementsDestination
import xelagurd.socialdating.ui.state.RequestStatus
import xelagurd.socialdating.ui.state.StatementsUiState

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

    fun initCurrentUserId() {
        viewModelScope.launch {
            currentUserId = preferencesRepository.currentUserId.first()
        }
    }

    fun getStatements() {
        viewModelScope.launch {
            try {
                dataRequestStatusFlow.update { RequestStatus.LOADING }

                val remoteDefiningThemes = remoteDefiningThemesRepository
                    .getDefiningThemes(categoryId)
                localDefiningThemesRepository.insertDefiningThemes(remoteDefiningThemes)

                val remoteDefiningThemeIds = remoteDefiningThemes.map { it.id }
                val remoteStatements = remoteStatementsRepository
                    .getStatements(remoteDefiningThemeIds)

                if (remoteStatements.isNotEmpty()) {
                    localStatementsRepository.insertStatements(remoteStatements)

                    dataRequestStatusFlow.update { RequestStatus.SUCCESS }
                } else {
                    dataRequestStatusFlow.update {
                        RequestStatus.FAILURE(
                            failureText = context.getString(R.string.no_data)
                        )
                    }
                }
            } catch (e: Exception) {
                when (e) {
                    is IOException, is HttpException -> {
                        if (localDefiningThemesRepository.getDefiningThemes().first().isEmpty()) {
                            localDefiningThemesRepository.insertDefiningThemes(FakeDataSource.definingThemes) // FixMe: remove after implementing server
                        }
                        if (localStatementsRepository.getStatements().first().isEmpty()) {
                            localStatementsRepository.insertStatements(FakeDataSource.statements) // FixMe: remove after implementing server
                        }

                        dataRequestStatusFlow.update {
                            RequestStatus.ERROR(
                                errorText = context.getString(R.string.no_internet_connection)
                            )
                        }
                    }

                    else -> throw e
                }
            }
        }
    }

    fun onStatementReactionClick(statementId: Int, reactionType: StatementReactionType) {
        if (currentUserId != null) {
            viewModelScope.launch {
                try {
                    remoteStatementsRepository.postStatementReaction(
                        StatementReaction(currentUserId!!, statementId, reactionType)
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