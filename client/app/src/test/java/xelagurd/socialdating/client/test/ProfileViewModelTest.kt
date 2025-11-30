package xelagurd.socialdating.client.test

import java.io.IOException
import kotlin.random.Random
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
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
    private val remoteRepository = mockk<RemoteUsersRepository>()
    private val localRepository = mockk<LocalUsersRepository>()

    private lateinit var viewModel: ProfileViewModel
    private lateinit var usersFlow: MutableStateFlow<User>
    private val profileUiState
        get() = viewModel.uiState.value

    private val userId = Random.nextInt()
    private val anotherUserId = userId

    @Before
    fun setup() {
        usersFlow = MutableStateFlow(mockk())

        mockGeneralMethods()
    }

    private fun initViewModel() {
        viewModel = ProfileViewModel(
            context,
            savedStateHandle,
            remoteRepository,
            localRepository
        )
    }

    private fun TestScope.setupUiStateCollecting() {
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
            viewModel.uiState.collect {}
        }
    }

    @Test
    fun profileViewModel_checkStateWithInternet() = runTest {
        mockDataWithInternet()

        initViewModel()
        setupUiStateCollecting()
        advanceUntilIdle()

        assertEquals(RequestStatus.SUCCESS, profileUiState.dataRequestStatus)

        verify(exactly = 1) { localRepository.getUser(any()) }
        coVerify(exactly = 1) { remoteRepository.getUser(any()) }
        coVerify(exactly = 1) { localRepository.insertUser(any()) }
        confirmVerified(localRepository, remoteRepository)
    }

    @Test
    fun profileViewModel_checkStateWithEmptyData() = runTest {
        mockEmptyData()

        initViewModel()
        setupUiStateCollecting()
        advanceUntilIdle()

        assertEquals(RequestStatus.SUCCESS, profileUiState.dataRequestStatus)

        verify(exactly = 1) { localRepository.getUser(any()) }
        coVerify(exactly = 1) { remoteRepository.getUser(any()) }
        confirmVerified(localRepository, remoteRepository)
    }

    @Test
    fun profileViewModel_checkStateWithoutInternet() = runTest {
        mockDataWithoutInternet()

        initViewModel()
        setupUiStateCollecting()
        advanceUntilIdle()

        assertEquals(RequestStatus.ERROR(), profileUiState.dataRequestStatus)

        verify(exactly = 1) { localRepository.getUser(any()) }
        coVerify(exactly = 1) { remoteRepository.getUser(any()) }
        confirmVerified(localRepository, remoteRepository)
    }

    @Test
    fun profileViewModel_checkRefreshedSuccessStateWithoutInternet() = runTest {
        mockDataWithInternet()

        initViewModel()
        setupUiStateCollecting()
        advanceUntilIdle()

        mockDataWithoutInternet()

        viewModel.getUser()
        advanceUntilIdle()

        assertEquals(RequestStatus.ERROR(), profileUiState.dataRequestStatus)

        verify(exactly = 1) { localRepository.getUser(any()) }
        coVerify(exactly = 2) { remoteRepository.getUser(any()) }
        coVerify(exactly = 1) { localRepository.insertUser(any()) }
        confirmVerified(localRepository, remoteRepository)
    }

    @Test
    fun profileViewModel_checkRefreshedErrorStateWithInternet() = runTest {
        mockDataWithoutInternet()

        initViewModel()
        setupUiStateCollecting()
        advanceUntilIdle()

        mockDataWithInternet()

        viewModel.getUser()
        advanceUntilIdle()

        assertEquals(RequestStatus.SUCCESS, profileUiState.dataRequestStatus)

        verify(exactly = 1) { localRepository.getUser(any()) }
        coVerify(exactly = 2) { remoteRepository.getUser(any()) }
        coVerify(exactly = 1) { localRepository.insertUser(any()) }
        confirmVerified(localRepository, remoteRepository)
    }

    @Test
    fun profileViewModel_checkRefreshedSuccessStateWithInternet() = runTest {
        mockDataWithInternet()

        initViewModel()
        setupUiStateCollecting()
        advanceUntilIdle()

        viewModel.getUser()
        advanceUntilIdle()

        assertEquals(RequestStatus.SUCCESS, profileUiState.dataRequestStatus)

        verify(exactly = 1) { localRepository.getUser(any()) }
        coVerify(exactly = 2) { remoteRepository.getUser(any()) }
        coVerify(exactly = 2) { localRepository.insertUser(any()) }
        confirmVerified(localRepository, remoteRepository)
    }

    @Test
    fun profileViewModel_checkRefreshedErrorStateWithoutInternet() = runTest {
        mockDataWithoutInternet()

        initViewModel()
        setupUiStateCollecting()
        advanceUntilIdle()

        viewModel.getUser()
        advanceUntilIdle()

        assertEquals(RequestStatus.ERROR(), profileUiState.dataRequestStatus)

        verify(exactly = 1) { localRepository.getUser(any()) }
        coVerify(exactly = 2) { remoteRepository.getUser(any()) }
        confirmVerified(localRepository, remoteRepository)
    }

    private fun mockGeneralMethods() {
        every { savedStateHandle.get<Int>(ProfileDestination.userId) } returns userId
        every { savedStateHandle.get<Int>(ProfileDestination.anotherUserId) } returns anotherUserId
        every { localRepository.getUser(any()) } returns usersFlow
    }

    private fun mockDataWithInternet() {
        coEvery { remoteRepository.getUser(any()) } returns Response.success(mockk())
        coEvery { localRepository.insertUser(any()) } just Runs
    }

    private fun mockEmptyData() {
        coEvery { remoteRepository.getUser(any()) } returns Response.success(null)
    }

    private fun mockDataWithoutInternet() {
        coEvery { remoteRepository.getUser(any()) } throws IOException()
    }
}