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
import kotlinx.coroutines.runBlocking
import android.content.Context
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import xelagurd.socialdating.client.data.PreferencesRepository
import xelagurd.socialdating.client.data.fake.FakeData
import xelagurd.socialdating.client.data.local.repository.CommonLocalRepository
import xelagurd.socialdating.client.data.local.repository.LocalCategoriesRepository
import xelagurd.socialdating.client.data.local.repository.LocalDefiningThemesRepository
import xelagurd.socialdating.client.data.local.repository.LocalUserCategoriesRepository
import xelagurd.socialdating.client.data.local.repository.LocalUserDefiningThemesRepository
import xelagurd.socialdating.client.data.model.DataUtils.TIMEOUT_MILLIS
import xelagurd.socialdating.client.data.model.DataUtils.toUserCategoriesWithData
import xelagurd.socialdating.client.data.model.DataUtils.toUserDefiningThemesWithData
import xelagurd.socialdating.client.data.model.additional.DetailedSimilarUser
import xelagurd.socialdating.client.data.model.ui.UserCategoryWithData
import xelagurd.socialdating.client.data.model.ui.UserDefiningThemeWithData
import xelagurd.socialdating.client.data.remote.ApiUtils.safeApiCall
import xelagurd.socialdating.client.data.remote.repository.RemoteCategoriesRepository
import xelagurd.socialdating.client.data.remote.repository.RemoteDefiningThemesRepository
import xelagurd.socialdating.client.data.remote.repository.RemoteUserCategoriesRepository
import xelagurd.socialdating.client.data.remote.repository.RemoteUserDefiningThemesRepository
import xelagurd.socialdating.client.ui.navigation.ProfileStatisticsDestination
import xelagurd.socialdating.client.ui.state.ProfileStatisticsUiState
import xelagurd.socialdating.client.ui.state.RequestStatus

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class ProfileStatisticsViewModel @Inject constructor(
    @param:ApplicationContext private val context: Context,
    savedStateHandle: SavedStateHandle,
    private val preferencesRepository: PreferencesRepository,
    private val remoteUserCategoriesRepository: RemoteUserCategoriesRepository,
    localUserCategoriesRepository: LocalUserCategoriesRepository,
    private val remoteUserDefiningThemesRepository: RemoteUserDefiningThemesRepository,
    localUserDefiningThemesRepository: LocalUserDefiningThemesRepository,
    private val remoteCategoriesRepository: RemoteCategoriesRepository,
    private val localCategoriesRepository: LocalCategoriesRepository,
    private val remoteDefiningThemesRepository: RemoteDefiningThemesRepository,
    private val localDefiningThemesRepository: LocalDefiningThemesRepository,
    private val commonLocalRepository: CommonLocalRepository
) : ViewModel() {

    private val userId: Int = checkNotNull(savedStateHandle[ProfileStatisticsDestination.userId])
    private val anotherUserId: Int = checkNotNull(savedStateHandle[ProfileStatisticsDestination.anotherUserId])
    private val isOfflineMode = runBlocking { preferencesRepository.isOfflineMode.first() }

    private val dataRequestStatusFlow = MutableStateFlow<RequestStatus>(RequestStatus.UNDEFINED)
    private val userCategoriesStateFlow = MutableStateFlow<List<UserCategoryWithData>>(listOf())
    private val userCategoriesFlow = when (anotherUserId) {
        userId -> localUserCategoriesRepository.getUserCategories(anotherUserId).distinctUntilChanged()
        else -> userCategoriesStateFlow
    }
    private val userDefiningThemesStateFlow = MutableStateFlow<List<UserDefiningThemeWithData>>(listOf())
    private val userDefiningThemesFlow = when (anotherUserId) {
        userId -> localUserDefiningThemesRepository.getUserDefiningThemes(anotherUserId).distinctUntilChanged()
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
        if (!isOfflineMode) { // FixMe: remove after adding server hosting
            getProfileStatistics()
        } else if (anotherUserId != userId) {
            dataRequestStatusFlow.update { RequestStatus.LOADING }
            detailedSimilarUserFlow.update { FakeData.detailedSimilarUser }
            userDefiningThemesStateFlow.update {
                FakeData.userDefiningThemes.toUserDefiningThemesWithData(FakeData.definingThemes)
            }
            userCategoriesStateFlow.update { FakeData.userCategories.toUserCategoriesWithData(FakeData.categories) }
            dataRequestStatusFlow.update { RequestStatus.SUCCESS }
        } else {
            dataRequestStatusFlow.update { RequestStatus.SUCCESS }
        }
    }

    fun getProfileStatistics() {
        viewModelScope.launch {
            var globalStatus: RequestStatus = RequestStatus.LOADING

            dataRequestStatusFlow.update { globalStatus }

            val (remoteDetailedSimilarUser, statusDetailedSimilarUser) = when (anotherUserId) {
                userId -> null to RequestStatus.UNDEFINED
                else -> safeApiCall(context) {
                    remoteUserCategoriesRepository.getDetailedSimilarUser(userId, anotherUserId)
                }
            }

            if (anotherUserId == userId || remoteDetailedSimilarUser != null) {
                val (remoteUserDefiningThemes, statusUserDefiningThemes) = safeApiCall(context) {
                    remoteUserDefiningThemesRepository.getUserDefiningThemes(anotherUserId)
                }

                if (remoteUserDefiningThemes != null) {
                    val (remoteUserCategories, statusUserCategories) = safeApiCall(context) {
                        remoteUserCategoriesRepository.getUserCategories(anotherUserId)
                    }

                    if (remoteUserCategories != null) {
                        val localDefiningThemes = localDefiningThemesRepository.getDefiningThemes().first()
                        val localDefiningThemeIds = localDefiningThemes.map { it.id }
                        val neededDefiningThemeIds = remoteUserDefiningThemes
                            .filter { it.definingThemeId !in localDefiningThemeIds }
                            .map { it.definingThemeId }

                        val (remoteDefiningThemes, statusDefiningThemes) = when {
                            neededDefiningThemeIds.isEmpty() -> null to RequestStatus.UNDEFINED
                            else -> safeApiCall(context) {
                                remoteDefiningThemesRepository.getDefiningThemes(neededDefiningThemeIds)
                            }
                        }

                        if (neededDefiningThemeIds.isEmpty() || remoteDefiningThemes != null) {
                            val localCategories = localCategoriesRepository.getCategories().first()
                            val localCategoryIds = localCategories.map { it.id }
                            val neededCategoryIds = remoteUserCategories
                                .filter { it.categoryId !in localCategoryIds }
                                .map { it.categoryId }

                            val (remoteCategories, statusCategories) = when {
                                neededCategoryIds.isEmpty() -> null to RequestStatus.UNDEFINED
                                else -> safeApiCall(context) {
                                    remoteCategoriesRepository.getCategories(neededCategoryIds)
                                }
                            }

                            if (neededCategoryIds.isEmpty() || remoteCategories != null) {
                                if (userId == anotherUserId) {
                                    commonLocalRepository.updateProfileStatisticsScreenData(
                                        remoteCategories,
                                        remoteDefiningThemes,
                                        remoteUserCategories,
                                        remoteUserDefiningThemes
                                    )
                                } else {
                                    commonLocalRepository.updateProfileStatisticsScreenData(
                                        remoteCategories,
                                        remoteDefiningThemes
                                    )

                                    detailedSimilarUserFlow.update { remoteDetailedSimilarUser }

                                    val allDefiningThemes = localDefiningThemes.toMutableList()
                                    allDefiningThemes.addAll(remoteDefiningThemes ?: emptyList())
                                    userDefiningThemesStateFlow.update {
                                        remoteUserDefiningThemes.toUserDefiningThemesWithData(allDefiningThemes)
                                    }

                                    val allCategories = localCategories.toMutableList()
                                    allCategories.addAll(remoteCategories ?: emptyList())
                                    userCategoriesStateFlow.update {
                                        remoteUserCategories.toUserCategoriesWithData(allCategories)
                                    }
                                }

                                globalStatus = when {
                                    neededCategoryIds.isEmpty() -> RequestStatus.SUCCESS
                                    else -> statusCategories
                                }
                            } else {
                                globalStatus = statusCategories
                            }
                        } else {
                            globalStatus = statusDefiningThemes
                        }
                    } else {
                        globalStatus = statusUserCategories
                    }
                } else {
                    globalStatus = statusUserDefiningThemes
                }
            } else {
                globalStatus = statusDetailedSimilarUser
            }

            dataRequestStatusFlow.update { globalStatus }
        }
    }
}