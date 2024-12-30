package xelagurd.socialdating.tests

import java.io.IOException
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import androidx.lifecycle.SavedStateHandle
import io.mockk.Runs
import io.mockk.coEvery
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import junit.framework.TestCase.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import xelagurd.socialdating.MainDispatcherRule
import xelagurd.socialdating.data.fake.FakeDataSource
import xelagurd.socialdating.data.fake.toUserCategoriesWithData
import xelagurd.socialdating.data.fake.toUserDefiningThemesWithData
import xelagurd.socialdating.data.local.repository.LocalCategoriesRepository
import xelagurd.socialdating.data.local.repository.LocalDefiningThemesRepository
import xelagurd.socialdating.data.local.repository.LocalUserCategoriesRepository
import xelagurd.socialdating.data.local.repository.LocalUserDefiningThemesRepository
import xelagurd.socialdating.data.model.Category
import xelagurd.socialdating.data.model.DefiningTheme
import xelagurd.socialdating.data.model.UserCategory
import xelagurd.socialdating.data.model.UserCategoryWithData
import xelagurd.socialdating.data.model.UserDefiningTheme
import xelagurd.socialdating.data.model.UserDefiningThemeWithData
import xelagurd.socialdating.data.remote.repository.RemoteCategoriesRepository
import xelagurd.socialdating.data.remote.repository.RemoteDefiningThemesRepository
import xelagurd.socialdating.data.remote.repository.RemoteUserCategoriesRepository
import xelagurd.socialdating.data.remote.repository.RemoteUserDefiningThemesRepository
import xelagurd.socialdating.mergeListsAsSets
import xelagurd.socialdating.ui.state.InternetStatus
import xelagurd.socialdating.ui.viewmodel.ProfileStatisticsViewModel

@OptIn(ExperimentalCoroutinesApi::class)
class ProfileStatisticsViewModelTest {
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val savedStateHandle: SavedStateHandle = mockk()
    private val remoteUserCategoriesRepository: RemoteUserCategoriesRepository = mockk()
    private val localUserCategoriesRepository: LocalUserCategoriesRepository = mockk()
    private val remoteUserDefiningThemesRepository: RemoteUserDefiningThemesRepository = mockk()
    private val localUserDefiningThemesRepository: LocalUserDefiningThemesRepository = mockk()
    private val remoteCategoriesRepository: RemoteCategoriesRepository = mockk()
    private val localCategoriesRepository: LocalCategoriesRepository = mockk()
    private val remoteDefiningThemesRepository: RemoteDefiningThemesRepository = mockk()
    private val localDefiningThemesRepository: LocalDefiningThemesRepository = mockk()

    private lateinit var viewModel: ProfileStatisticsViewModel
    private lateinit var userCategoriesFlow: MutableStateFlow<List<UserCategoryWithData>>
    private lateinit var userDefiningThemesFlow: MutableStateFlow<List<UserDefiningThemeWithData>>

    private val userId = 1

    private val localCategories = listOf(
        Category(1, "")
    )
    private val remoteCategories = listOf(
        Category(1, ""),
        Category(2, "")
    )
    private val localDefiningThemes = listOf(
        DefiningTheme(1, "", "", "", 1),
        DefiningTheme(2, "", "", "", 1)
    )
    private val remoteDefiningThemes = listOf(
        DefiningTheme(1, "", "", "", 1),
        DefiningTheme(2, "", "", "", 1),
        DefiningTheme(3, "", "", "", 2)
    )
    private val localUserCategories = listOf(
        UserCategory(1, 50, userId, 1)
    )
    private val remoteUserCategories = listOf(
        UserCategory(1, 50, userId, 1),
        UserCategory(2, 50, userId, 2)
    )
    private val localUserDefiningThemes = listOf(
        UserDefiningTheme(1, 50, 50, 1, 1),
        UserDefiningTheme(2, 50, 50, 1, 2)
    )
    private val remoteUserDefiningThemes = listOf(
        UserDefiningTheme(1, 50, 50, 1, 1),
        UserDefiningTheme(2, 50, 50, 1, 2),
        UserDefiningTheme(3, 50, 50, 2, 3)
    )

    private fun List<Category>.toCategoryIds() = this.map { it.id }
    private fun List<UserCategory>.toUserCategoryIds() = this.map { it.id }
    private fun MutableStateFlow<List<UserCategoryWithData>>.toIds() = this.value.map { it.id }

