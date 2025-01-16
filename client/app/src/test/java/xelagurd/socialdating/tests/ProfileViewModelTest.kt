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
import xelagurd.socialdating.ui.state.RequestStatus
import xelagurd.socialdating.ui.viewmodel.ProfileViewModel

@OptIn(ExperimentalCoroutinesApi::class)
class ProfileViewModelTest {
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val context: Context = mockk()
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
        User(userId, "", Gender.FEMALE, "", "", "", 40, "", Purpose.RELATIONSHIPS, 20)

    @Before
    fun setup() {
        usersFlow = MutableStateFlow(localUser)

        mockGeneralMethods()

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
        setupUiStateCollecting()

        mockDataWithInternet()
        advanceUntilIdle()

        assertEquals(RequestStatus.SUCCESS, profileUiState.dataRequestStatus)
        assertEquals(
            remoteUser,
            profileUiState.entity
        )
    }

    @Test
    fun profileViewModel_checkStateWithEmptyData() = runTest {
        setupUiStateCollecting()

        mockEmptyData()
        advanceUntilIdle()

        assertEquals(RequestStatus.FAILURE(), profileUiState.dataRequestStatus)
        assertEquals(
            localUser,
            profileUiState.entity
        )
    }

    @Test
    fun profileViewModel_checkStateWithoutInternet() = runTest {
        setupUiStateCollecting()

        mockDataWithoutInternet()
        advanceUntilIdle()

        assertEquals(RequestStatus.ERROR(), profileUiState.dataRequestStatus)
        assertEquals(
            localUser,
            profileUiState.entity
        )
    }

    @Test
    fun profileViewModel_checkRefreshedSuccessStateWithoutInternet() = runTest {
        setupUiStateCollecting()

        mockDataWithInternet()
        advanceUntilIdle()

        mockDataWithoutInternet()
        viewModel.getUser()
        advanceUntilIdle()

        assertEquals(RequestStatus.ERROR(), profileUiState.dataRequestStatus)
        assertEquals(
            remoteUser,
            profileUiState.entity
        )
    }

    @Test
    fun profileViewModel_checkRefreshedErrorStateWithInternet() = runTest {
        setupUiStateCollecting()

        mockDataWithoutInternet()
        advanceUntilIdle()

        mockDataWithInternet()
        viewModel.getUser()
        advanceUntilIdle()

        assertEquals(RequestStatus.SUCCESS, profileUiState.dataRequestStatus)
        assertEquals(
            remoteUser,
            profileUiState.entity
        )
    }

    @Test
    fun profileViewModel_checkRefreshedSuccessStateWithInternet() = runTest {
        setupUiStateCollecting()

        mockDataWithInternet()
        advanceUntilIdle()

        viewModel.getUser()
        advanceUntilIdle()

        assertEquals(RequestStatus.SUCCESS, profileUiState.dataRequestStatus)
        assertEquals(
            remoteUser,
            profileUiState.entity
        )
    }

    @Test
    fun profileViewModel_checkRefreshedErrorStateWithoutInternet() = runTest {
        setupUiStateCollecting()

        mockDataWithoutInternet()
        advanceUntilIdle()

        viewModel.getUser()
        advanceUntilIdle()

        assertEquals(RequestStatus.ERROR(), profileUiState.dataRequestStatus)
        assertEquals(
            localUser,
            profileUiState.entity
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

    private fun mockEmptyData() {
        every { context.getString(any()) } returns ""
        coEvery { remoteRepository.getUser(userId) } returns null
    }

    private fun mockDataWithoutInternet() {
        every { context.getString(any()) } returns ""
        coEvery { remoteRepository.getUser(userId) } throws IOException()
    }
}