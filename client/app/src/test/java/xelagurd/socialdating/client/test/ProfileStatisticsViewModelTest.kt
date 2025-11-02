package xelagurd.socialdating.client.test

import java.io.IOException
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import android.content.Context
import androidx.lifecycle.SavedStateHandle
import io.mockk.Runs
import io.mockk.coEvery
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import junit.framework.TestCase.assertEquals
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import retrofit2.Response
import xelagurd.socialdating.client.MainDispatcherRule
import xelagurd.socialdating.client.data.fake.toUserCategoriesWithData
import xelagurd.socialdating.client.data.fake.toUserDefiningThemesWithData
import xelagurd.socialdating.client.data.local.repository.LocalCategoriesRepository
import xelagurd.socialdating.client.data.local.repository.LocalDefiningThemesRepository
import xelagurd.socialdating.client.data.local.repository.LocalUserCategoriesRepository
import xelagurd.socialdating.client.data.local.repository.LocalUserDefiningThemesRepository
import xelagurd.socialdating.client.data.model.Category
import xelagurd.socialdating.client.data.model.DefiningTheme
import xelagurd.socialdating.client.data.model.UserCategory
import xelagurd.socialdating.client.data.model.UserDefiningTheme
import xelagurd.socialdating.client.data.model.ui.UserCategoryWithData
import xelagurd.socialdating.client.data.model.ui.UserDefiningThemeWithData
import xelagurd.socialdating.client.data.remote.repository.RemoteCategoriesRepository
import xelagurd.socialdating.client.data.remote.repository.RemoteDefiningThemesRepository
import xelagurd.socialdating.client.data.remote.repository.RemoteUserCategoriesRepository
import xelagurd.socialdating.client.data.remote.repository.RemoteUserDefiningThemesRepository
import xelagurd.socialdating.client.mergeListsAsSets
import xelagurd.socialdating.client.ui.state.RequestStatus
import xelagurd.socialdating.client.ui.viewmodel.ProfileStatisticsViewModel

@OptIn(ExperimentalCoroutinesApi::class)
class ProfileStatisticsViewModelTest {
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val context: Context = mockk()
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
    private val profileStatisticsUiState
        get() = viewModel.uiState.value

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

    @Before
    fun setup() {
        userCategoriesFlow = MutableStateFlow(localUserCategories.toUserCategoriesWithData())
        userDefiningThemesFlow =
            MutableStateFlow(localUserDefiningThemes.toUserDefiningThemesWithData())

        mockGeneralMethods()
    }

