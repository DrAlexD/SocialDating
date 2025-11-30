package xelagurd.socialdating.client.test

import java.io.IOException
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import android.content.Context
import io.mockk.Runs
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.confirmVerified
import io.mockk.just
import io.mockk.mockk
import junit.framework.TestCase.assertEquals
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import retrofit2.Response
import xelagurd.socialdating.client.MainDispatcherRule
import xelagurd.socialdating.client.data.AccountManager
import xelagurd.socialdating.client.data.PreferencesRepository
import xelagurd.socialdating.client.data.local.repository.CommonLocalRepository
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
    private val accountManager = mockk<AccountManager>()
    private val preferencesRepository = mockk<PreferencesRepository>(relaxed = true)
    private val commonLocalRepository = mockk<CommonLocalRepository>()
    private val remoteUsersRepository = mockk<RemoteUsersRepository>()
    private val localUsersRepository = mockk<LocalUsersRepository>()

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
            accountManager,
            preferencesRepository,
            commonLocalRepository,
            remoteUsersRepository,
            localUsersRepository
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
        coVerify(exactly = 1) { remoteUsersRepository.loginUser(any()) }
        coVerify(exactly = 1) { accountManager.saveCredentials(any()) }
        coVerify(exactly = 1) { localUsersRepository.insertUser(any()) }
        coVerify(exactly = 1) { preferencesRepository.saveAccessToken(any()) }
        coVerify(exactly = 1) { preferencesRepository.saveRefreshToken(any()) }
        coVerify(exactly = 1) { preferencesRepository.saveCurrentUserId(any()) }
        confirmVerified(preferencesRepository, localUsersRepository, remoteUsersRepository, accountManager)
    }

    @Test
    fun loginViewModel_loginWithoutInternet() = runTest {
        mockDataWithoutInternet()

        initViewModel()
        advanceUntilIdle()

        assertEquals(RequestStatus.ERROR(), loginUiState.actionRequestStatus)

        coVerify(exactly = 1) { accountManager.findCredentials() }
        coVerify(exactly = 1) { remoteUsersRepository.loginUser(any()) }
        confirmVerified(preferencesRepository, localUsersRepository, remoteUsersRepository, accountManager)
    }

    @Test
    fun loginViewModel_loginWithWrongData() = runTest {
        mockWrongData()

        initViewModel()
        advanceUntilIdle()

        assertEquals(RequestStatus.FAILURE(BAD_REQUEST.toString()), loginUiState.actionRequestStatus)

        coVerify(exactly = 1) { accountManager.findCredentials() }
        coVerify(exactly = 1) { remoteUsersRepository.loginUser(any()) }
        confirmVerified(preferencesRepository, localUsersRepository, remoteUsersRepository, accountManager)
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
        coVerify(exactly = 2) { remoteUsersRepository.loginUser(any()) }
        coVerify(exactly = 1) { accountManager.saveCredentials(any()) }
        coVerify(exactly = 1) { localUsersRepository.insertUser(any()) }
        coVerify(exactly = 1) { preferencesRepository.saveAccessToken(any()) }
        coVerify(exactly = 1) { preferencesRepository.saveRefreshToken(any()) }
        coVerify(exactly = 1) { preferencesRepository.saveCurrentUserId(any()) }
        confirmVerified(preferencesRepository, localUsersRepository, remoteUsersRepository, accountManager)
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
        coVerify(exactly = 2) { remoteUsersRepository.loginUser(any()) }
        coVerify(exactly = 1) { accountManager.saveCredentials(any()) }
        coVerify(exactly = 1) { localUsersRepository.insertUser(any()) }
        coVerify(exactly = 1) { preferencesRepository.saveAccessToken(any()) }
        coVerify(exactly = 1) { preferencesRepository.saveRefreshToken(any()) }
        coVerify(exactly = 1) { preferencesRepository.saveCurrentUserId(any()) }
        confirmVerified(preferencesRepository, localUsersRepository, remoteUsersRepository, accountManager)
    }

    private fun mockFindCredentialsWithError() {
        coEvery { accountManager.findCredentials() } returns null
    }

    private fun mockDataWithInternet() {
        coEvery { remoteUsersRepository.loginUser(any()) } returns Response.success(mockk(relaxed = true))
        coEvery { accountManager.saveCredentials(any()) } just Runs
        coEvery { localUsersRepository.insertUser(any()) } just Runs
    }

    private fun mockWrongData() {
        coEvery { remoteUsersRepository.loginUser(any()) } returns
                Response.error(BAD_REQUEST, BAD_REQUEST.toString().toResponseBody())
    }

    private fun mockDataWithoutInternet() {
        coEvery { remoteUsersRepository.loginUser(any()) } throws IOException()
    }
}