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
import xelagurd.socialdating.client.data.PreferencesRepository
import xelagurd.socialdating.client.data.fake.FakeData
import xelagurd.socialdating.client.data.local.repository.LocalUsersRepository
import xelagurd.socialdating.client.data.model.User
import xelagurd.socialdating.client.data.remote.repository.RemoteUsersRepository
import xelagurd.socialdating.client.ui.navigation.ProfileDestination
import xelagurd.socialdating.client.ui.state.RequestStatus
import xelagurd.socialdating.client.ui.viewmodel.ProfileViewModel

@OptIn(ExperimentalCoroutinesApi::class)
class ProfileViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val context = mockk<Context>(relaxed = true)
    private val savedStateHandle = mockk<SavedStateHandle>()
    private val preferencesRepository = mockk<PreferencesRepository>()
    private val remoteUsersRepository = mockk<RemoteUsersRepository>()
    private val localUsersRepository = mockk<LocalUsersRepository>()

    private lateinit var viewModel: ProfileViewModel
    private lateinit var usersFlow: MutableStateFlow<User>
    private val profileUiState
        get() = viewModel.uiState.value

    private val userId = Random.nextInt(1, Int.MAX_VALUE)
    private var anotherUserId = userId
    private val isOfflineModeFlow = flowOf(false)

    @Before
    fun setup() {
        usersFlow = MutableStateFlow(mockk())
    }

    private fun initViewModel() {
        mockGeneralMethods()

        viewModel = ProfileViewModel(
            context,
            savedStateHandle,
            preferencesRepository,
            remoteUsersRepository,
            localUsersRepository
        )
    }

    private fun TestScope.setupUiStateCollecting() {
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
            viewModel.uiState.collect {}
        }
    }

    @Test
    fun profileViewModel_withInternet_successStatus() = runTest {
        mockDataWithInternet()

        initViewModel()
        setupUiStateCollecting()
        advanceUntilIdle()

        assertEquals(RequestStatus.SUCCESS, profileUiState.dataRequestStatus)

        verify(exactly = 1) { localUsersRepository.getUser(any()) }
        coVerify(exactly = 1) { remoteUsersRepository.getUser(any()) }
        coVerify(exactly = 1) { localUsersRepository.insertUser(any()) }
        confirmVerified(localUsersRepository, remoteUsersRepository)
    }

    @Test
    fun profileViewModel_withEmptyRemoteUser_successStatus() = runTest {
        mockEmptyData()

        initViewModel()
        setupUiStateCollecting()
        advanceUntilIdle()

        assertEquals(RequestStatus.SUCCESS, profileUiState.dataRequestStatus)

        verify(exactly = 1) { localUsersRepository.getUser(any()) }
        coVerify(exactly = 1) { remoteUsersRepository.getUser(any()) }
        confirmVerified(localUsersRepository, remoteUsersRepository)
    }

    @Test
    fun profileViewModel_withoutInternet_errorStatus() = runTest {
        mockDataWithoutInternet()

        initViewModel()
        setupUiStateCollecting()
        advanceUntilIdle()

        assertEquals(RequestStatus.ERROR(), profileUiState.dataRequestStatus)

        verify(exactly = 1) { localUsersRepository.getUser(any()) }
        coVerify(exactly = 1) { remoteUsersRepository.getUser(any()) }
        confirmVerified(localUsersRepository, remoteUsersRepository)
    }

    @Test
    fun profileViewModel_refreshWithoutInternetAfterSuccess_errorStatus() = runTest {
        mockDataWithInternet()

        initViewModel()
        setupUiStateCollecting()
        advanceUntilIdle()

        mockDataWithoutInternet()

        viewModel.getUser()
        advanceUntilIdle()

        assertEquals(RequestStatus.ERROR(), profileUiState.dataRequestStatus)

        verify(exactly = 1) { localUsersRepository.getUser(any()) }
        coVerify(exactly = 2) { remoteUsersRepository.getUser(any()) }
        coVerify(exactly = 1) { localUsersRepository.insertUser(any()) }
        confirmVerified(localUsersRepository, remoteUsersRepository)
    }

    @Test
    fun profileViewModel_refreshWithInternetAfterError_successStatus() = runTest {
        mockDataWithoutInternet()

        initViewModel()
        setupUiStateCollecting()
        advanceUntilIdle()

        mockDataWithInternet()

        viewModel.getUser()
        advanceUntilIdle()

        assertEquals(RequestStatus.SUCCESS, profileUiState.dataRequestStatus)

        verify(exactly = 1) { localUsersRepository.getUser(any()) }
        coVerify(exactly = 2) { remoteUsersRepository.getUser(any()) }
        coVerify(exactly = 1) { localUsersRepository.insertUser(any()) }
        confirmVerified(localUsersRepository, remoteUsersRepository)
    }

    @Test
    fun profileViewModel_refreshWithInternetAfterSuccess_successStatus() = runTest {
        mockDataWithInternet()

        initViewModel()
        setupUiStateCollecting()
        advanceUntilIdle()

        viewModel.getUser()
        advanceUntilIdle()

        assertEquals(RequestStatus.SUCCESS, profileUiState.dataRequestStatus)

        verify(exactly = 1) { localUsersRepository.getUser(any()) }
        coVerify(exactly = 2) { remoteUsersRepository.getUser(any()) }
        coVerify(exactly = 2) { localUsersRepository.insertUser(any()) }
        confirmVerified(localUsersRepository, remoteUsersRepository)
    }

    @Test
    fun profileViewModel_refreshWithoutInternetAfterError_errorStatus() = runTest {
        mockDataWithoutInternet()

        initViewModel()
        setupUiStateCollecting()
        advanceUntilIdle()

        viewModel.getUser()
        advanceUntilIdle()

        assertEquals(RequestStatus.ERROR(), profileUiState.dataRequestStatus)

        verify(exactly = 1) { localUsersRepository.getUser(any()) }
        coVerify(exactly = 2) { remoteUsersRepository.getUser(any()) }
        confirmVerified(localUsersRepository, remoteUsersRepository)
    }

    @Test
    fun profileViewModel_anotherUserWithInternet_successStatusWithAnotherUser() = runTest {
        anotherUserId = userId + 1
        mockDataWithInternet()

        initViewModel()
        setupUiStateCollecting()
        advanceUntilIdle()

        assertEquals(RequestStatus.SUCCESS, profileUiState.dataRequestStatus)
        assertEquals(FakeData.users[1], profileUiState.entity)

        coVerify(exactly = 1) { remoteUsersRepository.getUser(any()) }
        confirmVerified(localUsersRepository, remoteUsersRepository)
    }

    private fun mockGeneralMethods() {
        every { savedStateHandle.get<Int>(ProfileDestination.userId) } returns userId
        every { savedStateHandle.get<Int>(ProfileDestination.anotherUserId) } returns anotherUserId
        every { preferencesRepository.isOfflineMode } returns isOfflineModeFlow
        every { localUsersRepository.getUser(any()) } returns usersFlow
    }

    private fun mockDataWithInternet() {
        coEvery { remoteUsersRepository.getUser(any()) } returns Response.success(FakeData.users[1])
        coEvery { localUsersRepository.insertUser(any()) } just Runs
    }

    private fun mockEmptyData() {
        coEvery { remoteUsersRepository.getUser(any()) } returns Response.success(null)
    }

    private fun mockDataWithoutInternet() {
        coEvery { remoteUsersRepository.getUser(any()) } throws IOException()
    }
}