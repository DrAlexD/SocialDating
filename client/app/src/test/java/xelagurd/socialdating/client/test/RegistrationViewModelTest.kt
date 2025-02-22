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
import xelagurd.socialdating.client.data.fake.FakeDataSource
import xelagurd.socialdating.client.data.local.repository.LocalUsersRepository
import xelagurd.socialdating.client.data.model.User
import xelagurd.socialdating.client.data.model.details.LoginDetails
import xelagurd.socialdating.client.data.model.details.RegistrationDetails
import xelagurd.socialdating.client.data.model.enums.Gender
import xelagurd.socialdating.client.data.model.enums.Purpose
import xelagurd.socialdating.client.data.remote.repository.RemoteUsersRepository
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

    private val registrationDetails =
        RegistrationDetails("", Gender.MALE, "", "", "", "", "", "", Purpose.ALL_AT_ONCE)
    private val remoteUser =
        User(1, "", Gender.FEMALE, "", "", "", 40, "", Purpose.RELATIONSHIPS, 20)

    private fun initViewModel() {
        viewModel = RegistrationViewModel(
            context,
            remoteRepository,
            localRepository,
            preferencesRepository,
            accountManager
        )
        viewModel.updateUiState(registrationDetails)
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

        // TODO: Change to ERROR after implementing server
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
                Response.success(remoteUser)
        coEvery { accountManager.saveCredentials(registrationDetails.toLoginDetails()) } just Runs
        coEvery { localRepository.insertUser(remoteUser) } just Runs
        coEvery { preferencesRepository.saveCurrentUserId(remoteUser.id) } just Runs
    }

    private fun mockWrongData() {
        every { context.getString(any()) } returns ""
        coEvery { remoteRepository.registerUser(ofType<RegistrationDetails>()) } returns
                Response.error(400, "400".toResponseBody())
    }

    private fun mockDataWithoutInternet() {
        every { context.getString(any()) } returns ""
        coEvery { remoteRepository.registerUser(ofType<RegistrationDetails>()) } throws IOException()

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
