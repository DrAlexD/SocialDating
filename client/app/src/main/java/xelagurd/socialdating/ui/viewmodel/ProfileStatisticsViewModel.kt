package xelagurd.socialdating.ui.viewmodel

import java.io.IOException
import javax.inject.Inject
import kotlin.collections.map
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import xelagurd.socialdating.data.fake.FakeDataSource
import xelagurd.socialdating.data.local.repository.LocalUserCategoriesRepository
import xelagurd.socialdating.data.local.repository.LocalUserDefiningThemesRepository
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
    private val localUserDefiningThemesRepository: LocalUserDefiningThemesRepository
) : ViewModel() {
    var internetStatus by mutableStateOf(InternetStatus.LOADING)
        private set

    private val userId: Int = checkNotNull(savedStateHandle[ProfileStatisticsDestination.userId])

    val uiState: StateFlow<ProfileStatisticsUiState> = localUserCategoriesRepository
        .getUserCategories(userId)
        .flatMapLatest { userCategories ->
            val userCategoryIds = userCategories.map { it.id }
            localUserDefiningThemesRepository.getUserDefiningThemes(userCategoryIds)
                .map { userDefiningThemes ->
                    val definingThemesByCategory = userDefiningThemes.groupBy { it.userCategoryId }
                    ProfileStatisticsUiState(
                        userCategories = userCategories,
                        userCategoryToDefiningThemes = definingThemesByCategory
                    )
                }
        }
        .stateIn(
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
                internetStatus = InternetStatus.LOADING

                delay(3000L) // FixMe: remove after implementing server

                val remoteUserCategories = remoteUserCategoriesRepository
                    .getUserCategories(userId)
                val remoteUserCategoriesIds = remoteUserCategories.map { it.id }

                val remoteUserDefiningThemes = remoteUserDefiningThemesRepository
                    .getUserDefiningThemes(remoteUserCategoriesIds)

                localUserCategoriesRepository.insertUserCategories(remoteUserCategories)
                localUserDefiningThemesRepository.insertUserDefiningThemes(
                    remoteUserDefiningThemes
                )

                internetStatus = InternetStatus.ONLINE
            } catch (_: IOException) {
                localUserCategoriesRepository.insertUserCategories(FakeDataSource.userCategories) // FixMe: remove after implementing server
                localUserDefiningThemesRepository.insertUserDefiningThemes(FakeDataSource.userDefiningThemes) // FixMe: remove after implementing server
                internetStatus = InternetStatus.OFFLINE
            }
        }
    }

    companion object {
        private const val TIMEOUT_MILLIS = 5_000L
    }
}