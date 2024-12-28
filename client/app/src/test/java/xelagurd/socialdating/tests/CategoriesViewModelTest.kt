package xelagurd.socialdating.tests

import java.io.IOException
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
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
import xelagurd.socialdating.ui.state.InternetStatus
import xelagurd.socialdating.ui.viewmodel.CategoriesViewModel

@OptIn(ExperimentalCoroutinesApi::class)
class CategoriesViewModelTest {
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val remoteRepository: RemoteCategoriesRepository = mockk()
    private val localRepository: LocalCategoriesRepository = mockk()

    private lateinit var viewModel: CategoriesViewModel
    private lateinit var categoriesFlow: MutableStateFlow<List<Category>>

    private val localCategories = listOf(Category(1, ""))
    private val remoteCategories = listOf(Category(1, ""), Category(2, ""))

    @Before
    fun setup() {
        categoriesFlow = MutableStateFlow(localCategories)

        mockGeneralMethods()

        viewModel = CategoriesViewModel(remoteRepository, localRepository)
    }

    @Test
    fun categoriesViewModel_checkInitialState() = runTest {
        mockDataWithInternet()

        assertEquals(InternetStatus.LOADING, viewModel.internetStatus)
        assertEquals(localCategories, localRepository.getCategories().first())
    }

    @Test
    fun categoriesViewModel_checkStateWithInternet() = runTest {
        mockDataWithInternet()
        advanceUntilIdle()

        assertEquals(InternetStatus.ONLINE, viewModel.internetStatus)
        assertEquals(
            mergeListsAsSets(localCategories, remoteCategories),
            localRepository.getCategories().first()
        )
    }

    @Test
    fun categoriesViewModel_checkStateWithoutInternet() = runTest {
        mockDataWithoutInternet()
        advanceUntilIdle()

        assertEquals(InternetStatus.OFFLINE, viewModel.internetStatus)
        assertEquals(
            mergeListsAsSets(localCategories, FakeDataSource.categories),
            localRepository.getCategories().first()
        )
    }

    @Test
    fun categoriesViewModel_checkRefreshedOnlineStateWithoutInternet() = runTest {
        mockDataWithInternet()
        advanceUntilIdle()

        mockDataWithoutInternet()
        viewModel.getCategories()
        advanceUntilIdle()

        assertEquals(InternetStatus.OFFLINE, viewModel.internetStatus)
        assertEquals(
            mergeListsAsSets(localCategories, remoteCategories, FakeDataSource.categories),
            localRepository.getCategories().first()
        )
    }

    @Test
    fun categoriesViewModel_checkRefreshedOfflineStateWithInternet() = runTest {
        mockDataWithoutInternet()
        advanceUntilIdle()

        mockDataWithInternet()
        viewModel.getCategories()
        advanceUntilIdle()

        assertEquals(InternetStatus.ONLINE, viewModel.internetStatus)
        assertEquals(
            mergeListsAsSets(localCategories, FakeDataSource.categories, remoteCategories),
            localRepository.getCategories().first()
        )
    }

    @Test
    fun categoriesViewModel_checkRefreshedOnlineStateWithInternet() = runTest {
        mockDataWithInternet()
        advanceUntilIdle()

        viewModel.getCategories()
        advanceUntilIdle()

        assertEquals(InternetStatus.ONLINE, viewModel.internetStatus)
        assertEquals(
            mergeListsAsSets(localCategories, remoteCategories),
            localRepository.getCategories().first()
        )
    }

    @Test
    fun categoriesViewModel_checkRefreshedOfflineStateWithoutInternet() = runTest {
        mockDataWithoutInternet()
        advanceUntilIdle()

        viewModel.getCategories()
        advanceUntilIdle()

        assertEquals(InternetStatus.OFFLINE, viewModel.internetStatus)
        assertEquals(
            mergeListsAsSets(localCategories, FakeDataSource.categories),
            localRepository.getCategories().first()
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

    private fun mockDataWithoutInternet() {
        coEvery { remoteRepository.getCategories() } throws IOException()
        coEvery { localRepository.insertCategories(FakeDataSource.categories) } answers {
            categoriesFlow.value = mergeListsAsSets(categoriesFlow.value, FakeDataSource.categories)
        }
    }
}