package xelagurd.socialdating.client.test

import java.io.IOException
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import android.content.Context
import androidx.credentials.Credential
import androidx.credentials.GetCredentialResponse
import androidx.credentials.PasswordCredential
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
import xelagurd.socialdating.client.data.AccountManager
import xelagurd.socialdating.client.data.PreferencesRepository
import xelagurd.socialdating.client.data.fake.FakeData
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
    }

    private fun initViewModelAndLoginWithInput() {
        initViewModel()
        viewModel.updateUiState(FakeData.loginFormData)
        viewModel.loginWithInput()
    }

    @Test
    fun loginViewModel_loginWithInternet_successStatus() = runTest {
        mockDataWithInternet()

        initViewModelAndLoginWithInput()
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
    fun loginViewModel_loginWithoutInternet_errorStatus() = runTest {
        mockDataWithoutInternet()

        initViewModelAndLoginWithInput()
        advanceUntilIdle()

        assertEquals(RequestStatus.ERROR(), loginUiState.actionRequestStatus)

        coVerify(exactly = 1) { accountManager.findCredentials() }
        coVerify(exactly = 1) { remoteUsersRepository.loginUser(any()) }
        confirmVerified(preferencesRepository, localUsersRepository, remoteUsersRepository, accountManager)
    }

    @Test
    fun loginViewModel_loginWithWrongData_failureStatus() = runTest {
        mockWrongData()

        initViewModelAndLoginWithInput()
        advanceUntilIdle()

        assertEquals(RequestStatus.FAILURE(BAD_REQUEST.toString()), loginUiState.actionRequestStatus)

        coVerify(exactly = 1) { accountManager.findCredentials() }
        coVerify(exactly = 1) { remoteUsersRepository.loginUser(any()) }
        confirmVerified(preferencesRepository, localUsersRepository, remoteUsersRepository, accountManager)
    }

    @Test
    fun loginViewModel_retryLoginWithInternetAfterError_successStatus() = runTest {
        mockDataWithoutInternet()

        initViewModelAndLoginWithInput()
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
    fun loginViewModel_retryLoginWithRightDataAfterFailure_successStatus() = runTest {
        mockWrongData()

        initViewModelAndLoginWithInput()
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
    fun loginViewModel_foundCredentials_successStatus() = runTest {
        mockFindCredentials()
        mockDataWithInternet()

        initViewModel()
        advanceUntilIdle()

        assertEquals(RequestStatus.SUCCESS, loginUiState.actionRequestStatus)

        coVerify(exactly = 1) { accountManager.findCredentials() }
        coVerify(exactly = 1) { remoteUsersRepository.loginUser(any()) }
        coVerify(exactly = 1) { localUsersRepository.insertUser(any()) }
        coVerify(exactly = 1) { preferencesRepository.saveAccessToken(any()) }
        coVerify(exactly = 1) { preferencesRepository.saveRefreshToken(any()) }
        coVerify(exactly = 1) { preferencesRepository.saveCurrentUserId(any()) }
        confirmVerified(preferencesRepository, localUsersRepository, remoteUsersRepository, accountManager)
    }

    @Test
    fun loginViewModel_foundUnsupportedCredentials_undefinedStatus() = runTest {
        mockFindUnsupportedCredentials()

        initViewModel()
        advanceUntilIdle()

        assertEquals(RequestStatus.UNDEFINED, loginUiState.actionRequestStatus)

        coVerify(exactly = 1) { accountManager.findCredentials() }
        confirmVerified(preferencesRepository, localUsersRepository, remoteUsersRepository, accountManager)
    }

    @Test
    fun loginViewModel_initOfflineMode_successStatusWithSavedOfflineMode() = runTest {
        mockInitOfflineModeData()

        initViewModel()
        viewModel.initOfflineMode()
        advanceUntilIdle()

        assertEquals(RequestStatus.SUCCESS, loginUiState.actionRequestStatus)

        coVerify(exactly = 1) { accountManager.findCredentials() }
        coVerify(exactly = 1) { commonLocalRepository.initOfflineModeData() }
        coVerify(exactly = 1) { preferencesRepository.saveIsOfflineMode(true) }
        coVerify(exactly = 1) { preferencesRepository.saveCurrentUserId(FakeData.users[0].id) }
        confirmVerified(
            preferencesRepository,
            localUsersRepository,
            remoteUsersRepository,
            accountManager,
            commonLocalRepository
        )
    }

    private fun mockFindCredentialsWithError() {
        coEvery { accountManager.findCredentials() } returns null
    }

    private fun mockFindCredentials() {
        coEvery { accountManager.findCredentials() } returns credentialResponse()
    }

    private fun mockFindUnsupportedCredentials() {
        val credentialResponse = mockk<GetCredentialResponse>()
        every { credentialResponse.credential } returns mockk<Credential>()

        coEvery { accountManager.findCredentials() } returns credentialResponse
    }

    private fun credentialResponse(): GetCredentialResponse {
        val passwordCredential = mockk<PasswordCredential>()
        every { passwordCredential.id } returns FakeData.loginFormData.username
        every { passwordCredential.password } returns FakeData.loginFormData.password

        val credentialResponse = mockk<GetCredentialResponse>()
        every { credentialResponse.credential } returns passwordCredential

        return credentialResponse
    }

    private fun mockDataWithInternet() {
        coEvery { remoteUsersRepository.loginUser(any()) } returns Response.success(mockk(relaxed = true))
        coEvery { accountManager.saveCredentials(any()) } just Runs
        coEvery { localUsersRepository.insertUser(any()) } just Runs
    }

    private fun mockInitOfflineModeData() {
        coEvery { commonLocalRepository.initOfflineModeData() } just Runs
    }

    private fun mockWrongData() {
        coEvery { remoteUsersRepository.loginUser(any()) } returns
                Response.error(BAD_REQUEST, BAD_REQUEST.toString().toResponseBody())
    }

    private fun mockDataWithoutInternet() {
        coEvery { remoteUsersRepository.loginUser(any()) } throws IOException()
    }
}