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
import org.junit.Rule
import org.junit.Test
import retrofit2.Response
import xelagurd.socialdating.client.data.model.DefaultDataProperties.ID_MIN
import xelagurd.socialdating.client.MainDispatcherRule
import xelagurd.socialdating.client.data.PreferencesRepository
import xelagurd.socialdating.client.data.fake.FakeData
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

    private val userId = Random.nextInt(ID_MIN, Int.MAX_VALUE)
    private val isOfflineModeFlow = flowOf(false)

    private val similarUsers = FakeData.similarUsers

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

        coVerify(exactly = 1) { remoteUserCategoriesRepository.getSimilarUsers(any(), any()) }
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

        coVerify(exactly = 1) { remoteUserCategoriesRepository.getSimilarUsers(any(), any()) }
        confirmVerified(remoteUserCategoriesRepository)
    }

    @Test
    fun similarUsersViewModel_withoutInternet_errorStatus() = runTest {
        mockDataWithoutInternet()

        initViewModel()
        setupUiStateCollecting()
        advanceUntilIdle()

        assertEquals(RequestStatus.ERROR(), similarUsersUiState.dataRequestStatus)

        coVerify(exactly = 1) { remoteUserCategoriesRepository.getSimilarUsers(any(), any()) }
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

        coVerify(exactly = 2) { remoteUserCategoriesRepository.getSimilarUsers(any(), any()) }
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

        coVerify(exactly = 2) { remoteUserCategoriesRepository.getSimilarUsers(any(), any()) }
        confirmVerified(remoteUserCategoriesRepository)
    }

    private fun mockGeneralMethods() {
        every { savedStateHandle.get<Int>(SimilarUsersDestination.userId) } returns userId
        every { preferencesRepository.isOfflineMode } returns isOfflineModeFlow
    }

    private fun mockDataWithInternet() {
        coEvery { remoteUserCategoriesRepository.getSimilarUsers(any(), any()) } returns
                Response.success(similarUsers)
    }

    private fun mockEmptySimilarUsers() {
        coEvery { remoteUserCategoriesRepository.getSimilarUsers(any(), any()) } returns Response.success(null)
    }

    private fun mockDataWithoutInternet() {
        coEvery { remoteUserCategoriesRepository.getSimilarUsers(any(), any()) } throws IOException()
    }
}