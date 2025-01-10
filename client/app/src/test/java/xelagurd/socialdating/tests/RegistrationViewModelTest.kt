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
import xelagurd.socialdating.data.fake.FakeDataSource
import xelagurd.socialdating.data.local.repository.LocalUsersRepository
import xelagurd.socialdating.data.model.User
import xelagurd.socialdating.data.model.additional.RegistrationDetails
import xelagurd.socialdating.data.model.enums.Gender
import xelagurd.socialdating.data.model.enums.Purpose
import xelagurd.socialdating.data.remote.repository.RemoteUsersRepository
import xelagurd.socialdating.ui.state.RequestStatus
import xelagurd.socialdating.ui.viewmodel.RegistrationViewModel

@OptIn(ExperimentalCoroutinesApi::class)
class RegistrationViewModelTest {
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val remoteRepository: RemoteUsersRepository = mockk()
    private val localRepository: LocalUsersRepository = mockk()
    private val preferencesRepository: PreferencesRepository = mockk()

    private lateinit var viewModel: RegistrationViewModel
    private val registrationUiState
        get() = viewModel.uiState.value

    private val registrationDetails = RegistrationDetails(
        "qwe", Gender.MALE, "abc", "123", "123", "", "20", "Moscow", Purpose.ALL_AT_ONCE
    )
    private val remoteUser =
        User(1, "abc", Gender.FEMALE, "abc", "123", "", 40, "", Purpose.RELATIONSHIPS, 20)

    @Before
    fun setup() {
        viewModel = RegistrationViewModel(remoteRepository, localRepository, preferencesRepository)
        viewModel.updateUiState(registrationDetails)
        viewModel.register()
    }

    @Test
    fun registrationViewModel_registrationWithInternet() = runTest {
        mockDataWithInternet()
        advanceUntilIdle()

        assertEquals(RequestStatus.SUCCESS, registrationUiState.requestStatus)
    }

    @Test
    fun registrationViewModel_registrationWithoutInternet() = runTest {
        mockDataWithoutInternet()
        advanceUntilIdle()

        assertEquals(RequestStatus.SUCCESS, registrationUiState.requestStatus) // TODO: Change to ERROR after implementing server
    }

    @Test
    fun registrationViewModel_registrationWithWrongData() = runTest {
        mockWrongData()
        advanceUntilIdle()

        assertEquals(RequestStatus.FAILED, registrationUiState.requestStatus)
    }

    @Test
    fun registrationViewModel_retryRegistrationWithInternet() = runTest {
        mockDataWithoutInternet()
        advanceUntilIdle()

        mockDataWithInternet()
        viewModel.register()
        advanceUntilIdle()

        assertEquals(RequestStatus.SUCCESS, registrationUiState.requestStatus)
    }

    @Test
    fun registrationViewModel_retryRegistrationWithRightData() = runTest {
        mockWrongData()
        advanceUntilIdle()

        mockDataWithInternet()
        viewModel.register()
        advanceUntilIdle()

        assertEquals(RequestStatus.SUCCESS, registrationUiState.requestStatus)
    }

    private fun mockDataWithInternet() {
        coEvery { remoteRepository.registerUser(ofType<RegistrationDetails>()) } returns remoteUser
        coEvery { localRepository.insertUser(remoteUser) } just Runs
        coEvery { preferencesRepository.saveCurrentUserId(remoteUser.id) } just Runs
    }

    private fun mockWrongData() {
        coEvery { remoteRepository.registerUser(ofType<RegistrationDetails>()) } returns null
    }

    private fun mockDataWithoutInternet() {
        coEvery { remoteRepository.registerUser(ofType<RegistrationDetails>()) } throws IOException()
        coEvery { localRepository.insertUser(FakeDataSource.users[0]) } just Runs // TODO: remove after implementing server
        coEvery { preferencesRepository.saveCurrentUserId(FakeDataSource.users[0].id) } just Runs // TODO: remove after implementing server
    }
}
