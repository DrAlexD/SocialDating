package xelagurd.socialdating.client.ui.viewmodel

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
import xelagurd.socialdating.client.data.fake.FakeDataSource
import xelagurd.socialdating.client.data.local.repository.LocalCategoriesRepository
import xelagurd.socialdating.client.data.local.repository.LocalDefiningThemesRepository
import xelagurd.socialdating.client.data.local.repository.LocalUserCategoriesRepository
import xelagurd.socialdating.client.data.local.repository.LocalUserDefiningThemesRepository
import xelagurd.socialdating.client.data.remote.repository.RemoteCategoriesRepository
import xelagurd.socialdating.client.data.remote.repository.RemoteDefiningThemesRepository
import xelagurd.socialdating.client.data.remote.repository.RemoteUserCategoriesRepository
import xelagurd.socialdating.client.data.remote.repository.RemoteUserDefiningThemesRepository
import xelagurd.socialdating.client.data.safeApiCall
import xelagurd.socialdating.client.ui.navigation.ProfileStatisticsDestination
import xelagurd.socialdating.client.ui.state.ProfileStatisticsUiState
import xelagurd.socialdating.client.ui.state.RequestStatus

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class ProfileStatisticsViewModel @Inject constructor(
    @param:ApplicationContext private val context: Context,
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
            var globalStatus: RequestStatus = RequestStatus.LOADING

            dataRequestStatusFlow.update { globalStatus }

            val (remoteCategories, statusCategories) = safeApiCall(context) {
                remoteCategoriesRepository.getCategories()
            }

            if (remoteCategories != null) {
                localCategoriesRepository.insertCategories(remoteCategories)

                val remoteCategoriesIds = remoteCategories.map { it.id }
                val (remoteDefiningThemes, statusDefiningThemes) = safeApiCall(context) {
                    remoteDefiningThemesRepository.getDefiningThemes(remoteCategoriesIds)
                }

                if (remoteDefiningThemes != null) {
                    localDefiningThemesRepository.insertDefiningThemes(remoteDefiningThemes)

                    val (remoteUserCategories, statusUserCategories) = safeApiCall(context) {
                        remoteUserCategoriesRepository.getUserCategories(userId)
                    }

                    if (remoteUserCategories != null) {
                        localUserCategoriesRepository.insertUserCategories(remoteUserCategories)

                        val remoteUserCategoriesIds = remoteUserCategories.map { it.id }
                        val (remoteUserDefiningThemes, statusUserDefiningThemes) = safeApiCall(
                            context
                        ) {
                            remoteUserDefiningThemesRepository.getUserDefiningThemes(
                                remoteUserCategoriesIds
                            )
                        }

                        if (remoteUserDefiningThemes != null) {
                            localUserDefiningThemesRepository.insertUserDefiningThemes(
                                remoteUserDefiningThemes
                            )
                        }

                        globalStatus = statusUserDefiningThemes
                    } else {
                        globalStatus = statusUserCategories
                    }
                } else {
                    globalStatus = statusDefiningThemes
                }
            } else {
                globalStatus = statusCategories
            }

            if (globalStatus is RequestStatus.ERROR) { // FixMe: remove after implementing server
                if (localCategoriesRepository.getCategories().first().isEmpty()) {
                    localCategoriesRepository.insertCategories(FakeDataSource.categories)
                }
                if (localDefiningThemesRepository.getDefiningThemes().first().isEmpty()) {
                    localDefiningThemesRepository.insertDefiningThemes(FakeDataSource.definingThemes)
                }
                if (localUserCategoriesRepository.getUserCategories().first().isEmpty()) {
                    localUserCategoriesRepository.insertUserCategories(FakeDataSource.userCategories)
                }
                if (localUserDefiningThemesRepository.getUserDefiningThemes().first()
                        .isEmpty()
                ) {
                    localUserDefiningThemesRepository.insertUserDefiningThemes(
                        FakeDataSource.userDefiningThemes
                    )
                }
            }

            dataRequestStatusFlow.update { globalStatus }
        }
    }

    companion object {
        private const val TIMEOUT_MILLIS = 5_000L
    }
}