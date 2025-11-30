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
import org.junit.Rule
import org.junit.Test
import retrofit2.Response
import xelagurd.socialdating.client.MainDispatcherRule
import xelagurd.socialdating.client.data.AccountManager
import xelagurd.socialdating.client.data.PreferencesRepository
import xelagurd.socialdating.client.data.fake.FakeData
import xelagurd.socialdating.client.data.local.repository.LocalUsersRepository
import xelagurd.socialdating.client.data.remote.ApiUtils.BAD_REQUEST
import xelagurd.socialdating.client.data.remote.repository.RemoteUsersRepository
import xelagurd.socialdating.client.ui.state.RequestStatus
import xelagurd.socialdating.client.ui.viewmodel.RegistrationViewModel

@OptIn(ExperimentalCoroutinesApi::class)
class RegistrationViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val context = mockk<Context>(relaxed = true)
    private val accountManager = mockk<AccountManager>()
    private val preferencesRepository = mockk<PreferencesRepository>(relaxed = true)
    private val remoteUsersRepository = mockk<RemoteUsersRepository>()
    private val localUsersRepository = mockk<LocalUsersRepository>()

    private lateinit var viewModel: RegistrationViewModel
    private val registrationUiState
        get() = viewModel.uiState.value

    private val registrationFormData = FakeData.registrationFormData

    private fun initViewModel() {
        viewModel = RegistrationViewModel(
            context,
            accountManager,
            preferencesRepository,
            remoteUsersRepository,
            localUsersRepository
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

        coVerify(exactly = 1) { remoteUsersRepository.registerUser(any()) }
        coVerify(exactly = 1) { accountManager.saveCredentials(any()) }
        coVerify(exactly = 1) { localUsersRepository.insertUser(any()) }
        coVerify(exactly = 1) { preferencesRepository.saveAccessToken(any()) }
        coVerify(exactly = 1) { preferencesRepository.saveRefreshToken(any()) }
        coVerify(exactly = 1) { preferencesRepository.saveCurrentUserId(any()) }
        confirmVerified(preferencesRepository, localUsersRepository, remoteUsersRepository, accountManager)
    }

    @Test
    fun registrationViewModel_registrationWithoutInternet() = runTest {
        mockDataWithoutInternet()

        initViewModel()
        advanceUntilIdle()

        assertEquals(RequestStatus.ERROR(), registrationUiState.actionRequestStatus)

        coVerify(exactly = 1) { remoteUsersRepository.registerUser(any()) }
        confirmVerified(preferencesRepository, localUsersRepository, remoteUsersRepository, accountManager)
    }

    @Test
    fun registrationViewModel_registrationWithWrongData() = runTest {
        mockWrongData()

        initViewModel()
        advanceUntilIdle()

        assertEquals(RequestStatus.FAILURE(BAD_REQUEST.toString()), registrationUiState.actionRequestStatus)

        coVerify(exactly = 1) { remoteUsersRepository.registerUser(any()) }
        confirmVerified(preferencesRepository, localUsersRepository, remoteUsersRepository, accountManager)
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

        coVerify(exactly = 2) { remoteUsersRepository.registerUser(any()) }
        coVerify(exactly = 1) { accountManager.saveCredentials(any()) }
        coVerify(exactly = 1) { localUsersRepository.insertUser(any()) }
        coVerify(exactly = 1) { preferencesRepository.saveAccessToken(any()) }
        coVerify(exactly = 1) { preferencesRepository.saveRefreshToken(any()) }
        coVerify(exactly = 1) { preferencesRepository.saveCurrentUserId(any()) }
        confirmVerified(preferencesRepository, localUsersRepository, remoteUsersRepository, accountManager)
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

        coVerify(exactly = 2) { remoteUsersRepository.registerUser(any()) }
        coVerify(exactly = 1) { accountManager.saveCredentials(any()) }
        coVerify(exactly = 1) { localUsersRepository.insertUser(any()) }
        coVerify(exactly = 1) { preferencesRepository.saveAccessToken(any()) }
        coVerify(exactly = 1) { preferencesRepository.saveRefreshToken(any()) }
        coVerify(exactly = 1) { preferencesRepository.saveCurrentUserId(any()) }
        confirmVerified(preferencesRepository, localUsersRepository, remoteUsersRepository, accountManager)
    }

    private fun mockDataWithInternet() {
        coEvery { remoteUsersRepository.registerUser(any()) } returns Response.success(mockk(relaxed = true))
        coEvery { accountManager.saveCredentials(any()) } just Runs
        coEvery { localUsersRepository.insertUser(any()) } just Runs
    }

    private fun mockWrongData() {
        coEvery { remoteUsersRepository.registerUser(any()) } returns
                Response.error(BAD_REQUEST, BAD_REQUEST.toString().toResponseBody())
    }

    private fun mockDataWithoutInternet() {
        coEvery { remoteUsersRepository.registerUser(any()) } throws IOException()
    }
}