    @Before
    fun setup() {
        userCategoriesFlow = MutableStateFlow(localUserCategories.toUserCategoriesWithData())
        userDefiningThemesFlow =
            MutableStateFlow(localUserDefiningThemes.toUserDefiningThemesWithData())

        mockGeneralMethods()

        viewModel = ProfileStatisticsViewModel(
            savedStateHandle,
            remoteUserCategoriesRepository,
            localUserCategoriesRepository,
            remoteUserDefiningThemesRepository,
            localUserDefiningThemesRepository,
            remoteCategoriesRepository,
            localCategoriesRepository,
            remoteDefiningThemesRepository,
            localDefiningThemesRepository
        )
    }

    @Test
    fun profileStatisticsViewModel_checkInitialState() = runTest {
        mockDataWithInternet()
        mockLocalProfileStatistics()

        assertEquals(InternetStatus.LOADING, viewModel.internetStatus)
        assertEquals(
            localUserCategories.toUserCategoriesWithData(),
            localUserCategoriesRepository.getUserCategories(userId).first()
        )
        assertEquals(
            localUserDefiningThemes.toUserDefiningThemesWithData(),
            localUserDefiningThemesRepository.getUserDefiningThemes(userCategoriesFlow.toIds())
                .first()
        )
    }

    @Test
    fun profileStatisticsViewModel_checkDataWithInternet() = runTest {
        mockDataWithInternet()
        advanceUntilIdle()
        mockLocalProfileStatistics()

        assertEquals(InternetStatus.ONLINE, viewModel.internetStatus)
        assertEquals(
            mergeListsAsSets(localUserCategories, remoteUserCategories).toUserCategoriesWithData(),
            localUserCategoriesRepository.getUserCategories(userId).first()
        )
        assertEquals(
            mergeListsAsSets(localUserDefiningThemes, remoteUserDefiningThemes)
                .toUserDefiningThemesWithData(),
            localUserDefiningThemesRepository.getUserDefiningThemes(userCategoriesFlow.toIds())
                .first()
        )
    }

    @Test
    fun profileStatisticsViewModel_checkDataWithoutInternet() = runTest {
        mockDataWithoutInternet()
        advanceUntilIdle()
        mockLocalProfileStatistics()

        assertEquals(InternetStatus.OFFLINE, viewModel.internetStatus)
        assertEquals(
            mergeListsAsSets(localUserCategories, FakeDataSource.userCategories)
                .toUserCategoriesWithData(),
            localUserCategoriesRepository.getUserCategories(userId).first()
        )
        assertEquals(
            mergeListsAsSets(localUserDefiningThemes, FakeDataSource.userDefiningThemes)
                .toUserDefiningThemesWithData(),
            localUserDefiningThemesRepository.getUserDefiningThemes(userCategoriesFlow.toIds())
                .first()
        )
    }

    @Test
    fun profileStatisticsViewModel_checkRefreshedOnlineDataWithoutInternet() = runTest {
        mockDataWithInternet()
        advanceUntilIdle()

        mockDataWithoutInternet()
        viewModel.getProfileStatistics()
        advanceUntilIdle()
        mockLocalProfileStatistics()

        assertEquals(InternetStatus.OFFLINE, viewModel.internetStatus)
        assertEquals(
            mergeListsAsSets(
                localUserCategories,
                remoteUserCategories,
                FakeDataSource.userCategories
            ).toUserCategoriesWithData(),
            localUserCategoriesRepository.getUserCategories(userId).first()
        )
        assertEquals(
            mergeListsAsSets(
                localUserDefiningThemes,
                remoteUserDefiningThemes,
                FakeDataSource.userDefiningThemes
            ).toUserDefiningThemesWithData(),
            localUserDefiningThemesRepository.getUserDefiningThemes(userCategoriesFlow.toIds())
                .first()
        )
    }

    @Test
    fun profileStatisticsViewModel_checkRefreshedOfflineDataWithInternet() = runTest {
        mockDataWithoutInternet()
        advanceUntilIdle()

        mockDataWithInternet()
        viewModel.getProfileStatistics()
        advanceUntilIdle()
        mockLocalProfileStatistics()

        assertEquals(InternetStatus.ONLINE, viewModel.internetStatus)
        assertEquals(
            mergeListsAsSets(
                localUserCategories,
                FakeDataSource.userCategories,
                remoteUserCategories
            ).toUserCategoriesWithData(),
            localUserCategoriesRepository.getUserCategories(userId).first()
        )
        assertEquals(
            mergeListsAsSets(
                localUserDefiningThemes,
                FakeDataSource.userDefiningThemes,
                remoteUserDefiningThemes
            ).toUserDefiningThemesWithData(),
            localUserDefiningThemesRepository.getUserDefiningThemes(userCategoriesFlow.toIds())
                .first()
        )
    }

