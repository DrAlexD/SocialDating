package xelagurd.socialdating.client.test

import java.io.IOException
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import android.content.Context
import androidx.credentials.GetCredentialResponse
import androidx.credentials.PasswordCredential
import io.mockk.Runs
import io.mockk.coEvery
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
import xelagurd.socialdating.client.data.local.repository.LocalUsersRepository
import xelagurd.socialdating.client.data.model.details.LoginDetails
import xelagurd.socialdating.client.data.remote.BAD_REQUEST
import xelagurd.socialdating.client.data.remote.repository.RemoteUsersRepository
import xelagurd.socialdating.client.ui.state.RequestStatus
import xelagurd.socialdating.client.ui.viewmodel.LoginViewModel

@OptIn(ExperimentalCoroutinesApi::class)
class LoginViewModelTest {
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val context: Context = mockk()
    private val remoteRepository: RemoteUsersRepository = mockk()
    private val localRepository: LocalUsersRepository = mockk()
    private val preferencesRepository: PreferencesRepository = mockk()
    private val accountManager: AccountManager = mockk()

    private lateinit var viewModel: LoginViewModel
    private val loginUiState
        get() = viewModel.uiState.value

    private val loginFormData = FakeData.loginFormData
    private val authResponse = FakeData.authResponse

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
        viewModel.updateUiState(loginFormData)
        viewModel.loginWithInput()
    }

    @Test
    fun loginViewModel_loginWithInternet() = runTest {
        mockDataWithInternet()

        initViewModel()
        advanceUntilIdle()

        assertEquals(RequestStatus.SUCCESS, loginUiState.actionRequestStatus)
    }

    @Test
    fun loginViewModel_loginWithoutInternet() = runTest {
        mockDataWithoutInternet()

        initViewModel()
        advanceUntilIdle()

        // FixMe: Change to ERROR after implementing server
        assertEquals(RequestStatus.SUCCESS, loginUiState.actionRequestStatus)
    }

    @Test
    fun loginViewModel_loginWithWrongData() = runTest {
        mockWrongData()

        initViewModel()
        advanceUntilIdle()

        assertEquals(RequestStatus.FAILURE(BAD_REQUEST.toString()), loginUiState.actionRequestStatus)
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
    }

    private fun mockFindCredentialsWithData() {
        // FixMe: Can't test due to `BaseBundle not mocked`
        coEvery { accountManager.findCredentials() } returns
                GetCredentialResponse(
                    PasswordCredential(
                        id = loginFormData.username,
                        password = loginFormData.password
                    )
                )
    }

    private fun mockFindCredentialsWithError() {
        coEvery { accountManager.findCredentials() } returns null
        coEvery { accountManager.saveCredentials(loginFormData) } just Runs
    }

    private fun mockDataWithInternet() {
        coEvery { remoteRepository.loginUser(ofType<LoginDetails>()) } returns
                Response.success(authResponse)
        coEvery { localRepository.insertUser(authResponse.user) } just Runs
        coEvery { preferencesRepository.saveAccessToken(authResponse.accessToken) } just Runs
        coEvery { preferencesRepository.saveRefreshToken(authResponse.refreshToken) } just Runs
        coEvery { preferencesRepository.saveCurrentUserId(authResponse.user.id) } just Runs
    }

    private fun mockWrongData() {
        every { context.getString(any()) } returns ""
        coEvery { remoteRepository.loginUser(ofType<LoginDetails>()) } returns
                Response.error(BAD_REQUEST, BAD_REQUEST.toString().toResponseBody())
    }

    private fun mockDataWithoutInternet() {
        every { context.getString(any()) } returns ""
        coEvery { remoteRepository.loginUser(ofType<LoginDetails>()) } throws IOException()

        // FixMe: remove after adding server hosting
        coEvery { accountManager.saveCredentials(loginFormData) } just Runs
        every { localRepository.getUsers() } returns flowOf(listOf(FakeData.users[0]))
        coEvery { preferencesRepository.saveCurrentUserId(FakeData.users[0].id) } just Runs
    }
}