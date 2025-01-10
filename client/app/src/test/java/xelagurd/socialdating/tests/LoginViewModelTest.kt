package xelagurd.socialdating.tests

import java.io.IOException
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import io.mockk.Runs
import io.mockk.coEvery
import io.mockk.just
import io.mockk.mockk
import junit.framework.TestCase.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import xelagurd.socialdating.MainDispatcherRule
import xelagurd.socialdating.PreferencesRepository
import xelagurd.socialdating.data.local.repository.LocalUsersRepository
import xelagurd.socialdating.data.model.User
import xelagurd.socialdating.data.model.additional.LoginDetails
import xelagurd.socialdating.data.model.enums.Gender
import xelagurd.socialdating.data.model.enums.Purpose
import xelagurd.socialdating.data.remote.repository.RemoteUsersRepository
import xelagurd.socialdating.ui.state.RequestStatus
import xelagurd.socialdating.ui.viewmodel.LoginViewModel

@OptIn(ExperimentalCoroutinesApi::class)
class LoginViewModelTest {
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val remoteRepository: RemoteUsersRepository = mockk()
    private val localRepository: LocalUsersRepository = mockk()
    private val preferencesRepository: PreferencesRepository = mockk()

    private lateinit var viewModel: LoginViewModel
    private val loginUiState
        get() = viewModel.uiState.value

    private val loginDetails = LoginDetails("abc", "123")
    private val remoteUser =
        User(1, "abc", Gender.FEMALE, "abc", "123", "", 40, "", Purpose.RELATIONSHIPS, 20)

    @Before
    fun setup() {
        viewModel = LoginViewModel(remoteRepository, localRepository, preferencesRepository)
        viewModel.updateUiState(loginDetails)
        viewModel.login()
    }

    @Test
    fun loginViewModel_loginWithInternet() = runTest {
        mockDataWithInternet()
        advanceUntilIdle()

        assertEquals(RequestStatus.SUCCESS, loginUiState.requestStatus)
    }

    @Test
    fun loginViewModel_loginWithoutInternet() = runTest {
        mockDataWithoutInternet()
        advanceUntilIdle()

        assertEquals(RequestStatus.ERROR, loginUiState.requestStatus)
    }

    @Test
    fun loginViewModel_loginWithWrongData() = runTest {
        mockWrongData()
        advanceUntilIdle()

        assertEquals(RequestStatus.FAILED, loginUiState.requestStatus)
    }

    @Test
    fun loginViewModel_retryLoginWithInternet() = runTest {
        mockDataWithoutInternet()
        advanceUntilIdle()

        mockDataWithInternet()
        viewModel.login()
        advanceUntilIdle()

        assertEquals(RequestStatus.SUCCESS, loginUiState.requestStatus)
    }

    @Test
    fun loginViewModel_retryLoginWithRightData() = runTest {
        mockWrongData()
        advanceUntilIdle()

        mockDataWithInternet()
        viewModel.login()
        advanceUntilIdle()

        assertEquals(RequestStatus.SUCCESS, loginUiState.requestStatus)
    }

    private fun mockDataWithInternet() {
        coEvery { remoteRepository.loginUser(ofType<LoginDetails>()) } returns remoteUser
        coEvery { localRepository.insertUser(remoteUser) } just Runs
        coEvery { preferencesRepository.saveCurrentUserId(remoteUser.id) } just Runs
    }

    private fun mockWrongData() {
        coEvery { remoteRepository.loginUser(ofType<LoginDetails>()) } returns null
    }

    private fun mockDataWithoutInternet() {
        coEvery { remoteRepository.loginUser(ofType<LoginDetails>()) } throws IOException()
    }
}