    @Test
    fun profileStatisticsViewModel_checkRefreshedOnlineDataWithInternet() = runTest {
        mockDataWithInternet()
        advanceUntilIdle()

        viewModel.getProfileStatistics()
        advanceUntilIdle()
        mockLocalProfileStatistics()

        assertEquals(InternetStatus.ONLINE, viewModel.internetStatus)
        assertEquals(
            mergeListsAsSets(localUserCategories, remoteUserCategories).toUserCategoriesWithData(),
            localUserCategoriesRepository.getUserCategories(userId).first()
        )
        assertEquals(
            mergeListsAsSets(localUserDefiningThemes, remoteUserDefiningThemes)
                .toUserDefiningThemesWithData(),
            localUserDefiningThemesRepository.getUserDefiningThemes(userCategoriesFlow.toIds())
                .first()
        )
    }

    @Test
    fun profileStatisticsViewModel_checkRefreshedOfflineDataWithoutInternet() = runTest {
        mockDataWithoutInternet()
        advanceUntilIdle()

        viewModel.getProfileStatistics()
        advanceUntilIdle()
        mockLocalProfileStatistics()

        assertEquals(InternetStatus.OFFLINE, viewModel.internetStatus)
        assertEquals(
            mergeListsAsSets(localUserCategories, FakeDataSource.userCategories)
                .toUserCategoriesWithData(),
            localUserCategoriesRepository.getUserCategories(userId).first()
        )
        assertEquals(
            mergeListsAsSets(localUserDefiningThemes, FakeDataSource.userDefiningThemes)
                .toUserDefiningThemesWithData(),
            localUserDefiningThemesRepository.getUserDefiningThemes(userCategoriesFlow.toIds())
                .first()
        )
    }

    private fun mockLocalProfileStatistics() {
        every { localUserDefiningThemesRepository.getUserDefiningThemes(userCategoriesFlow.toIds()) } returns userDefiningThemesFlow
    }

    private fun mockGeneralMethods() {
        every { savedStateHandle.get<Int>("userId") } returns userId
        every { localUserCategoriesRepository.getUserCategories(userId) } returns userCategoriesFlow
    }

    private fun mockDataWithInternet() {
        coEvery { remoteCategoriesRepository.getCategories() } returns remoteCategories
        coEvery { remoteDefiningThemesRepository.getDefiningThemes(remoteCategories.toCategoryIds()) } returns remoteDefiningThemes
        coEvery { remoteUserCategoriesRepository.getUserCategories(userId) } returns remoteUserCategories
        coEvery { remoteUserDefiningThemesRepository.getUserDefiningThemes(remoteUserCategories.toUserCategoryIds()) } returns remoteUserDefiningThemes

        coEvery { localCategoriesRepository.insertCategories(remoteCategories) } just Runs
        coEvery { localDefiningThemesRepository.insertDefiningThemes(remoteDefiningThemes) } just Runs
        coEvery { localUserCategoriesRepository.insertUserCategories(remoteUserCategories) } answers {
            userCategoriesFlow.value =
                mergeListsAsSets(
                    userCategoriesFlow.value,
                    remoteUserCategories.toUserCategoriesWithData()
                )
        }
        coEvery {
            localUserDefiningThemesRepository.insertUserDefiningThemes(
                remoteUserDefiningThemes
            )
        } answers {
            userDefiningThemesFlow.value =
                mergeListsAsSets(
                    userDefiningThemesFlow.value,
                    remoteUserDefiningThemes.toUserDefiningThemesWithData()
                )
        }
    }

    private fun mockDataWithoutInternet() {
        coEvery { remoteCategoriesRepository.getCategories() } throws IOException()

        coEvery { localCategoriesRepository.insertCategories(FakeDataSource.categories) } just Runs
        coEvery { localDefiningThemesRepository.insertDefiningThemes(FakeDataSource.definingThemes) } just Runs
        coEvery { localUserCategoriesRepository.insertUserCategories(FakeDataSource.userCategories) } answers {
            userCategoriesFlow.value =
                mergeListsAsSets(
                    userCategoriesFlow.value,
                    FakeDataSource.userCategories.toUserCategoriesWithData()
                )
        }
        coEvery { localUserDefiningThemesRepository.insertUserDefiningThemes(FakeDataSource.userDefiningThemes) } answers {
            userDefiningThemesFlow.value =
                mergeListsAsSets(
                    userDefiningThemesFlow.value,
                    FakeDataSource.userDefiningThemes.toUserDefiningThemesWithData()
                )
        }
    }
}