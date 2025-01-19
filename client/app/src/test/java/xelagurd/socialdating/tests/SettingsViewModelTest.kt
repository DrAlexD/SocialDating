package xelagurd.socialdating.tests

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import io.mockk.Runs
import io.mockk.coEvery
import io.mockk.just
import io.mockk.mockk
import junit.framework.TestCase.assertEquals
import org.junit.Rule
import org.junit.Test
import xelagurd.socialdating.MainDispatcherRule
import xelagurd.socialdating.PreferencesRepository
import xelagurd.socialdating.ui.state.RequestStatus
import xelagurd.socialdating.ui.viewmodel.SettingsViewModel

@OptIn(ExperimentalCoroutinesApi::class)
class SettingsViewModelTest {
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val preferencesRepository: PreferencesRepository = mockk()

    private lateinit var viewModel: SettingsViewModel
    private val settingsUiState
        get() = viewModel.uiState.value

    private fun initViewModel() {
        viewModel = SettingsViewModel(preferencesRepository)
    }

    @Test
    fun settingsViewModel_logout() = runTest {
        mockLogout()

        initViewModel()
        viewModel.logout()
        advanceUntilIdle()

        assertEquals(RequestStatus.SUCCESS, settingsUiState.actionRequestStatus)
    }

    private fun mockLogout() {
        coEvery { preferencesRepository.saveCurrentUserId(-1) } just Runs
    }
}