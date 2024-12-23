package xelagurd.socialdating

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
import xelagurd.socialdating.data.fake.FakeDataSource
import xelagurd.socialdating.data.local.repository.LocalCategoriesRepository
import xelagurd.socialdating.data.model.Category
import xelagurd.socialdating.data.network.repository.RemoteCategoriesRepository
import xelagurd.socialdating.ui.state.InternetStatus
import xelagurd.socialdating.ui.viewmodel.CategoriesViewModel

@OptIn(ExperimentalCoroutinesApi::class)
class CategoriesViewModelTest {
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val remoteRepository: RemoteCategoriesRepository = mockk()
    private val localRepository: LocalCategoriesRepository = mockk()

    private lateinit var viewModel: CategoriesViewModel
    private lateinit var state: MutableStateFlow<List<Category>>

    private val localCategories = listOf(Category(1, "Database Category"))
    private val remoteCategories = listOf(Category(2, "Remote Category"))

    @Before
    fun setUp() {
        state = MutableStateFlow(localCategories)
        every { localRepository.getCategories() } returns state

        viewModel = CategoriesViewModel(remoteRepository, localRepository)

        assertEquals(InternetStatus.LOADING, viewModel.internetStatus)
    }

    @Test
    fun categoriesViewModel_GetDataWithInternet_OnlineStatus() = runTest {
        assertEquals(localCategories, localRepository.getCategories().first())

        coEvery { remoteRepository.getCategories() } returns remoteCategories
        coEvery { localRepository.insertCategories(remoteCategories) } answers {
            state.value += remoteCategories
        }

        advanceUntilIdle()

        assertEquals(InternetStatus.ONLINE, viewModel.internetStatus)
        assertEquals(
            localCategories + remoteCategories,
            localRepository.getCategories().first()
        )
    }

    @Test
    fun categoriesViewModel_GetDataWithoutInternet_OfflineStatus() = runTest {
        assertEquals(localCategories, localRepository.getCategories().first())

        coEvery { remoteRepository.getCategories() } throws IOException()
        coEvery { localRepository.insertCategories(FakeDataSource.categories) } answers {
            state.value += FakeDataSource.categories
        }

        advanceUntilIdle()

        assertEquals(InternetStatus.OFFLINE, viewModel.internetStatus)
        assertEquals(
            localCategories + FakeDataSource.categories,
            localRepository.getCategories().first()
        )
    }

    @Test
    fun categoriesViewModel_RefreshDataWithoutInternet_OfflineStatus() = runTest {
        assertEquals(localCategories, localRepository.getCategories().first())

        coEvery { remoteRepository.getCategories() } returns remoteCategories
        coEvery { localRepository.insertCategories(remoteCategories) } answers {
            state.value += remoteCategories
        }

        advanceUntilIdle()

        assertEquals(InternetStatus.ONLINE, viewModel.internetStatus)
        assertEquals(localCategories + remoteCategories, localRepository.getCategories().first())

        coEvery { remoteRepository.getCategories() } throws IOException()
        coEvery { localRepository.insertCategories(FakeDataSource.categories) } answers {
            state.value += FakeDataSource.categories
        }

        viewModel.getCategories()

        advanceUntilIdle()

        assertEquals(InternetStatus.OFFLINE, viewModel.internetStatus)
        assertEquals(
            localCategories + remoteCategories + FakeDataSource.categories,
            localRepository.getCategories().first()
        )
    }

    @Test
    fun categoriesViewModel_RefreshDataWithInternet_OnlineStatus() = runTest {
        assertEquals(localCategories, localRepository.getCategories().first())

        coEvery { remoteRepository.getCategories() } throws IOException()
        coEvery { localRepository.insertCategories(FakeDataSource.categories) } answers {
            state.value += FakeDataSource.categories
        }

        advanceUntilIdle()

        assertEquals(InternetStatus.OFFLINE, viewModel.internetStatus)
        assertEquals(
            localCategories + FakeDataSource.categories,
            localRepository.getCategories().first()
        )

        coEvery { remoteRepository.getCategories() } returns remoteCategories
        coEvery { localRepository.insertCategories(remoteCategories) } answers {
            state.value += remoteCategories
        }

        viewModel.getCategories()

        advanceUntilIdle()

        assertEquals(InternetStatus.ONLINE, viewModel.internetStatus)
        assertEquals(
            localCategories + FakeDataSource.categories + remoteCategories,
            localRepository.getCategories().first()
        )
    }
}