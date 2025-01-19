package xelagurd.socialdating.ui.viewmodel

import java.io.IOException
import java.lang.Exception
import javax.inject.Inject
import kotlin.collections.groupBy
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
import xelagurd.socialdating.R
import xelagurd.socialdating.data.fake.FakeDataSource
import xelagurd.socialdating.data.local.repository.LocalCategoriesRepository
import xelagurd.socialdating.data.local.repository.LocalDefiningThemesRepository
import xelagurd.socialdating.data.local.repository.LocalUserCategoriesRepository
import xelagurd.socialdating.data.local.repository.LocalUserDefiningThemesRepository
import xelagurd.socialdating.data.remote.repository.RemoteCategoriesRepository
import xelagurd.socialdating.data.remote.repository.RemoteDefiningThemesRepository
import xelagurd.socialdating.data.remote.repository.RemoteUserCategoriesRepository
import xelagurd.socialdating.data.remote.repository.RemoteUserDefiningThemesRepository
import xelagurd.socialdating.ui.navigation.ProfileStatisticsDestination
import xelagurd.socialdating.ui.state.ProfileStatisticsUiState
import xelagurd.socialdating.ui.state.RequestStatus

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class ProfileStatisticsViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    savedStateHandle: SavedStateHandle,
    private val remoteUserCategoriesRepository: RemoteUserCategoriesRepository,
    private val localUserCategoriesRepository: LocalUserCategoriesRepository,
    private val remoteUserDefiningThemesRepository: RemoteUserDefiningThemesRepository,
    private val localUserDefiningThemesRepository: LocalUserDefiningThemesRepository,
    private val remoteCategoriesRepository: RemoteCategoriesRepository,
    private val localCategoriesRepository: LocalCategoriesRepository,
    private val remoteDefiningThemesRepository: RemoteDefiningThemesRepository,
    private val localDefiningThemesRepository: LocalDefiningThemesRepository
) : ViewModel() {
    private val userId: Int = checkNotNull(savedStateHandle[ProfileStatisticsDestination.userId])

    private val dataRequestStatusFlow = MutableStateFlow<RequestStatus>(RequestStatus.UNDEFINED)
    private val userCategoriesFlow = localUserCategoriesRepository.getUserCategories(userId)
    private val userDefiningThemesFlow = userCategoriesFlow
        .distinctUntilChanged()
        .flatMapLatest { userCategories ->
            val userCategoryIds = userCategories.map { it.id }.distinct()
            localUserDefiningThemesRepository.getUserDefiningThemes(userCategoryIds)
                .distinctUntilChanged()
        }

    val uiState = combine(userCategoriesFlow, userDefiningThemesFlow, dataRequestStatusFlow)
    { userCategories, userDefiningThemes, dataRequestStatus ->
        ProfileStatisticsUiState(
            entities = userCategories,
            entityIdToData = userDefiningThemes.groupBy { it.userCategoryId },
            dataRequestStatus = dataRequestStatus
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(TIMEOUT_MILLIS),
        initialValue = ProfileStatisticsUiState()
    )

    init {
        getProfileStatistics()
    }

    fun getProfileStatistics() {
        viewModelScope.launch {
            try {
                dataRequestStatusFlow.update { RequestStatus.LOADING }

                val remoteCategories = remoteCategoriesRepository.getCategories()
                localCategoriesRepository.insertCategories(remoteCategories)

                val remoteCategoriesIds = remoteCategories.map { it.id }
                val remoteDefiningThemes =
                    remoteDefiningThemesRepository.getDefiningThemes(remoteCategoriesIds)
                localDefiningThemesRepository.insertDefiningThemes(remoteDefiningThemes)

                val remoteUserCategories = remoteUserCategoriesRepository
                    .getUserCategories(userId)
                localUserCategoriesRepository.insertUserCategories(remoteUserCategories)

                val remoteUserCategoriesIds = remoteUserCategories.map { it.id }
                val remoteUserDefiningThemes = remoteUserDefiningThemesRepository
                    .getUserDefiningThemes(remoteUserCategoriesIds)

                if (remoteUserDefiningThemes.isNotEmpty()) {
                    localUserDefiningThemesRepository.insertUserDefiningThemes(
                        remoteUserDefiningThemes
                    )

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
                        if (localCategoriesRepository.getCategories().first().isEmpty()) {
                            localCategoriesRepository.insertCategories(FakeDataSource.categories) // FixMe: remove after implementing server
                        }
                        if (localDefiningThemesRepository.getDefiningThemes().first().isEmpty()) {
                            localDefiningThemesRepository.insertDefiningThemes(FakeDataSource.definingThemes) // FixMe: remove after implementing server
                        }
                        if (localUserCategoriesRepository.getUserCategories().first().isEmpty()) {
                            localUserCategoriesRepository.insertUserCategories(FakeDataSource.userCategories) // FixMe: remove after implementing server
                        }
                        if (localUserDefiningThemesRepository.getUserDefiningThemes().first()
                                .isEmpty()
                        ) {
                            localUserDefiningThemesRepository.insertUserDefiningThemes(
                                FakeDataSource.userDefiningThemes
                            ) // FixMe: remove after implementing server
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

    companion object {
        private const val TIMEOUT_MILLIS = 5_000L
    }
}