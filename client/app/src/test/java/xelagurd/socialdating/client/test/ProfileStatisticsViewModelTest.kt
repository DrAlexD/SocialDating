package xelagurd.socialdating.client.test

import java.io.IOException
import kotlin.random.Random
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
import io.mockk.coVerify
import io.mockk.confirmVerified
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.verify
import junit.framework.TestCase.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import retrofit2.Response
import xelagurd.socialdating.client.MainDispatcherRule
import xelagurd.socialdating.client.TestUtils.mockkList
import xelagurd.socialdating.client.data.PreferencesRepository
import xelagurd.socialdating.client.data.local.repository.CommonLocalRepository
import xelagurd.socialdating.client.data.local.repository.LocalCategoriesRepository
import xelagurd.socialdating.client.data.local.repository.LocalDefiningThemesRepository
import xelagurd.socialdating.client.data.local.repository.LocalUserCategoriesRepository
import xelagurd.socialdating.client.data.local.repository.LocalUserDefiningThemesRepository
import xelagurd.socialdating.client.data.model.Category
import xelagurd.socialdating.client.data.model.DefiningTheme
import xelagurd.socialdating.client.data.model.ui.UserCategoryWithData
import xelagurd.socialdating.client.data.model.ui.UserDefiningThemeWithData
import xelagurd.socialdating.client.data.remote.repository.RemoteCategoriesRepository
import xelagurd.socialdating.client.data.remote.repository.RemoteDefiningThemesRepository
import xelagurd.socialdating.client.data.remote.repository.RemoteUserCategoriesRepository
import xelagurd.socialdating.client.data.remote.repository.RemoteUserDefiningThemesRepository
import xelagurd.socialdating.client.ui.navigation.ProfileStatisticsDestination
import xelagurd.socialdating.client.ui.state.RequestStatus
import xelagurd.socialdating.client.ui.viewmodel.ProfileStatisticsViewModel

