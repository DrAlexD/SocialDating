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
import org.junit.Rule
import org.junit.Test
import retrofit2.Response
import xelagurd.socialdating.client.MainDispatcherRule
import xelagurd.socialdating.client.data.AccountManager
import xelagurd.socialdating.client.data.PreferencesRepository
import xelagurd.socialdating.client.data.fake.FakeData
import xelagurd.socialdating.client.data.local.repository.LocalUsersRepository
import xelagurd.socialdating.client.data.remote.BAD_REQUEST
import xelagurd.socialdating.client.data.remote.repository.RemoteUsersRepository
import xelagurd.socialdating.client.mockkList
import xelagurd.socialdating.client.ui.state.RequestStatus
import xelagurd.socialdating.client.ui.viewmodel.RegistrationViewModel

@OptIn(ExperimentalCoroutinesApi::class)
class RegistrationViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val context = mockk<Context>(relaxed = true)
    private val preferencesRepository = mockk<PreferencesRepository>(relaxed = true)
    private val accountManager = mockk<AccountManager>()
    private val remoteRepository = mockk<RemoteUsersRepository>()
    private val localRepository = mockk<LocalUsersRepository>()

    private lateinit var viewModel: RegistrationViewModel
    private val registrationUiState
        get() = viewModel.uiState.value

    private val registrationFormData = FakeData.registrationFormData

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

        coVerify(exactly = 1) { remoteRepository.registerUser(any()) }
        coVerify(exactly = 1) { accountManager.saveCredentials(any()) }
        coVerify(exactly = 1) { localRepository.insertUser(any()) }
        coVerify(exactly = 1) { preferencesRepository.saveAccessToken(any()) }
        coVerify(exactly = 1) { preferencesRepository.saveRefreshToken(any()) }
        coVerify(exactly = 1) { preferencesRepository.saveCurrentUserId(any()) }
        confirmVerified(preferencesRepository, localRepository, remoteRepository, accountManager)
    }

    @Test
    fun registrationViewModel_registrationWithoutInternet() = runTest {
        mockDataWithoutInternet()

        initViewModel()
        advanceUntilIdle()

        // FixMe: Change to ERROR after implementing server
        assertEquals(RequestStatus.SUCCESS, registrationUiState.actionRequestStatus)

        coVerify(exactly = 1) { remoteRepository.registerUser(any()) }
        coVerify(exactly = 1) { accountManager.saveCredentials(any()) } // FixMe: remove after adding server hosting
        coVerify(exactly = 1) { localRepository.getUsers() } // FixMe: remove after adding server hosting
        coVerify(exactly = 1) { localRepository.insertUser(any()) } // FixMe: remove after adding server hosting
        coVerify(exactly = 1) { preferencesRepository.saveCurrentUserId(any()) } // FixMe: remove after adding server hosting
        confirmVerified(preferencesRepository, localRepository, remoteRepository, accountManager)
    }

    @Test
    fun registrationViewModel_registrationWithWrongData() = runTest {
        mockWrongData()

        initViewModel()
        advanceUntilIdle()

        assertEquals(RequestStatus.FAILURE(BAD_REQUEST.toString()), registrationUiState.actionRequestStatus)

        coVerify(exactly = 1) { remoteRepository.registerUser(any()) }
        confirmVerified(preferencesRepository, localRepository, remoteRepository, accountManager)
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

        coVerify(exactly = 2) { remoteRepository.registerUser(any()) }
        coVerify(exactly = 2) { accountManager.saveCredentials(any()) } // FixMe: set to 1 after adding server hosting
        coVerify(exactly = 1) { localRepository.getUsers() } // FixMe: remove after adding server hosting
        coVerify(exactly = 2) { localRepository.insertUser(any()) } // FixMe: set to 1 after adding server hosting
        coVerify(exactly = 1) { preferencesRepository.saveAccessToken(any()) }
        coVerify(exactly = 1) { preferencesRepository.saveRefreshToken(any()) }
        coVerify(exactly = 2) { preferencesRepository.saveCurrentUserId(any()) } // FixMe: set to 1 after adding server hosting
        confirmVerified(preferencesRepository, localRepository, remoteRepository, accountManager)
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

        coVerify(exactly = 2) { remoteRepository.registerUser(any()) }
        coVerify(exactly = 1) { accountManager.saveCredentials(any()) }
        coVerify(exactly = 1) { localRepository.insertUser(any()) }
        coVerify(exactly = 1) { preferencesRepository.saveAccessToken(any()) }
        coVerify(exactly = 1) { preferencesRepository.saveRefreshToken(any()) }
        coVerify(exactly = 1) { preferencesRepository.saveCurrentUserId(any()) }
        confirmVerified(preferencesRepository, localRepository, remoteRepository, accountManager)
    }

    private fun mockDataWithInternet() {
        coEvery { remoteRepository.registerUser(any()) } returns Response.success(mockk(relaxed = true))
        coEvery { accountManager.saveCredentials(any()) } just Runs
        coEvery { localRepository.insertUser(any()) } just Runs
    }

    private fun mockWrongData() {
        coEvery { remoteRepository.registerUser(any()) } returns
                Response.error(BAD_REQUEST, BAD_REQUEST.toString().toResponseBody())
    }

    private fun mockDataWithoutInternet() {
        coEvery { remoteRepository.registerUser(any()) } throws IOException()

        // FixMe: remove after adding server hosting
        coEvery { accountManager.saveCredentials(any()) } just Runs
        every { localRepository.getUsers() } returns flowOf(mockkList(relaxed = true))
        coEvery { localRepository.insertUser(any()) } just Runs
    }
}
