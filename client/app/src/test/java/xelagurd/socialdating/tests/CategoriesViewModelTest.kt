package xelagurd.socialdating.tests

import java.io.IOException
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import android.content.Context
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import junit.framework.TestCase.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import xelagurd.socialdating.MainDispatcherRule
import xelagurd.socialdating.data.fake.FakeDataSource
import xelagurd.socialdating.data.local.repository.LocalCategoriesRepository
import xelagurd.socialdating.data.model.Category
import xelagurd.socialdating.data.remote.repository.RemoteCategoriesRepository
import xelagurd.socialdating.mergeListsAsSets
import xelagurd.socialdating.ui.state.RequestStatus
import xelagurd.socialdating.ui.viewmodel.CategoriesViewModel

@OptIn(ExperimentalCoroutinesApi::class)
class CategoriesViewModelTest {
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val context: Context = mockk()
    private val remoteRepository: RemoteCategoriesRepository = mockk()
    private val localRepository: LocalCategoriesRepository = mockk()

    private lateinit var viewModel: CategoriesViewModel
    private lateinit var categoriesFlow: MutableStateFlow<List<Category>>
    private val categoriesUiState
        get() = viewModel.uiState.value

    private val localCategories = listOf(Category(1, ""))
    private val remoteCategories = listOf(Category(1, ""), Category(2, ""))

    @Before
    fun setup() {
        categoriesFlow = MutableStateFlow(localCategories)

        mockGeneralMethods()
    }

    private fun TestScope.setupUiStateCollecting() {
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
            viewModel.uiState.collect {}
        }
    }

    @Test
    fun categoriesViewModel_checkStateWithInternet() = runTest {
        mockDataWithInternet()

        viewModel = CategoriesViewModel(
            context,
            remoteRepository,
            localRepository
        )
        setupUiStateCollecting()
        advanceUntilIdle()

        assertEquals(RequestStatus.SUCCESS, categoriesUiState.dataRequestStatus)
        assertEquals(
            mergeListsAsSets(localCategories, remoteCategories),
            categoriesUiState.entities
        )
    }

    @Test
    fun categoriesViewModel_checkStateWithEmptyData() = runTest {
        mockEmptyData()

        viewModel = CategoriesViewModel(
            context,
            remoteRepository,
            localRepository
        )
        setupUiStateCollecting()
        advanceUntilIdle()

        assertEquals(RequestStatus.FAILURE(), categoriesUiState.dataRequestStatus)
        assertEquals(
            localCategories,
            categoriesUiState.entities
        )
    }

    @Test
    fun categoriesViewModel_checkStateWithoutInternet() = runTest {
        mockDataWithoutInternet()

        viewModel = CategoriesViewModel(
            context,
            remoteRepository,
            localRepository
        )
        setupUiStateCollecting()
        advanceUntilIdle()

        assertEquals(RequestStatus.ERROR(), categoriesUiState.dataRequestStatus)
        assertEquals(
            localCategories,
            categoriesUiState.entities
        )
    }

    @Test
    fun categoriesViewModel_checkRefreshedSuccessStateWithoutInternet() = runTest {
        mockDataWithInternet()

        viewModel = CategoriesViewModel(
            context,
            remoteRepository,
            localRepository
        )
        setupUiStateCollecting()
        advanceUntilIdle()

        mockDataWithoutInternet()
        viewModel.getCategories()
        advanceUntilIdle()

        assertEquals(RequestStatus.ERROR(), categoriesUiState.dataRequestStatus)
        assertEquals(
            mergeListsAsSets(localCategories, remoteCategories),
            categoriesUiState.entities
        )
    }

    @Test
    fun categoriesViewModel_checkRefreshedErrorStateWithInternet() = runTest {
        mockDataWithoutInternet()

        viewModel = CategoriesViewModel(
            context,
            remoteRepository,
            localRepository
        )
        setupUiStateCollecting()
        advanceUntilIdle()

        mockDataWithInternet()
        viewModel.getCategories()
        advanceUntilIdle()

        assertEquals(RequestStatus.SUCCESS, categoriesUiState.dataRequestStatus)
        assertEquals(
            mergeListsAsSets(localCategories, remoteCategories),
            categoriesUiState.entities
        )
    }

    @Test
    fun categoriesViewModel_checkRefreshedSuccessStateWithInternet() = runTest {
        mockDataWithInternet()

        viewModel = CategoriesViewModel(
            context,
            remoteRepository,
            localRepository
        )
        setupUiStateCollecting()
        advanceUntilIdle()

        viewModel.getCategories()
        advanceUntilIdle()

        assertEquals(RequestStatus.SUCCESS, categoriesUiState.dataRequestStatus)
        assertEquals(
            mergeListsAsSets(localCategories, remoteCategories),
            categoriesUiState.entities
        )
    }

    @Test
    fun categoriesViewModel_checkRefreshedErrorStateWithoutInternet() = runTest {
        mockDataWithoutInternet()

        viewModel = CategoriesViewModel(
            context,
            remoteRepository,
            localRepository
        )
        setupUiStateCollecting()
        advanceUntilIdle()

        viewModel.getCategories()
        advanceUntilIdle()

        assertEquals(RequestStatus.ERROR(), categoriesUiState.dataRequestStatus)
        assertEquals(
            localCategories,
            categoriesUiState.entities
        )
    }

    private fun mockGeneralMethods() {
        every { localRepository.getCategories() } returns categoriesFlow
    }

    private fun mockDataWithInternet() {
        coEvery { remoteRepository.getCategories() } returns remoteCategories
        coEvery { localRepository.insertCategories(remoteCategories) } answers {
            categoriesFlow.value = mergeListsAsSets(categoriesFlow.value, remoteCategories)
        }
    }

    private fun mockEmptyData() {
        every { context.getString(any()) } returns ""
        coEvery { remoteRepository.getCategories() } returns emptyList()
    }

    private fun mockDataWithoutInternet() {
        every { context.getString(any()) } returns ""
        coEvery { remoteRepository.getCategories() } throws IOException()
        coEvery { localRepository.insertCategories(FakeDataSource.categories) } answers {
            categoriesFlow.value = mergeListsAsSets(categoriesFlow.value, FakeDataSource.categories)
        }
    }
}