@OptIn(ExperimentalCoroutinesApi::class)
class ProfileStatisticsViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val context = mockk<Context>(relaxed = true)
    private val savedStateHandle = mockk<SavedStateHandle>()
    private val preferencesRepository = mockk<PreferencesRepository>()
    private val remoteUserCategoriesRepository = mockk<RemoteUserCategoriesRepository>()
    private val localUserCategoriesRepository = mockk<LocalUserCategoriesRepository>()
    private val remoteUserDefiningThemesRepository = mockk<RemoteUserDefiningThemesRepository>()
    private val localUserDefiningThemesRepository = mockk<LocalUserDefiningThemesRepository>()
    private val remoteCategoriesRepository = mockk<RemoteCategoriesRepository>()
    private val localCategoriesRepository = mockk<LocalCategoriesRepository>()
    private val remoteDefiningThemesRepository = mockk<RemoteDefiningThemesRepository>()
    private val localDefiningThemesRepository = mockk<LocalDefiningThemesRepository>()
    private val commonLocalRepository = mockk<CommonLocalRepository>()

    private lateinit var viewModel: ProfileStatisticsViewModel
    private lateinit var categoriesFlow: MutableStateFlow<List<Category>>
    private lateinit var definingThemesFlow: MutableStateFlow<List<DefiningTheme>>
    private lateinit var userCategoriesFlow: MutableStateFlow<List<UserCategoryWithData>>
    private lateinit var userDefiningThemesFlow: MutableStateFlow<List<UserDefiningThemeWithData>>
    private val profileStatisticsUiState
        get() = viewModel.uiState.value

    private val userId = Random.nextInt()
    private val anotherUserId = userId
    private val isOfflineModeFlow = flowOf(false)

    @Before
    fun setup() {
        categoriesFlow = MutableStateFlow(mockkList(relaxed = true))
        definingThemesFlow = MutableStateFlow(mockkList(relaxed = true))
        userCategoriesFlow = MutableStateFlow(mockkList())
        userDefiningThemesFlow = MutableStateFlow(mockkList(relaxed = true))

        mockGeneralMethods()
    }

    private fun initViewModel() {
        viewModel = ProfileStatisticsViewModel(
            context,
            savedStateHandle,
            preferencesRepository,
            remoteUserCategoriesRepository,
            localUserCategoriesRepository,
            remoteUserDefiningThemesRepository,
            localUserDefiningThemesRepository,
            remoteCategoriesRepository,
            localCategoriesRepository,
            remoteDefiningThemesRepository,
            localDefiningThemesRepository,
            commonLocalRepository
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

        verify(exactly = 1) { localCategoriesRepository.getCategories() }
        verify(exactly = 1) { localDefiningThemesRepository.getDefiningThemes() }
        verify(exactly = 1) { localUserCategoriesRepository.getUserCategories(any()) }
        verify(exactly = 1) { localUserDefiningThemesRepository.getUserDefiningThemes(any()) }
        coVerify(exactly = 1) { remoteUserCategoriesRepository.getUserCategories(any()) }
        coVerify(exactly = 1) { remoteUserDefiningThemesRepository.getUserDefiningThemes(any()) }
        coVerify(exactly = 1) { commonLocalRepository.updateProfileStatisticsScreenData(any(), any(), any(), any()) }
        confirmVerified(
            localUserCategoriesRepository,
            remoteUserCategoriesRepository,
            localUserDefiningThemesRepository,
            remoteUserDefiningThemesRepository,
            localCategoriesRepository,
            remoteCategoriesRepository,
            localDefiningThemesRepository,
            remoteDefiningThemesRepository,
            commonLocalRepository
        )
    }

    @Test
    fun profileStatisticsViewModel_checkStateWithEmptyData() = runTest {
        mockEmptyData()

        initViewModel()
        setupUiStateCollecting()
        advanceUntilIdle()

        assertEquals(RequestStatus.SUCCESS, profileStatisticsUiState.dataRequestStatus)

        verify(exactly = 1) { localUserCategoriesRepository.getUserCategories(any()) }
        verify(exactly = 1) { localUserDefiningThemesRepository.getUserDefiningThemes(any()) }
        coVerify(exactly = 1) { remoteUserDefiningThemesRepository.getUserDefiningThemes(any()) }
        confirmVerified(
            localUserCategoriesRepository,
            remoteUserCategoriesRepository,
            localUserDefiningThemesRepository,
            remoteUserDefiningThemesRepository,
            localCategoriesRepository,
            remoteCategoriesRepository,
            localDefiningThemesRepository,
            remoteDefiningThemesRepository,
            commonLocalRepository
        )
    }

    @Test
    fun profileStatisticsViewModel_checkStateWithoutInternet() = runTest {
        mockDataWithoutInternet()

        initViewModel()
        setupUiStateCollecting()
        advanceUntilIdle()

        assertEquals(RequestStatus.ERROR(), profileStatisticsUiState.dataRequestStatus)

        verify(exactly = 1) { localUserCategoriesRepository.getUserCategories(any()) }
        verify(exactly = 1) { localUserDefiningThemesRepository.getUserDefiningThemes(any()) }
        coVerify(exactly = 1) { remoteUserDefiningThemesRepository.getUserDefiningThemes(any()) }
        confirmVerified(
            localUserCategoriesRepository,
            remoteUserCategoriesRepository,
            localUserDefiningThemesRepository,
            remoteUserDefiningThemesRepository,
            localCategoriesRepository,
            remoteCategoriesRepository,
            localDefiningThemesRepository,
            remoteDefiningThemesRepository,
            commonLocalRepository
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

        verify(exactly = 1) { localCategoriesRepository.getCategories() }
        verify(exactly = 1) { localDefiningThemesRepository.getDefiningThemes() }
        verify(exactly = 1) { localUserCategoriesRepository.getUserCategories(any()) }
        verify(exactly = 1) { localUserDefiningThemesRepository.getUserDefiningThemes(any()) }
        coVerify(exactly = 1) { remoteUserCategoriesRepository.getUserCategories(any()) }
        coVerify(exactly = 2) { remoteUserDefiningThemesRepository.getUserDefiningThemes(any()) }
        coVerify(exactly = 1) { commonLocalRepository.updateProfileStatisticsScreenData(any(), any(), any(), any()) }
        confirmVerified(
            localUserCategoriesRepository,
            remoteUserCategoriesRepository,
            localUserDefiningThemesRepository,
            remoteUserDefiningThemesRepository,
            localCategoriesRepository,
            remoteCategoriesRepository,
            localDefiningThemesRepository,
            remoteDefiningThemesRepository,
            commonLocalRepository
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

        verify(exactly = 1) { localCategoriesRepository.getCategories() }
        verify(exactly = 1) { localDefiningThemesRepository.getDefiningThemes() }
        verify(exactly = 1) { localUserCategoriesRepository.getUserCategories(any()) }
        verify(exactly = 1) { localUserDefiningThemesRepository.getUserDefiningThemes(any()) }
        coVerify(exactly = 1) { remoteUserCategoriesRepository.getUserCategories(any()) }
        coVerify(exactly = 2) { remoteUserDefiningThemesRepository.getUserDefiningThemes(any()) }
        coVerify(exactly = 1) { commonLocalRepository.updateProfileStatisticsScreenData(any(), any(), any(), any()) }
        confirmVerified(
            localUserCategoriesRepository,
            remoteUserCategoriesRepository,
            localUserDefiningThemesRepository,
            remoteUserDefiningThemesRepository,
            localCategoriesRepository,
            remoteCategoriesRepository,
            localDefiningThemesRepository,
            remoteDefiningThemesRepository,
            commonLocalRepository
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

        verify(exactly = 2) { localCategoriesRepository.getCategories() }
        verify(exactly = 2) { localDefiningThemesRepository.getDefiningThemes() }
        verify(exactly = 1) { localUserCategoriesRepository.getUserCategories(any()) }
        verify(exactly = 1) { localUserDefiningThemesRepository.getUserDefiningThemes(any()) }
        coVerify(exactly = 2) { remoteUserCategoriesRepository.getUserCategories(any()) }
        coVerify(exactly = 2) { remoteUserDefiningThemesRepository.getUserDefiningThemes(any()) }
        coVerify(exactly = 2) { commonLocalRepository.updateProfileStatisticsScreenData(any(), any(), any(), any()) }
        confirmVerified(
            localUserCategoriesRepository,
            remoteUserCategoriesRepository,
            localUserDefiningThemesRepository,
            remoteUserDefiningThemesRepository,
            localCategoriesRepository,
            remoteCategoriesRepository,
            localDefiningThemesRepository,
            remoteDefiningThemesRepository,
            commonLocalRepository
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

        verify(exactly = 1) { localUserCategoriesRepository.getUserCategories(any()) }
        verify(exactly = 1) { localUserDefiningThemesRepository.getUserDefiningThemes(any()) }
        coVerify(exactly = 2) { remoteUserDefiningThemesRepository.getUserDefiningThemes(any()) }
        confirmVerified(
            localUserCategoriesRepository,
            remoteUserCategoriesRepository,
            localUserDefiningThemesRepository,
            remoteUserDefiningThemesRepository,
            localCategoriesRepository,
            remoteCategoriesRepository,
            localDefiningThemesRepository,
            remoteDefiningThemesRepository,
            commonLocalRepository
        )
    }

    private fun mockGeneralMethods() {
        every { savedStateHandle.get<Int>(ProfileStatisticsDestination.userId) } returns userId
        every { savedStateHandle.get<Int>(ProfileStatisticsDestination.anotherUserId) } returns anotherUserId
        every { preferencesRepository.isOfflineMode } returns isOfflineModeFlow
        every { localCategoriesRepository.getCategories() } returns categoriesFlow
        every { localDefiningThemesRepository.getDefiningThemes() } returns definingThemesFlow
        every { localUserCategoriesRepository.getUserCategories(any()) } returns userCategoriesFlow
        every { localUserDefiningThemesRepository.getUserDefiningThemes(any()) } returns userDefiningThemesFlow
    }

    private fun mockDataWithInternet() {
        coEvery { remoteUserCategoriesRepository.getUserCategories(any()) } returns
                Response.success(mockkList(relaxed = true))
        coEvery { remoteUserDefiningThemesRepository.getUserDefiningThemes(any()) } returns
                Response.success(mockkList(relaxed = true))

        coEvery { commonLocalRepository.updateProfileStatisticsScreenData(any(), any(), any(), any()) } just Runs
    }

    private fun mockEmptyData() {
        coEvery { remoteUserDefiningThemesRepository.getUserDefiningThemes(any()) } returns Response.success(null)
    }

    private fun mockDataWithoutInternet() {
        coEvery { remoteUserDefiningThemesRepository.getUserDefiningThemes(any()) } throws IOException()
    }
}