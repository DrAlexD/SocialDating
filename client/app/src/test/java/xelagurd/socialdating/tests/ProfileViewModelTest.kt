package xelagurd.socialdating.tests

import java.io.IOException
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import androidx.lifecycle.SavedStateHandle
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import junit.framework.TestCase.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import xelagurd.socialdating.MainDispatcherRule
import xelagurd.socialdating.data.local.repository.LocalUsersRepository
import xelagurd.socialdating.data.model.User
import xelagurd.socialdating.data.model.enums.Gender
import xelagurd.socialdating.data.model.enums.Purpose
import xelagurd.socialdating.data.remote.repository.RemoteUsersRepository
import xelagurd.socialdating.ui.state.InternetStatus
import xelagurd.socialdating.ui.viewmodel.ProfileViewModel

@OptIn(ExperimentalCoroutinesApi::class)
class ProfileViewModelTest {
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val savedStateHandle: SavedStateHandle = mockk()
    private val remoteRepository: RemoteUsersRepository = mockk()
    private val localRepository: LocalUsersRepository = mockk()

    private lateinit var viewModel: ProfileViewModel
    private lateinit var usersFlow: MutableStateFlow<User>
    private val profileUiState
        get() = viewModel.uiState.value

    private val userId = 1

    private val localUser =
        User(userId, "", Gender.MALE, "", "", "", 30, "", Purpose.ALL_AT_ONCE, 50)
    private val remoteUser =
        User(userId, "abc", Gender.FEMALE, "", "", "", 40, "", Purpose.RELATIONSHIPS, 20)

    @Before
    fun setup() {
        usersFlow = MutableStateFlow(localUser)

        mockGeneralMethods()

        viewModel = ProfileViewModel(savedStateHandle, remoteRepository, localRepository)
    }

    private fun TestScope.setupUiStateCollecting() {
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
            viewModel.uiState.collect {}
        }
    }

    @Test
    fun usersViewModel_checkStateWithInternet() = runTest {
        setupUiStateCollecting()

        mockDataWithInternet()
        advanceUntilIdle()

        assertEquals(InternetStatus.ONLINE, profileUiState.internetStatus)
        assertEquals(
            remoteUser,
            profileUiState.user
        )
    }

    @Test
    fun usersViewModel_checkStateWithoutInternet() = runTest {
        setupUiStateCollecting()

        mockDataWithoutInternet()
        advanceUntilIdle()

        assertEquals(InternetStatus.OFFLINE, profileUiState.internetStatus)
        assertEquals(
            localUser,
            profileUiState.user
        )
    }

    @Test
    fun usersViewModel_checkRefreshedOnlineStateWithoutInternet() = runTest {
        setupUiStateCollecting()

        mockDataWithInternet()
        advanceUntilIdle()

        mockDataWithoutInternet()
        viewModel.getUser()
        advanceUntilIdle()

        assertEquals(InternetStatus.OFFLINE, profileUiState.internetStatus)
        assertEquals(
            remoteUser,
            profileUiState.user
        )
    }

    @Test
    fun usersViewModel_checkRefreshedOfflineStateWithInternet() = runTest {
        setupUiStateCollecting()

        mockDataWithoutInternet()
        advanceUntilIdle()

        mockDataWithInternet()
        viewModel.getUser()
        advanceUntilIdle()

        assertEquals(InternetStatus.ONLINE, profileUiState.internetStatus)
        assertEquals(
            remoteUser,
            profileUiState.user
        )
    }

    @Test
    fun usersViewModel_checkRefreshedOnlineStateWithInternet() = runTest {
        setupUiStateCollecting()

        mockDataWithInternet()
        advanceUntilIdle()

        viewModel.getUser()
        advanceUntilIdle()

        assertEquals(InternetStatus.ONLINE, profileUiState.internetStatus)
        assertEquals(
            remoteUser,
            profileUiState.user
        )
    }

    @Test
    fun usersViewModel_checkRefreshedOfflineStateWithoutInternet() = runTest {
        setupUiStateCollecting()

        mockDataWithoutInternet()
        advanceUntilIdle()

        viewModel.getUser()
        advanceUntilIdle()

        assertEquals(InternetStatus.OFFLINE, profileUiState.internetStatus)
        assertEquals(
            localUser,
            profileUiState.user
        )
    }

    private fun mockGeneralMethods() {
        every { savedStateHandle.get<Int>("userId") } returns userId
        every { localRepository.getUser(userId) } returns usersFlow
    }

    private fun mockDataWithInternet() {
        coEvery { remoteRepository.getUser(userId) } returns remoteUser
        coEvery { localRepository.insertUser(remoteUser) } answers {
            usersFlow.value = remoteUser
        }
    }

    private fun mockDataWithoutInternet() {
        coEvery { remoteRepository.getUser(userId) } throws IOException()
    }
}