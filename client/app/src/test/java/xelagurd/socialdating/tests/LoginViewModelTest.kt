package xelagurd.socialdating.tests

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
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import xelagurd.socialdating.AccountManager
import xelagurd.socialdating.MainDispatcherRule
import xelagurd.socialdating.PreferencesRepository
import xelagurd.socialdating.data.fake.FakeDataSource
import xelagurd.socialdating.data.local.repository.LocalUsersRepository
import xelagurd.socialdating.data.model.User
import xelagurd.socialdating.data.model.details.LoginDetails
import xelagurd.socialdating.data.model.enums.Gender
import xelagurd.socialdating.data.model.enums.Purpose
import xelagurd.socialdating.data.remote.repository.RemoteUsersRepository
import xelagurd.socialdating.ui.state.RequestStatus
import xelagurd.socialdating.ui.viewmodel.LoginViewModel

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

    private val loginDetails = LoginDetails("", "")
    private val remoteUser =
        User(1, "", Gender.FEMALE, "", "", "", 40, "", Purpose.RELATIONSHIPS, 20)

    @Before
    fun setup() {
        mockFindCredentialsWithError()

        viewModel = LoginViewModel(
            context,
            remoteRepository,
            localRepository,
            preferencesRepository,
            accountManager
        )
        viewModel.updateUiState(loginDetails)
        viewModel.loginWithInput()
    }

    @Test
    fun loginViewModel_loginWithInternet() = runTest {
        mockDataWithInternet()
        advanceUntilIdle()

        assertEquals(RequestStatus.SUCCESS, loginUiState.actionRequestStatus)
    }

    @Test
    fun loginViewModel_loginWithoutInternet() = runTest {
        mockDataWithoutInternet()
        advanceUntilIdle()

        // TODO: Change to ERROR after implementing server
        assertEquals(RequestStatus.SUCCESS, loginUiState.actionRequestStatus)
    }

    @Test
    fun loginViewModel_loginWithWrongData() = runTest {
        mockWrongData()
        advanceUntilIdle()

        assertEquals(RequestStatus.FAILURE(), loginUiState.actionRequestStatus)
    }

    @Test
    fun loginViewModel_retryLoginWithInternet() = runTest {
        mockDataWithoutInternet()
        advanceUntilIdle()

        mockDataWithInternet()
        viewModel.loginWithInput()
        advanceUntilIdle()

        assertEquals(RequestStatus.SUCCESS, loginUiState.actionRequestStatus)
    }

    @Test
    fun loginViewModel_retryLoginWithRightData() = runTest {
        mockWrongData()
        advanceUntilIdle()

        mockDataWithInternet()
        viewModel.loginWithInput()
        advanceUntilIdle()

        assertEquals(RequestStatus.SUCCESS, loginUiState.actionRequestStatus)
    }

    private fun mockFindCredentialsWithData() {
        // FixMe: Can't test due to `BaseBundle not mocked`
        coEvery { accountManager.findCredentials() } returns GetCredentialResponse(
            PasswordCredential(
                id = loginDetails.username,
                password = loginDetails.password
            )
        )
    }

    private fun mockFindCredentialsWithError() {
        coEvery { accountManager.findCredentials() } returns null
        coEvery { accountManager.saveCredentials(loginDetails) } just Runs
    }

    private fun mockDataWithInternet() {
        coEvery { remoteRepository.loginUser(ofType<LoginDetails>()) } returns remoteUser
        coEvery { localRepository.insertUser(remoteUser) } just Runs
        coEvery { preferencesRepository.saveCurrentUserId(remoteUser.id) } just Runs
    }

    private fun mockWrongData() {
        every { context.getString(any()) } returns ""
        coEvery { remoteRepository.loginUser(ofType<LoginDetails>()) } returns null
    }

    private fun mockDataWithoutInternet() {
        every { context.getString(any()) } returns ""
        coEvery { remoteRepository.loginUser(ofType<LoginDetails>()) } throws IOException()

        coEvery {
            accountManager.saveCredentials(
                LoginDetails(
                    FakeDataSource.users[0].username,
                    FakeDataSource.users[0].password
                )
            )
        } just Runs // TODO: remove after implementing server
        every { localRepository.getUsers() } returns flowOf(listOf(FakeDataSource.users[0])) // TODO: remove after implementing server
        coEvery { preferencesRepository.saveCurrentUserId(FakeDataSource.users[0].id) } just Runs // TODO: remove after implementing server
    }
}