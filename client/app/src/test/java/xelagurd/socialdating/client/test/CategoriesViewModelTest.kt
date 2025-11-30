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
import io.mockk.Runs
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.confirmVerified
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.verify
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import retrofit2.Response
import xelagurd.socialdating.client.MainDispatcherRule
import xelagurd.socialdating.client.TestUtils.mockkList
import xelagurd.socialdating.client.data.PreferencesRepository
import xelagurd.socialdating.client.data.local.repository.LocalCategoriesRepository
import xelagurd.socialdating.client.data.model.Category
import xelagurd.socialdating.client.data.remote.repository.RemoteCategoriesRepository
import xelagurd.socialdating.client.ui.state.RequestStatus
import xelagurd.socialdating.client.ui.viewmodel.CategoriesViewModel

@OptIn(ExperimentalCoroutinesApi::class)
class CategoriesViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val context = mockk<Context>(relaxed = true)
    private val preferencesRepository = mockk<PreferencesRepository>()
    private val remoteCategoriesRepository = mockk<RemoteCategoriesRepository>()
    private val localCategoriesRepository = mockk<LocalCategoriesRepository>()

    private lateinit var viewModel: CategoriesViewModel
    private lateinit var categoriesFlow: MutableStateFlow<List<Category>>
    private val categoriesUiState
        get() = viewModel.uiState.value

    private val isOfflineModeFlow = flowOf(false)

    @Before
    fun setup() {
        categoriesFlow = MutableStateFlow(mockkList())

        mockGeneralMethods()
    }

    private fun initViewModel() {
        viewModel = CategoriesViewModel(
            context,
            preferencesRepository,
            remoteCategoriesRepository,
            localCategoriesRepository
        )
    }

    private fun TestScope.setupUiStateCollecting() {
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
            viewModel.uiState.collect {}
        }
    }

    @Test
    fun categoriesViewModel_checkStateWithInternet() = runTest {
        mockDataWithInternet()

        initViewModel()
        setupUiStateCollecting()
        advanceUntilIdle()

        assertEquals(RequestStatus.SUCCESS, categoriesUiState.dataRequestStatus)

        verify(exactly = 1) { localCategoriesRepository.getCategories() }
        coVerify(exactly = 1) { remoteCategoriesRepository.getCategories() }
        coVerify(exactly = 1) { localCategoriesRepository.insertCategories(any()) }
        confirmVerified(localCategoriesRepository, remoteCategoriesRepository)
    }

    @Test
    fun categoriesViewModel_checkStateWithEmptyData() = runTest {
        mockEmptyData()

        initViewModel()
        setupUiStateCollecting()
        advanceUntilIdle()

        assertEquals(RequestStatus.SUCCESS, categoriesUiState.dataRequestStatus)

        verify(exactly = 1) { localCategoriesRepository.getCategories() }
        coVerify(exactly = 1) { remoteCategoriesRepository.getCategories() }
        confirmVerified(localCategoriesRepository, remoteCategoriesRepository)
    }

    @Test
    fun categoriesViewModel_checkStateWithoutInternet() = runTest {
        mockDataWithoutInternet()

        initViewModel()
        setupUiStateCollecting()
        advanceUntilIdle()

        assertEquals(RequestStatus.ERROR(), categoriesUiState.dataRequestStatus)

        verify(exactly = 1) { localCategoriesRepository.getCategories() }
        coVerify(exactly = 1) { remoteCategoriesRepository.getCategories() }
        confirmVerified(localCategoriesRepository, remoteCategoriesRepository)
    }

    @Test
    fun categoriesViewModel_checkRefreshedSuccessStateWithoutInternet() = runTest {
        mockDataWithInternet()

        initViewModel()
        setupUiStateCollecting()
        advanceUntilIdle()

        mockDataWithoutInternet()

        viewModel.getCategories()
        advanceUntilIdle()

        assertEquals(RequestStatus.ERROR(), categoriesUiState.dataRequestStatus)

        verify(exactly = 1) { localCategoriesRepository.getCategories() }
        coVerify(exactly = 2) { remoteCategoriesRepository.getCategories() }
        coVerify(exactly = 1) { localCategoriesRepository.insertCategories(any()) }
        confirmVerified(localCategoriesRepository, remoteCategoriesRepository)
    }

    @Test
    fun categoriesViewModel_checkRefreshedErrorStateWithInternet() = runTest {
        mockDataWithoutInternet()

        initViewModel()
        setupUiStateCollecting()
        advanceUntilIdle()

        mockDataWithInternet()

        viewModel.getCategories()
        advanceUntilIdle()

        assertEquals(RequestStatus.SUCCESS, categoriesUiState.dataRequestStatus)

        verify(exactly = 1) { localCategoriesRepository.getCategories() }
        coVerify(exactly = 2) { remoteCategoriesRepository.getCategories() }
        coVerify(exactly = 1) { localCategoriesRepository.insertCategories(any()) }
        confirmVerified(localCategoriesRepository, remoteCategoriesRepository)
    }

    @Test
    fun categoriesViewModel_checkRefreshedSuccessStateWithInternet() = runTest {
        mockDataWithInternet()

        initViewModel()
        setupUiStateCollecting()
        advanceUntilIdle()

        viewModel.getCategories()
        advanceUntilIdle()

        assertEquals(RequestStatus.SUCCESS, categoriesUiState.dataRequestStatus)

        verify(exactly = 1) { localCategoriesRepository.getCategories() }
        coVerify(exactly = 2) { remoteCategoriesRepository.getCategories() }
        coVerify(exactly = 2) { localCategoriesRepository.insertCategories(any()) }
        confirmVerified(localCategoriesRepository, remoteCategoriesRepository)
    }

    @Test
    fun categoriesViewModel_checkRefreshedErrorStateWithoutInternet() = runTest {
        mockDataWithoutInternet()

        initViewModel()
        setupUiStateCollecting()
        advanceUntilIdle()

        viewModel.getCategories()
        advanceUntilIdle()

        assertEquals(RequestStatus.ERROR(), categoriesUiState.dataRequestStatus)

        verify(exactly = 1) { localCategoriesRepository.getCategories() }
        coVerify(exactly = 2) { remoteCategoriesRepository.getCategories() }
        confirmVerified(localCategoriesRepository, remoteCategoriesRepository)
    }

    private fun mockGeneralMethods() {
        every { localCategoriesRepository.getCategories() } returns categoriesFlow
        every { preferencesRepository.isOfflineMode } returns isOfflineModeFlow
    }

    private fun mockDataWithInternet() {
        coEvery { remoteCategoriesRepository.getCategories() } returns Response.success(mockkList())
        coEvery { localCategoriesRepository.insertCategories(any()) } just Runs
    }

    private fun mockEmptyData() {
        coEvery { remoteCategoriesRepository.getCategories() } returns Response.success(null)
    }

    private fun mockDataWithoutInternet() {
        coEvery { remoteCategoriesRepository.getCategories() } throws IOException()
    }
}