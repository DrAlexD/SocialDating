package xelagurd.socialdating.client.test

import java.io.IOException
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import android.content.Context
import io.mockk.Runs
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.confirmVerified
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import junit.framework.TestCase.assertEquals
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import retrofit2.Response
import xelagurd.socialdating.client.MainDispatcherRule
import xelagurd.socialdating.client.TestUtils.mockkList
import xelagurd.socialdating.client.data.AccountManager
import xelagurd.socialdating.client.data.PreferencesRepository
import xelagurd.socialdating.client.data.local.repository.LocalUsersRepository
import xelagurd.socialdating.client.data.remote.ApiUtils.BAD_REQUEST
import xelagurd.socialdating.client.data.remote.repository.RemoteUsersRepository
import xelagurd.socialdating.client.ui.state.RequestStatus
import xelagurd.socialdating.client.ui.viewmodel.LoginViewModel

@OptIn(ExperimentalCoroutinesApi::class)
class LoginViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val context = mockk<Context>(relaxed = true)
    private val preferencesRepository = mockk<PreferencesRepository>(relaxed = true)
    private val accountManager = mockk<AccountManager>()
    private val remoteRepository = mockk<RemoteUsersRepository>()
    private val localRepository = mockk<LocalUsersRepository>()

    private lateinit var viewModel: LoginViewModel
    private val loginUiState
        get() = viewModel.uiState.value

    @Before
    fun setup() {
        mockFindCredentialsWithError()
    }

    private fun initViewModel() {
        viewModel = LoginViewModel(
            context,
            remoteRepository,
            localRepository,
            preferencesRepository,
            accountManager
        )
        viewModel.loginWithInput()
    }

    @Test
    fun loginViewModel_loginWithInternet() = runTest {
        mockDataWithInternet()

        initViewModel()
        advanceUntilIdle()

        assertEquals(RequestStatus.SUCCESS, loginUiState.actionRequestStatus)

        coVerify(exactly = 1) { accountManager.findCredentials() }
        coVerify(exactly = 1) { remoteRepository.loginUser(any()) }
        coVerify(exactly = 1) { accountManager.saveCredentials(any()) }
        coVerify(exactly = 1) { localRepository.insertUser(any()) }
        coVerify(exactly = 1) { preferencesRepository.saveAccessToken(any()) }
        coVerify(exactly = 1) { preferencesRepository.saveRefreshToken(any()) }
        coVerify(exactly = 1) { preferencesRepository.saveCurrentUserId(any()) }
        confirmVerified(preferencesRepository, localRepository, remoteRepository, accountManager)
    }

    @Test
    fun loginViewModel_loginWithoutInternet() = runTest {
        mockDataWithoutInternet()

        initViewModel()
        advanceUntilIdle()

        // FixMe: Change to ERROR after implementing server
        assertEquals(RequestStatus.SUCCESS, loginUiState.actionRequestStatus)

        coVerify(exactly = 1) { accountManager.findCredentials() }
        coVerify(exactly = 1) { remoteRepository.loginUser(any()) }
        coVerify(exactly = 1) { accountManager.saveCredentials(any()) } // FixMe: remove after adding server hosting
        coVerify(exactly = 1) { localRepository.getUsers() } // FixMe: remove after adding server hosting
        coVerify(exactly = 1) { localRepository.insertUser(any()) } // FixMe: remove after adding server hosting
        coVerify(exactly = 1) { preferencesRepository.saveCurrentUserId(any()) } // FixMe: remove after adding server hosting
        confirmVerified(preferencesRepository, localRepository, remoteRepository, accountManager)
    }

    @Test
    fun loginViewModel_loginWithWrongData() = runTest {
        mockWrongData()

        initViewModel()
        advanceUntilIdle()

        assertEquals(RequestStatus.FAILURE(BAD_REQUEST.toString()), loginUiState.actionRequestStatus)

        coVerify(exactly = 1) { accountManager.findCredentials() }
        coVerify(exactly = 1) { remoteRepository.loginUser(any()) }
        confirmVerified(preferencesRepository, localRepository, remoteRepository, accountManager)
    }

    @Test
    fun loginViewModel_retryLoginWithInternet() = runTest {
        mockDataWithoutInternet()

        initViewModel()
        advanceUntilIdle()

        mockDataWithInternet()

        viewModel.loginWithInput()
        advanceUntilIdle()

        assertEquals(RequestStatus.SUCCESS, loginUiState.actionRequestStatus)

        coVerify(exactly = 1) { accountManager.findCredentials() }
        coVerify(exactly = 2) { remoteRepository.loginUser(any()) }
        coVerify(exactly = 2) { accountManager.saveCredentials(any()) } // FixMe: set to 1 after adding server hosting
        coVerify(exactly = 1) { localRepository.getUsers() } // FixMe: remove after adding server hosting
        coVerify(exactly = 2) { localRepository.insertUser(any()) } // FixMe: set to 1 after adding server hosting
        coVerify(exactly = 1) { preferencesRepository.saveAccessToken(any()) }
        coVerify(exactly = 1) { preferencesRepository.saveRefreshToken(any()) }
        coVerify(exactly = 2) { preferencesRepository.saveCurrentUserId(any()) } // FixMe: set to 1 after adding server hosting
        confirmVerified(preferencesRepository, localRepository, remoteRepository, accountManager)
    }

    @Test
    fun loginViewModel_retryLoginWithRightData() = runTest {
        mockWrongData()

        initViewModel()
        advanceUntilIdle()

        mockDataWithInternet()

        viewModel.loginWithInput()
        advanceUntilIdle()

        assertEquals(RequestStatus.SUCCESS, loginUiState.actionRequestStatus)

        coVerify(exactly = 1) { accountManager.findCredentials() }
        coVerify(exactly = 2) { remoteRepository.loginUser(any()) }
        coVerify(exactly = 1) { accountManager.saveCredentials(any()) }
        coVerify(exactly = 1) { localRepository.insertUser(any()) }
        coVerify(exactly = 1) { preferencesRepository.saveAccessToken(any()) }
        coVerify(exactly = 1) { preferencesRepository.saveRefreshToken(any()) }
        coVerify(exactly = 1) { preferencesRepository.saveCurrentUserId(any()) }
        confirmVerified(preferencesRepository, localRepository, remoteRepository, accountManager)
    }

    private fun mockFindCredentialsWithError() {
        coEvery { accountManager.findCredentials() } returns null
    }

    private fun mockDataWithInternet() {
        coEvery { remoteRepository.loginUser(any()) } returns Response.success(mockk(relaxed = true))
        coEvery { accountManager.saveCredentials(any()) } just Runs
        coEvery { localRepository.insertUser(any()) } just Runs
    }

    private fun mockWrongData() {
        coEvery { remoteRepository.loginUser(any()) } returns
                Response.error(BAD_REQUEST, BAD_REQUEST.toString().toResponseBody())
    }

    private fun mockDataWithoutInternet() {
        coEvery { remoteRepository.loginUser(any()) } throws IOException()

        // FixMe: remove after adding server hosting
        coEvery { accountManager.saveCredentials(any()) } just Runs
        every { localRepository.getUsers() } returns flowOf(mockkList(relaxed = true))
        coEvery { localRepository.insertUser(any()) } just Runs
    }
}