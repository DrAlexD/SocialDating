package xelagurd.socialdating.client.ui.viewmodel

import javax.inject.Inject
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import android.content.Context
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import xelagurd.socialdating.client.data.fake.FakeData
import xelagurd.socialdating.client.data.local.repository.LocalCategoriesRepository
import xelagurd.socialdating.client.data.local.repository.LocalDefiningThemesRepository
import xelagurd.socialdating.client.data.local.repository.LocalUserCategoriesRepository
import xelagurd.socialdating.client.data.local.repository.LocalUserDefiningThemesRepository
import xelagurd.socialdating.client.data.model.additional.DetailedSimilarUser
import xelagurd.socialdating.client.data.model.toUserCategoriesWithData
import xelagurd.socialdating.client.data.model.toUserDefiningThemesWithData
import xelagurd.socialdating.client.data.model.ui.UserCategoryWithData
import xelagurd.socialdating.client.data.model.ui.UserDefiningThemeWithData
import xelagurd.socialdating.client.data.remote.repository.RemoteCategoriesRepository
import xelagurd.socialdating.client.data.remote.repository.RemoteDefiningThemesRepository
import xelagurd.socialdating.client.data.remote.repository.RemoteUserCategoriesRepository
import xelagurd.socialdating.client.data.remote.repository.RemoteUserDefiningThemesRepository
import xelagurd.socialdating.client.data.remote.safeApiCall
import xelagurd.socialdating.client.ui.navigation.ProfileStatisticsDestination
import xelagurd.socialdating.client.ui.navigation.TIMEOUT_MILLIS
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
    private val anotherUserId: Int = checkNotNull(savedStateHandle[ProfileStatisticsDestination.anotherUserId])

    private val dataRequestStatusFlow = MutableStateFlow<RequestStatus>(RequestStatus.UNDEFINED)
    private val userCategoriesStateFlow = MutableStateFlow<List<UserCategoryWithData>>(listOf())
    private val userCategoriesFlow = when {
        userId == anotherUserId -> localUserCategoriesRepository.getUserCategories(anotherUserId)
            .distinctUntilChanged()

        else -> userCategoriesStateFlow
    }
    private val userDefiningThemesStateFlow = MutableStateFlow<List<UserDefiningThemeWithData>>(listOf())
    private val userDefiningThemesFlow = when {
        userId == anotherUserId -> localUserDefiningThemesRepository.getUserDefiningThemes(anotherUserId)
            .distinctUntilChanged()

        else -> userDefiningThemesStateFlow
    }
    private val detailedSimilarUserFlow = MutableStateFlow<DetailedSimilarUser?>(null)

    val uiState = combine(userCategoriesFlow, userDefiningThemesFlow, detailedSimilarUserFlow, dataRequestStatusFlow)
    { userCategories, userDefiningThemes, detailedSimilarUser, dataRequestStatus ->
        ProfileStatisticsUiState(
            userId = userId,
            anotherUserId = anotherUserId,
            entities = userCategories,
            entityIdToData = userDefiningThemes.groupBy { it.categoryId },
            entitiesMask = detailedSimilarUser,
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

                val (remoteDefiningThemes, statusDefiningThemes) = safeApiCall(context) {
                    remoteDefiningThemesRepository.getDefiningThemes()
                }

                if (remoteDefiningThemes != null) {
                    localDefiningThemesRepository.insertDefiningThemes(remoteDefiningThemes)

                    val (remoteUserCategories, statusUserCategories) = safeApiCall(context) {
                        remoteUserCategoriesRepository.getUserCategories(anotherUserId)
                    }

                    if (remoteUserCategories != null) {
                        if (userId == anotherUserId) {
                            localUserCategoriesRepository.insertUserCategories(remoteUserCategories)
                        } else {
                            userCategoriesStateFlow.update {
                                remoteUserCategories.toUserCategoriesWithData(remoteCategories)
                            }
                        }

                        val (remoteUserDefiningThemes, statusUserDefiningThemes) = safeApiCall(context) {
                            remoteUserDefiningThemesRepository.getUserDefiningThemes(anotherUserId)
                        }

                        if (remoteUserDefiningThemes != null) {
                            if (userId == anotherUserId) {
                                localUserDefiningThemesRepository.insertUserDefiningThemes(remoteUserDefiningThemes)

                                globalStatus = statusUserDefiningThemes
                            } else {
                                userDefiningThemesStateFlow.update {
                                    remoteUserDefiningThemes.toUserDefiningThemesWithData(remoteDefiningThemes)
                                }

                                val (remoteDetailedSimilarUser, statusDetailedSimilarUser) = safeApiCall(context) {
                                    remoteUserCategoriesRepository.getDetailedSimilarUser(userId, anotherUserId)
                                }

                                if (remoteDetailedSimilarUser != null) {
                                    detailedSimilarUserFlow.update { remoteDetailedSimilarUser }
                                }

                                globalStatus = statusDetailedSimilarUser
                            }
                        } else {
                            globalStatus = statusUserDefiningThemes
                        }
                    } else {
                        globalStatus = statusUserCategories
                    }
                } else {
                    globalStatus = statusDefiningThemes
                }
            } else {
                globalStatus = statusCategories
            }

            if (globalStatus is RequestStatus.ERROR) { // FixMe: remove after adding server hosting
                if (localCategoriesRepository.getCategories().first().isEmpty()) {
                    localCategoriesRepository.insertCategories(FakeData.categories)
                }
                if (localDefiningThemesRepository.getDefiningThemes().first().isEmpty()) {
                    localDefiningThemesRepository.insertDefiningThemes(FakeData.definingThemes)
                }
                if (userId == anotherUserId) {
                    if (localUserCategoriesRepository.getUserCategories().first().isEmpty()) {
                        localUserCategoriesRepository.insertUserCategories(FakeData.userCategories)
                    }
                    if (localUserDefiningThemesRepository.getUserDefiningThemes().first().isEmpty()) {
                        localUserDefiningThemesRepository.insertUserDefiningThemes(FakeData.userDefiningThemes)
                    }
                } else {
                    userCategoriesStateFlow.update {
                        FakeData.userCategories.toUserCategoriesWithData(FakeData.categories)
                    }
                    userDefiningThemesStateFlow.update {
                        FakeData.userDefiningThemes.toUserDefiningThemesWithData(FakeData.definingThemes)
                    }
                    detailedSimilarUserFlow.update { FakeData.detailedSimilarUser }
                }
            }

            dataRequestStatusFlow.update { globalStatus }
        }
    }
}