    private fun initViewModel() {
        viewModel = ProfileStatisticsViewModel(
            context,
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

    private fun TestScope.setupUiStateCollecting() {
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
            viewModel.uiState.collect {}
        }
    }

    @Test
    fun profileStatisticsViewModel_checkStateWithInternet() = runTest {
        mockDataWithInternet()

        initViewModel()
        setupUiStateCollecting()
        advanceUntilIdle()

        assertEquals(RequestStatus.SUCCESS, profileStatisticsUiState.dataRequestStatus)
        assertEquals(
            mergeListsAsSets(localUserCategories, remoteUserCategories).toUserCategoriesWithData(),
            profileStatisticsUiState.entities
        )
        assertEquals(
            mergeListsAsSets(localUserDefiningThemes, remoteUserDefiningThemes)
                .toUserDefiningThemesWithData()
                .groupBy { it.categoryId },
            profileStatisticsUiState.entityIdToData
        )
    }

    @Test
    fun profileStatisticsViewModel_checkStateWithEmptyData() = runTest {
        mockEmptyData()

        initViewModel()
        setupUiStateCollecting()
        advanceUntilIdle()

        assertEquals(RequestStatus.FAILURE(), profileStatisticsUiState.dataRequestStatus)
        assertEquals(
            localUserCategories.toUserCategoriesWithData(),
            profileStatisticsUiState.entities
        )
        assertEquals(
            localUserDefiningThemes
                .toUserDefiningThemesWithData()
                .groupBy { it.categoryId },
            profileStatisticsUiState.entityIdToData
        )
    }

    @Test
    fun profileStatisticsViewModel_checkStateWithoutInternet() = runTest {
        mockDataWithoutInternet()

        initViewModel()
        setupUiStateCollecting()
        advanceUntilIdle()

        assertEquals(RequestStatus.ERROR(), profileStatisticsUiState.dataRequestStatus)
        assertEquals(
            localUserCategories.toUserCategoriesWithData(),
            profileStatisticsUiState.entities
        )
        assertEquals(
            localUserDefiningThemes.toUserDefiningThemesWithData().groupBy { it.categoryId },
            profileStatisticsUiState.entityIdToData
        )
    }

    @Test
    fun profileStatisticsViewModel_checkRefreshedSuccessStateWithoutInternet() = runTest {
        mockDataWithInternet()

        initViewModel()
        setupUiStateCollecting()
        advanceUntilIdle()

        mockDataWithoutInternet()

        viewModel.getProfileStatistics()
        advanceUntilIdle()

        assertEquals(RequestStatus.ERROR(), profileStatisticsUiState.dataRequestStatus)
        assertEquals(
            mergeListsAsSets(
                localUserCategories,
                remoteUserCategories
            ).toUserCategoriesWithData(),
            profileStatisticsUiState.entities
        )
        assertEquals(
            mergeListsAsSets(
                localUserDefiningThemes,
                remoteUserDefiningThemes
            )
                .toUserDefiningThemesWithData()
                .groupBy { it.categoryId },
            profileStatisticsUiState.entityIdToData
        )
    }

    @Test
    fun profileStatisticsViewModel_checkRefreshedErrorStateWithInternet() = runTest {
        mockDataWithoutInternet()

        initViewModel()
        setupUiStateCollecting()
        advanceUntilIdle()

        mockDataWithInternet()

        viewModel.getProfileStatistics()
        advanceUntilIdle()

        assertEquals(RequestStatus.SUCCESS, profileStatisticsUiState.dataRequestStatus)
        assertEquals(
            mergeListsAsSets(
                localUserCategories,
                remoteUserCategories
            ).toUserCategoriesWithData(),
            profileStatisticsUiState.entities
        )
        assertEquals(
            mergeListsAsSets(
                localUserDefiningThemes,
                remoteUserDefiningThemes
            )
                .toUserDefiningThemesWithData()
                .groupBy { it.categoryId },
            profileStatisticsUiState.entityIdToData
        )
    }

    @Test
    fun profileStatisticsViewModel_checkRefreshedSuccessStateWithInternet() = runTest {
        mockDataWithInternet()

        initViewModel()
        setupUiStateCollecting()
        advanceUntilIdle()

        viewModel.getProfileStatistics()
        advanceUntilIdle()

        assertEquals(RequestStatus.SUCCESS, profileStatisticsUiState.dataRequestStatus)
        assertEquals(
            mergeListsAsSets(localUserCategories, remoteUserCategories).toUserCategoriesWithData(),
            profileStatisticsUiState.entities
        )
        assertEquals(
            mergeListsAsSets(localUserDefiningThemes, remoteUserDefiningThemes)
                .toUserDefiningThemesWithData()
                .groupBy { it.categoryId },
            profileStatisticsUiState.entityIdToData
        )
    }

    @Test
    fun profileStatisticsViewModel_checkRefreshedErrorStateWithoutInternet() = runTest {
        mockDataWithoutInternet()

        initViewModel()
        setupUiStateCollecting()
        advanceUntilIdle()

        viewModel.getProfileStatistics()
        advanceUntilIdle()

        assertEquals(RequestStatus.ERROR(), profileStatisticsUiState.dataRequestStatus)
        assertEquals(
            localUserCategories.toUserCategoriesWithData(),
            profileStatisticsUiState.entities
        )
        assertEquals(
            localUserDefiningThemes.toUserDefiningThemesWithData().groupBy { it.categoryId },
            profileStatisticsUiState.entityIdToData
        )
    }

    private fun mockGeneralMethods() {
        every { savedStateHandle.get<Int>("userId") } returns userId
        every { localUserCategoriesRepository.getUserCategories(userId) } returns userCategoriesFlow
        every { localUserDefiningThemesRepository.getUserDefiningThemes(userId) } returns userDefiningThemesFlow
    }

    private fun mockDataWithInternet() {
        coEvery { remoteCategoriesRepository.getCategories() } returns
                Response.success(remoteCategories)
        coEvery { remoteDefiningThemesRepository.getDefiningThemes() } returns
                Response.success(remoteDefiningThemes)
        coEvery { remoteUserCategoriesRepository.getUserCategories(userId) } returns
                Response.success(remoteUserCategories)
        coEvery { remoteUserDefiningThemesRepository.getUserDefiningThemes(userId) } returns
                Response.success(remoteUserDefiningThemes)

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

    private fun mockEmptyData() {
        every { context.getString(any()) } returns ""
        coEvery { remoteCategoriesRepository.getCategories() } returns
                Response.error(404, "404".toResponseBody())
        coEvery { remoteDefiningThemesRepository.getDefiningThemes() } returns
                Response.error(404, "404".toResponseBody())
        coEvery { remoteUserCategoriesRepository.getUserCategories(userId) } returns
                Response.error(404, "404".toResponseBody())
        coEvery { remoteUserDefiningThemesRepository.getUserDefiningThemes(userId) } returns
                Response.error(404, "404".toResponseBody())

        coEvery { localCategoriesRepository.insertCategories(emptyList()) } just Runs
        coEvery { localDefiningThemesRepository.insertDefiningThemes(emptyList()) } just Runs
        coEvery { localUserCategoriesRepository.insertUserCategories(emptyList()) } just Runs
    }

    private fun mockDataWithoutInternet() {
        every { context.getString(any()) } returns ""
        coEvery { remoteCategoriesRepository.getCategories() } throws IOException()

        // FixMe: remove after adding server hosting
        every { localCategoriesRepository.getCategories() } returns flowOf(localCategories)
        every { localDefiningThemesRepository.getDefiningThemes() } returns flowOf(
            localDefiningThemes
        )
        every { localUserCategoriesRepository.getUserCategories() } returns flowOf(
            localUserCategories
        )
        every { localUserDefiningThemesRepository.getUserDefiningThemes() } returns flowOf(
            localUserDefiningThemes
        )
    }
}