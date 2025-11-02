package xelagurd.socialdating.client.test

import java.io.IOException
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import android.content.Context
import io.mockk.Runs
import io.mockk.coEvery
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import junit.framework.TestCase.assertEquals
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.Rule
import org.junit.Test
import retrofit2.Response
import xelagurd.socialdating.client.MainDispatcherRule
import xelagurd.socialdating.client.data.AccountManager
import xelagurd.socialdating.client.data.PreferencesRepository
import xelagurd.socialdating.client.data.fake.FakeData
import xelagurd.socialdating.client.data.local.repository.LocalUsersRepository
import xelagurd.socialdating.client.data.model.User
import xelagurd.socialdating.client.data.model.additional.AuthResponse
import xelagurd.socialdating.client.data.model.details.RegistrationDetails
import xelagurd.socialdating.client.data.model.enums.Gender
import xelagurd.socialdating.client.data.model.enums.Purpose
import xelagurd.socialdating.client.data.model.enums.Role
import xelagurd.socialdating.client.data.remote.repository.RemoteUsersRepository
import xelagurd.socialdating.client.ui.form.LoginFormData
import xelagurd.socialdating.client.ui.form.RegistrationFormData
import xelagurd.socialdating.client.ui.state.RequestStatus
import xelagurd.socialdating.client.ui.viewmodel.RegistrationViewModel

@OptIn(ExperimentalCoroutinesApi::class)
class RegistrationViewModelTest {
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val context: Context = mockk()
    private val remoteRepository: RemoteUsersRepository = mockk()
    private val localRepository: LocalUsersRepository = mockk()
    private val preferencesRepository: PreferencesRepository = mockk()
    private val accountManager: AccountManager = mockk()

    private lateinit var viewModel: RegistrationViewModel
    private val registrationUiState
        get() = viewModel.uiState.value

    private val registrationFormData =
        RegistrationFormData("", Gender.MALE, "", "", "", "", "1", "", Purpose.ALL_AT_ONCE)
    private val remoteUser =
        User(1, "", Gender.FEMALE, "", "", "", 40, "", Purpose.RELATIONSHIPS, 20, Role.USER)
    private val authResponse = AuthResponse(remoteUser, "", "")

    private fun initViewModel() {
        viewModel = RegistrationViewModel(
            context,
            remoteRepository,
            localRepository,
            preferencesRepository,
            accountManager
        )
        viewModel.updateUiState(registrationFormData)
        viewModel.register()
    }

    @Test
    fun registrationViewModel_registrationWithInternet() = runTest {
        mockDataWithInternet()

        initViewModel()
        advanceUntilIdle()

        assertEquals(RequestStatus.SUCCESS, registrationUiState.actionRequestStatus)
    }

    @Test
    fun registrationViewModel_registrationWithoutInternet() = runTest {
        mockDataWithoutInternet()

        initViewModel()
        advanceUntilIdle()

        // FixMe: Change to ERROR after implementing server
        assertEquals(RequestStatus.SUCCESS, registrationUiState.actionRequestStatus)
    }

    @Test
    fun registrationViewModel_registrationWithWrongData() = runTest {
        mockWrongData()

        initViewModel()
        advanceUntilIdle()

        assertEquals(RequestStatus.FAILURE("400"), registrationUiState.actionRequestStatus)
    }

    @Test
    fun registrationViewModel_retryRegistrationWithInternet() = runTest {
        mockDataWithoutInternet()

        initViewModel()
        advanceUntilIdle()

        mockDataWithInternet()

        viewModel.register()
        advanceUntilIdle()

        assertEquals(RequestStatus.SUCCESS, registrationUiState.actionRequestStatus)
    }

    @Test
    fun registrationViewModel_retryRegistrationWithRightData() = runTest {
        mockWrongData()

        initViewModel()
        advanceUntilIdle()

        mockDataWithInternet()

        viewModel.register()
        advanceUntilIdle()

        assertEquals(RequestStatus.SUCCESS, registrationUiState.actionRequestStatus)
    }

    private fun mockDataWithInternet() {
        coEvery { remoteRepository.registerUser(ofType<RegistrationDetails>()) } returns
                Response.success(authResponse)
        coEvery { accountManager.saveCredentials(registrationFormData.toLoginFormData()) } just Runs
        coEvery { localRepository.insertUser(authResponse.user) } just Runs
        coEvery { preferencesRepository.saveAccessToken(authResponse.accessToken) } just Runs
        coEvery { preferencesRepository.saveRefreshToken(authResponse.refreshToken) } just Runs
        coEvery { preferencesRepository.saveCurrentUserId(authResponse.user.id) } just Runs
    }

    private fun mockWrongData() {
        every { context.getString(any()) } returns ""
        coEvery { remoteRepository.registerUser(ofType<RegistrationDetails>()) } returns
                Response.error(400, "400".toResponseBody())
    }

    private fun mockDataWithoutInternet() {
        every { context.getString(any()) } returns ""
        coEvery { remoteRepository.registerUser(ofType<RegistrationDetails>()) } throws IOException()

        // FixMe: remove after adding server hosting
        coEvery {
            accountManager.saveCredentials(
                LoginFormData(
                    FakeData.users[0].username,
                    FakeData.users[0].password
                )
            )
        } just Runs
        every { localRepository.getUsers() } returns flowOf(listOf(FakeData.users[0]))
        coEvery { preferencesRepository.saveCurrentUserId(FakeData.users[0].id) } just Runs
    }
}
