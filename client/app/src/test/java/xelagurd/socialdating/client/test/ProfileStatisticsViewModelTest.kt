package xelagurd.socialdating.client.test

import java.io.IOException
import kotlin.random.Random
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
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
import xelagurd.socialdating.client.data.model.DefaultDataProperties.ID_MIN
import xelagurd.socialdating.client.MainDispatcherRule
import xelagurd.socialdating.client.data.PreferencesRepository
import xelagurd.socialdating.client.data.fake.FakeData
import xelagurd.socialdating.client.data.local.repository.CommonLocalRepository
import xelagurd.socialdating.client.data.local.repository.LocalCategoriesRepository
import xelagurd.socialdating.client.data.local.repository.LocalDefiningThemesRepository
import xelagurd.socialdating.client.data.local.repository.LocalUserCategoriesRepository
import xelagurd.socialdating.client.data.local.repository.LocalUserDefiningThemesRepository
import xelagurd.socialdating.client.data.model.Category
import xelagurd.socialdating.client.data.model.DataUtils.toUserCategoriesWithData
import xelagurd.socialdating.client.data.model.DataUtils.toUserDefiningThemesWithData
import xelagurd.socialdating.client.data.model.DefiningTheme
import xelagurd.socialdating.client.data.model.UserCategory
import xelagurd.socialdating.client.data.model.UserDefiningTheme
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
    private lateinit var userCategoriesFlow: MutableStateFlow<List<UserCategoryWithData>>
    private lateinit var userDefiningThemesFlow: MutableStateFlow<List<UserDefiningThemeWithData>>
    private val profileStatisticsUiState
        get() = viewModel.uiState.value

    private val userId = Random.nextInt(ID_MIN, Int.MAX_VALUE)
    private var anotherUserId = userId

    private val savedCategories = listOf(Category(id = 1, name = "Category1"))
    private val missingCategories = listOf(Category(id = 2, name = "Category2"))
    private val savedDefiningThemes = listOf(
        DefiningTheme(
            id = 1,
            name = "DefiningTheme1",
            fromOpinion = "No",
            toOpinion = "Yes",
            categoryId = 1,
            numberInCategory = 1
        )
    )
    private val missingDefiningThemes = listOf(
        DefiningTheme(
            id = 2,
            name = "DefiningTheme2",
            fromOpinion = "No",
            toOpinion = "Yes",
            categoryId = 2,
            numberInCategory = 1
        )
    )
    private val remoteUserCategories = listOf(
        UserCategory(id = 1, interest = 10, userId = userId, categoryId = 1),
        UserCategory(id = 2, interest = 20, userId = userId, categoryId = 2)
    )
    private val remoteUserDefiningThemes = listOf(
        UserDefiningTheme(id = 1, value = 10, interest = 10, userId = userId, definingThemeId = 1),
        UserDefiningTheme(id = 2, value = 20, interest = 20, userId = userId, definingThemeId = 2)
    )
    private val allCategories = savedCategories + missingCategories
    private val allDefiningThemes = savedDefiningThemes + missingDefiningThemes

    private val isOfflineModeFlow = flowOf(false)
    private var categoriesFlow: Flow<List<Category>> = flowOf(allCategories)
    private var definingThemesFlow: Flow<List<DefiningTheme>> = flowOf(allDefiningThemes)

    @Before
    fun setup() {
        userCategoriesFlow = MutableStateFlow(remoteUserCategories.toUserCategoriesWithData(allCategories))
        userDefiningThemesFlow =
            MutableStateFlow(remoteUserDefiningThemes.toUserDefiningThemesWithData(allDefiningThemes))
    }

    private fun initViewModel() {
        mockGeneralMethods()

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
    fun profileStatisticsViewModel_withInternet_successStatus() = runTest {
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
    fun profileStatisticsViewModel_withEmptyRemoteUserDefiningThemes_successStatus() = runTest {
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
    fun profileStatisticsViewModel_withEmptyRemoteUserCategories_successStatus() = runTest {
        mockEmptyUserCategories()

        initViewModel()
        setupUiStateCollecting()
        advanceUntilIdle()

        assertEquals(RequestStatus.SUCCESS, profileStatisticsUiState.dataRequestStatus)

        verify(exactly = 1) { localUserCategoriesRepository.getUserCategories(any()) }
        verify(exactly = 1) { localUserDefiningThemesRepository.getUserDefiningThemes(any()) }
        coVerify(exactly = 1) { remoteUserDefiningThemesRepository.getUserDefiningThemes(any()) }
        coVerify(exactly = 1) { remoteUserCategoriesRepository.getUserCategories(any()) }
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
    fun profileStatisticsViewModel_withoutInternet_errorStatus() = runTest {
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
    fun profileStatisticsViewModel_refreshWithoutInternetAfterSuccess_errorStatus() = runTest {
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
    fun profileStatisticsViewModel_refreshWithInternetAfterError_successStatus() = runTest {
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
    fun profileStatisticsViewModel_refreshWithInternetAfterSuccess_successStatus() = runTest {
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
    fun profileStatisticsViewModel_refreshWithoutInternetAfterError_errorStatus() = runTest {
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

    @Test
    fun profileStatisticsViewModel_withMissingLocalDataAndInternet_successStatus() = runTest {
        mockMissingLocalData()
        mockDataWithInternet()

        initViewModel()
        setupUiStateCollecting()
        advanceUntilIdle()

        assertEquals(RequestStatus.SUCCESS, profileStatisticsUiState.dataRequestStatus)

        coVerify(exactly = 1) { remoteDefiningThemesRepository.getDefiningThemes(missingDefiningThemes.map { it.id }) }
        coVerify(exactly = 1) { remoteCategoriesRepository.getCategories(missingCategories.map { it.id }) }
        coVerify(exactly = 1) { commonLocalRepository.updateProfileStatisticsScreenData(any(), any(), any(), any()) }
    }

    @Test
    fun profileStatisticsViewModel_withMissingDefiningThemesAndWithoutInternet_errorStatus() = runTest {
        mockMissingLocalData()
        mockDataWithInternet()
        mockMissingDefiningThemesWithoutInternet()

        initViewModel()
        setupUiStateCollecting()
        advanceUntilIdle()

        assertEquals(RequestStatus.ERROR(), profileStatisticsUiState.dataRequestStatus)

        coVerify(exactly = 1) { remoteDefiningThemesRepository.getDefiningThemes(any()) }
        coVerify(exactly = 0) { remoteCategoriesRepository.getCategories(any()) }
        coVerify(exactly = 0) { commonLocalRepository.updateProfileStatisticsScreenData(any(), any(), any(), any()) }
    }

    @Test
    fun profileStatisticsViewModel_withMissingCategoriesAndWithoutInternet_errorStatus() = runTest {
        mockMissingLocalData()
        mockDataWithInternet()
        mockMissingCategoriesWithoutInternet()

        initViewModel()
        setupUiStateCollecting()
        advanceUntilIdle()

        assertEquals(RequestStatus.ERROR(), profileStatisticsUiState.dataRequestStatus)

        coVerify(exactly = 1) { remoteDefiningThemesRepository.getDefiningThemes(any()) }
        coVerify(exactly = 1) { remoteCategoriesRepository.getCategories(any()) }
        coVerify(exactly = 0) { commonLocalRepository.updateProfileStatisticsScreenData(any(), any(), any(), any()) }
    }

    @Test
    fun profileStatisticsViewModel_anotherUserWithMissingLocalDataAndInternet_successStatus() = runTest {
        anotherUserId = userId + 1
        mockMissingLocalData()
        mockDataWithInternet()

        initViewModel()
        setupUiStateCollecting()
        advanceUntilIdle()

        assertEquals(RequestStatus.SUCCESS, profileStatisticsUiState.dataRequestStatus)
        assertEquals(FakeData.detailedSimilarUser, profileStatisticsUiState.entitiesMask)
        assertEquals(remoteUserCategories.toUserCategoriesWithData(allCategories), profileStatisticsUiState.entities)

        coVerify(exactly = 1) { remoteUserCategoriesRepository.getDetailedSimilarUser(any(), any()) }
        coVerify(exactly = 1) { commonLocalRepository.updateProfileStatisticsScreenData(any(), any()) }
    }

    @Test
    fun profileStatisticsViewModel_anotherUserWithInternet_successStatus() = runTest {
        anotherUserId = userId + 1
        mockDataWithInternet()

        initViewModel()
        setupUiStateCollecting()
        advanceUntilIdle()

        assertEquals(RequestStatus.SUCCESS, profileStatisticsUiState.dataRequestStatus)
        assertEquals(FakeData.detailedSimilarUser, profileStatisticsUiState.entitiesMask)
        assertEquals(remoteUserCategories.toUserCategoriesWithData(allCategories), profileStatisticsUiState.entities)

        coVerify(exactly = 1) { remoteUserCategoriesRepository.getDetailedSimilarUser(any(), any()) }
        coVerify(exactly = 1) { commonLocalRepository.updateProfileStatisticsScreenData(any(), any()) }
    }

    @Test
    fun profileStatisticsViewModel_anotherUserWithEmptyRemoteDetailedSimilarUser_successStatus() = runTest {
        anotherUserId = userId + 1
        mockDataWithInternet()
        mockEmptyDetailedSimilarUser()

        initViewModel()
        setupUiStateCollecting()
        advanceUntilIdle()

        assertEquals(RequestStatus.SUCCESS, profileStatisticsUiState.dataRequestStatus)

        coVerify(exactly = 1) { remoteUserCategoriesRepository.getDetailedSimilarUser(any(), any()) }
        coVerify(exactly = 0) { remoteUserDefiningThemesRepository.getUserDefiningThemes(any()) }
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

    private fun mockMissingLocalData() {
        categoriesFlow = flowOf(savedCategories)
        definingThemesFlow = flowOf(savedDefiningThemes)
    }

    private fun mockDataWithInternet() {
        coEvery { remoteUserCategoriesRepository.getDetailedSimilarUser(any(), any()) } returns
                Response.success(FakeData.detailedSimilarUser)
        coEvery { remoteUserCategoriesRepository.getUserCategories(any()) } returns
                Response.success(remoteUserCategories)
        coEvery { remoteUserDefiningThemesRepository.getUserDefiningThemes(any()) } returns
                Response.success(remoteUserDefiningThemes)
        coEvery { remoteCategoriesRepository.getCategories(any()) } returns Response.success(missingCategories)
        coEvery { remoteDefiningThemesRepository.getDefiningThemes(any()) } returns
                Response.success(missingDefiningThemes)

        coEvery { commonLocalRepository.updateProfileStatisticsScreenData(any(), any(), any(), any()) } just Runs
    }

    private fun mockEmptyData() {
        coEvery { remoteUserDefiningThemesRepository.getUserDefiningThemes(any()) } returns Response.success(null)
    }

    private fun mockEmptyUserCategories() {
        coEvery { remoteUserDefiningThemesRepository.getUserDefiningThemes(any()) } returns
                Response.success(remoteUserDefiningThemes)
        coEvery { remoteUserCategoriesRepository.getUserCategories(any()) } returns Response.success(null)
    }

    private fun mockEmptyDetailedSimilarUser() {
        coEvery { remoteUserCategoriesRepository.getDetailedSimilarUser(any(), any()) } returns Response.success(null)
    }

    private fun mockDataWithoutInternet() {
        coEvery { remoteUserDefiningThemesRepository.getUserDefiningThemes(any()) } throws IOException()
    }

    private fun mockMissingDefiningThemesWithoutInternet() {
        coEvery { remoteDefiningThemesRepository.getDefiningThemes(any()) } throws IOException()
    }

    private fun mockMissingCategoriesWithoutInternet() {
        coEvery { remoteCategoriesRepository.getCategories(any()) } throws IOException()
    }
}