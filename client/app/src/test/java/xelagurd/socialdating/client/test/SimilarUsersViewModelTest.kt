package xelagurd.socialdating.client.test

import java.io.IOException
import kotlin.random.Random
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import android.content.Context
import androidx.lifecycle.SavedStateHandle
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.confirmVerified
import io.mockk.every
import io.mockk.mockk
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertFalse
import junit.framework.TestCase.assertTrue
import org.junit.Rule
import org.junit.Test
import retrofit2.Response
import xelagurd.socialdating.client.MainDispatcherRule
import xelagurd.socialdating.client.data.PreferencesRepository
import xelagurd.socialdating.client.data.fake.FakeData
import xelagurd.socialdating.client.data.model.dto.PageDto
import xelagurd.socialdating.client.data.remote.repository.RemoteUserCategoriesRepository
import xelagurd.socialdating.client.ui.navigation.SimilarUsersDestination
import xelagurd.socialdating.client.ui.state.RequestStatus
import xelagurd.socialdating.client.ui.viewmodel.SimilarUsersViewModel

@OptIn(ExperimentalCoroutinesApi::class)
class SimilarUsersViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val context = mockk<Context>(relaxed = true)
    private val savedStateHandle = mockk<SavedStateHandle>()
    private val preferencesRepository = mockk<PreferencesRepository>()
    private val remoteUserCategoriesRepository = mockk<RemoteUserCategoriesRepository>()

    private lateinit var viewModel: SimilarUsersViewModel
    private val similarUsersUiState
        get() = viewModel.uiState.value

    private val userId = Random.nextInt(1, Int.MAX_VALUE)
    private val isOfflineModeFlow = flowOf(false)

    private val similarUsers = FakeData.similarUsers
    private val nextCursor = "3:${Random.nextInt(1, Int.MAX_VALUE)}"
    private val nextPageSimilarUsers = similarUsers.map { it.copy(id = it.id + similarUsers.size) }

    private fun initViewModel() {
        mockGeneralMethods()

        viewModel = SimilarUsersViewModel(
            context,
            savedStateHandle,
            preferencesRepository,
            remoteUserCategoriesRepository
        )
    }

    private fun TestScope.setupUiStateCollecting() {
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
            viewModel.uiState.collect {}
        }
    }

    @Test
    fun similarUsersViewModel_withInternet_successStatusWithSimilarUsers() = runTest {
        mockDataWithInternet()

        initViewModel()
        setupUiStateCollecting()
        advanceUntilIdle()

        assertEquals(RequestStatus.SUCCESS, similarUsersUiState.dataRequestStatus)
        assertEquals(similarUsers, similarUsersUiState.entities)
        assertTrue(similarUsersUiState.isLastPage)

        coVerify(exactly = 1) { remoteUserCategoriesRepository.getSimilarUsers(any(), any(), null, any()) }
        confirmVerified(remoteUserCategoriesRepository)
    }

    @Test
    fun similarUsersViewModel_withEmptyRemoteSimilarUsers_successStatusWithoutSimilarUsers() = runTest {
        mockEmptySimilarUsers()

        initViewModel()
        setupUiStateCollecting()
        advanceUntilIdle()

        assertEquals(RequestStatus.SUCCESS, similarUsersUiState.dataRequestStatus)
        assertEquals(listOf<Nothing>(), similarUsersUiState.entities)
        assertTrue(similarUsersUiState.isLastPage)

        coVerify(exactly = 1) { remoteUserCategoriesRepository.getSimilarUsers(any(), any(), any(), any()) }
        confirmVerified(remoteUserCategoriesRepository)
    }

    @Test
    fun similarUsersViewModel_withoutInternet_errorStatus() = runTest {
        mockDataWithoutInternet()

        initViewModel()
        setupUiStateCollecting()
        advanceUntilIdle()

        assertEquals(RequestStatus.ERROR(), similarUsersUiState.dataRequestStatus)

        coVerify(exactly = 1) { remoteUserCategoriesRepository.getSimilarUsers(any(), any(), any(), any()) }
        confirmVerified(remoteUserCategoriesRepository)
    }

    @Test
    fun similarUsersViewModel_refreshWithInternetAfterError_successStatus() = runTest {
        mockDataWithoutInternet()

        initViewModel()
        setupUiStateCollecting()
        advanceUntilIdle()

        mockDataWithInternet()

        viewModel.getSimilarUsers()
        advanceUntilIdle()

        assertEquals(RequestStatus.SUCCESS, similarUsersUiState.dataRequestStatus)

        coVerify(exactly = 2) { remoteUserCategoriesRepository.getSimilarUsers(any(), any(), any(), any()) }
        confirmVerified(remoteUserCategoriesRepository)
    }

    @Test
    fun similarUsersViewModel_refreshWithoutInternetAfterSuccess_errorStatus() = runTest {
        mockDataWithInternet()

        initViewModel()
        setupUiStateCollecting()
        advanceUntilIdle()

        mockDataWithoutInternet()

        viewModel.getSimilarUsers()
        advanceUntilIdle()

        assertEquals(RequestStatus.ERROR(), similarUsersUiState.dataRequestStatus)

        coVerify(exactly = 2) { remoteUserCategoriesRepository.getSimilarUsers(any(), any(), any(), any()) }
        confirmVerified(remoteUserCategoriesRepository)
    }

    @Test
    fun similarUsersViewModel_nextPageWithInternet_addsPageToLoadedSimilarUsers() = runTest {
        mockDataWithInternet(firstPageNextCursor = nextCursor)

        initViewModel()
        setupUiStateCollecting()
        advanceUntilIdle()

        assertFalse(similarUsersUiState.isLastPage)

        viewModel.getNextSimilarUsers()
        advanceUntilIdle()

        assertEquals(RequestStatus.SUCCESS, similarUsersUiState.nextPageRequestStatus)
        assertEquals(similarUsers + nextPageSimilarUsers, similarUsersUiState.entities)
        assertTrue(similarUsersUiState.isLastPage)

        coVerify(exactly = 1) { remoteUserCategoriesRepository.getSimilarUsers(any(), any(), null, any()) }
        coVerify(exactly = 1) { remoteUserCategoriesRepository.getSimilarUsers(any(), any(), nextCursor, any()) }
        confirmVerified(remoteUserCategoriesRepository)
    }

    @Test
    fun similarUsersViewModel_nextPageWithAlreadyLoadedUser_keepsUniqueSimilarUsers() = runTest {
        mockDataWithInternet(firstPageNextCursor = nextCursor)
        coEvery { remoteUserCategoriesRepository.getSimilarUsers(any(), any(), nextCursor, any()) } returns
                Response.success(PageDto(listOf(similarUsers.last()) + nextPageSimilarUsers))

        initViewModel()
        setupUiStateCollecting()
        advanceUntilIdle()

        viewModel.getNextSimilarUsers()
        advanceUntilIdle()

        assertEquals(similarUsers + nextPageSimilarUsers, similarUsersUiState.entities)

        val loadedIds = similarUsersUiState.entities.map { it.id }
        assertEquals(loadedIds.distinct(), loadedIds)
    }

    @Test
    fun similarUsersViewModel_nextPageAfterLastPage_doesNothing() = runTest {
        mockDataWithInternet()

        initViewModel()
        setupUiStateCollecting()
        advanceUntilIdle()

        viewModel.getNextSimilarUsers()
        advanceUntilIdle()

        coVerify(exactly = 1) { remoteUserCategoriesRepository.getSimilarUsers(any(), any(), any(), any()) }
        confirmVerified(remoteUserCategoriesRepository)
    }

    @Test
    fun similarUsersViewModel_nextPageWithoutInternet_errorStatusOfNextPageOnly() = runTest {
        mockDataWithInternet(firstPageNextCursor = nextCursor)

        initViewModel()
        setupUiStateCollecting()
        advanceUntilIdle()

        coEvery { remoteUserCategoriesRepository.getSimilarUsers(any(), any(), any(), any()) } throws IOException()

        viewModel.getNextSimilarUsers()
        advanceUntilIdle()

        assertEquals(RequestStatus.SUCCESS, similarUsersUiState.dataRequestStatus)
        assertEquals(RequestStatus.ERROR(), similarUsersUiState.nextPageRequestStatus)
        assertEquals(similarUsers, similarUsersUiState.entities)
        assertFalse(similarUsersUiState.isLastPage)
    }

    @Test
    fun similarUsersViewModel_refreshAfterNextPage_replacesLoadedSimilarUsers() = runTest {
        mockDataWithInternet(firstPageNextCursor = nextCursor)

        initViewModel()
        setupUiStateCollecting()
        advanceUntilIdle()

        viewModel.getNextSimilarUsers()
        advanceUntilIdle()

        viewModel.getSimilarUsers()
        advanceUntilIdle()

        assertEquals(similarUsers, similarUsersUiState.entities)

        coVerify(exactly = 2) { remoteUserCategoriesRepository.getSimilarUsers(any(), any(), null, any()) }
        coVerify(exactly = 1) { remoteUserCategoriesRepository.getSimilarUsers(any(), any(), nextCursor, any()) }
        confirmVerified(remoteUserCategoriesRepository)
    }

    private fun mockGeneralMethods() {
        every { savedStateHandle.get<Int>(SimilarUsersDestination.userId) } returns userId
        every { preferencesRepository.isOfflineMode } returns isOfflineModeFlow
    }

    private fun mockDataWithInternet(firstPageNextCursor: String? = null) {
        coEvery { remoteUserCategoriesRepository.getSimilarUsers(any(), any(), null, any()) } returns
                Response.success(PageDto(similarUsers, firstPageNextCursor))
        coEvery {
            remoteUserCategoriesRepository.getSimilarUsers(any(), any(), firstPageNextCursor ?: "", any())
        } returns Response.success(PageDto(nextPageSimilarUsers))
    }

    private fun mockEmptySimilarUsers() {
        coEvery { remoteUserCategoriesRepository.getSimilarUsers(any(), any(), any(), any()) } returns
                Response.success(null)
    }

    private fun mockDataWithoutInternet() {
        coEvery { remoteUserCategoriesRepository.getSimilarUsers(any(), any(), any(), any()) } throws IOException()
    }
}
