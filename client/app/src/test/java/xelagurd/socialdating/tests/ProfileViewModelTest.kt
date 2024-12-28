package xelagurd.socialdating.tests

import java.io.IOException
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
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
import xelagurd.socialdating.data.fake.FakeDataSource
import xelagurd.socialdating.data.local.repository.LocalUsersRepository
import xelagurd.socialdating.data.model.User
import xelagurd.socialdating.data.network.repository.RemoteUsersRepository
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

    private val userId = 1

    private val localUser = User(userId, "", "", "", "", "", 30, "", "", 50)
    private val remoteUser = User(userId, "abc", "qwe", "", "", "", 40, "", "", 20)

    @Before
    fun setup() {
        usersFlow = MutableStateFlow(localUser)

        mockGeneralMethods()

        viewModel = ProfileViewModel(savedStateHandle, remoteRepository, localRepository)
    }

    @Test
    fun usersViewModel_checkInitialState() = runTest {
        mockDataWithInternet()

        assertEquals(InternetStatus.LOADING, viewModel.internetStatus)
        assertEquals(localUser, localRepository.getUser(userId).first())
    }

    @Test
    fun usersViewModel_checkStateWithInternet() = runTest {
        mockDataWithInternet()
        advanceUntilIdle()

        assertEquals(InternetStatus.ONLINE, viewModel.internetStatus)
        assertEquals(
            remoteUser,
            localRepository.getUser(userId).first()
        )
    }

    @Test
    fun usersViewModel_checkStateWithoutInternet() = runTest {
        mockDataWithoutInternet()
        advanceUntilIdle()

        assertEquals(InternetStatus.OFFLINE, viewModel.internetStatus)
        assertEquals(
            FakeDataSource.users[0],
            localRepository.getUser(userId).first()
        )
    }

    @Test
    fun usersViewModel_checkRefreshedOnlineStateWithoutInternet() = runTest {
        mockDataWithInternet()
        advanceUntilIdle()

        mockDataWithoutInternet()
        viewModel.getUser()
        advanceUntilIdle()

        assertEquals(InternetStatus.OFFLINE, viewModel.internetStatus)
        assertEquals(
            FakeDataSource.users[0],
            localRepository.getUser(userId).first()
        )
    }

    @Test
    fun usersViewModel_checkRefreshedOfflineStateWithInternet() = runTest {
        mockDataWithoutInternet()
        advanceUntilIdle()

        mockDataWithInternet()
        viewModel.getUser()
        advanceUntilIdle()

        assertEquals(InternetStatus.ONLINE, viewModel.internetStatus)
        assertEquals(
            remoteUser,
            localRepository.getUser(userId).first()
        )
    }

    @Test
    fun usersViewModel_checkRefreshedOnlineStateWithInternet() = runTest {
        mockDataWithInternet()
        advanceUntilIdle()

        viewModel.getUser()
        advanceUntilIdle()

        assertEquals(InternetStatus.ONLINE, viewModel.internetStatus)
        assertEquals(
            remoteUser,
            localRepository.getUser(userId).first()
        )
    }

    @Test
    fun usersViewModel_checkRefreshedOfflineStateWithoutInternet() = runTest {
        mockDataWithoutInternet()
        advanceUntilIdle()

        viewModel.getUser()
        advanceUntilIdle()

        assertEquals(InternetStatus.OFFLINE, viewModel.internetStatus)
        assertEquals(
            FakeDataSource.users[0],
            localRepository.getUser(userId).first()
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
        coEvery { localRepository.insertUser(FakeDataSource.users[0]) } answers {
            usersFlow.value = FakeDataSource.users[0]
        }
    }
}