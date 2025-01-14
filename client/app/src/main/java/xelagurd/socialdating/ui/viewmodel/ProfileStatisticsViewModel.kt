package xelagurd.socialdating.ui.viewmodel

import java.io.IOException
import javax.inject.Inject
import kotlin.collections.groupBy
import kotlin.collections.map
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import xelagurd.socialdating.data.fake.FAKE_SERVER_LATENCY
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
import xelagurd.socialdating.ui.state.InternetStatus
import xelagurd.socialdating.ui.state.ProfileStatisticsUiState

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class ProfileStatisticsViewModel @Inject constructor(
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

    private val internetStatusFlow = MutableStateFlow(InternetStatus.LOADING)
    private val userCategoriesFlow = localUserCategoriesRepository.getUserCategories(userId)
    private val userDefiningThemesFlow = userCategoriesFlow
        .distinctUntilChanged()
        .flatMapLatest { userCategories ->
            val userCategoryIds = userCategories.map { it.id }.distinct()
            localUserDefiningThemesRepository.getUserDefiningThemes(userCategoryIds)
                .distinctUntilChanged()
        }

    val uiState = combine(userCategoriesFlow, userDefiningThemesFlow, internetStatusFlow)
    { userCategories, userDefiningThemes, internetStatus ->
        ProfileStatisticsUiState(
            entities = userCategories,
            entityIdToData = userDefiningThemes.groupBy { it.userCategoryId },
            internetStatus = internetStatus
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
                internetStatusFlow.update { InternetStatus.LOADING }

                delay(FAKE_SERVER_LATENCY) // FixMe: remove after implementing server

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
                localUserDefiningThemesRepository.insertUserDefiningThemes(
                    remoteUserDefiningThemes
                )

                internetStatusFlow.update { InternetStatus.ONLINE }
            } catch (_: IOException) {
                localCategoriesRepository.insertCategories(FakeDataSource.categories) // FixMe: remove after implementing server
                localDefiningThemesRepository.insertDefiningThemes(FakeDataSource.definingThemes) // FixMe: remove after implementing server
                localUserCategoriesRepository.insertUserCategories(FakeDataSource.userCategories) // FixMe: remove after implementing server
                localUserDefiningThemesRepository.insertUserDefiningThemes(FakeDataSource.userDefiningThemes) // FixMe: remove after implementing server

                internetStatusFlow.update { InternetStatus.OFFLINE }
            }
        }
    }

    companion object {
        private const val TIMEOUT_MILLIS = 5_000L
    }
}