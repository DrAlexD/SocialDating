package xelagurd.socialdating.client.test

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import io.mockk.Runs
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.confirmVerified
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.verify
import junit.framework.TestCase.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import xelagurd.socialdating.client.MainDispatcherRule
import xelagurd.socialdating.client.data.AppLocaleManager
import xelagurd.socialdating.client.data.AppUiModeManager
import xelagurd.socialdating.client.data.PreferencesRepository
import xelagurd.socialdating.client.data.local.repository.CommonLocalRepository
import xelagurd.socialdating.client.data.model.enums.AppLanguage
import xelagurd.socialdating.client.data.model.enums.ThemeMode
import xelagurd.socialdating.client.ui.state.RequestStatus
import xelagurd.socialdating.client.ui.viewmodel.SettingsViewModel

@OptIn(ExperimentalCoroutinesApi::class)
class SettingsViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val preferencesRepository = mockk<PreferencesRepository>()
    private val commonLocalRepository = mockk<CommonLocalRepository>()
    private val appLocaleManager = mockk<AppLocaleManager>()
    private val appUiModeManager = mockk<AppUiModeManager>()

    private lateinit var viewModel: SettingsViewModel
    private val settingsUiState
        get() = viewModel.uiState.value

    @Before
    fun setup() {
        mockGeneralMethods()
    }

    private fun initViewModel() {
        viewModel = SettingsViewModel(
            preferencesRepository,
            commonLocalRepository,
            appLocaleManager,
            appUiModeManager
        )
    }

    @Test
    fun settingsViewModel_init_savedThemeModeAndLanguage() {
        every { preferencesRepository.themeMode } returns flowOf(ThemeMode.DARK)
        every { appLocaleManager.getAppLanguage() } returns AppLanguage.RUSSIAN

        initViewModel()

        assertEquals(ThemeMode.DARK, settingsUiState.themeMode)
        assertEquals(AppLanguage.RUSSIAN, settingsUiState.language)
    }

    @Test
    fun settingsViewModel_updateThemeMode_savedAndAppliedThemeMode() = runTest {
        coEvery { preferencesRepository.saveThemeMode(any()) } just Runs
        every { appUiModeManager.setThemeMode(any()) } just Runs

        initViewModel()
        viewModel.updateThemeMode(ThemeMode.LIGHT)
        advanceUntilIdle()

        assertEquals(ThemeMode.LIGHT, settingsUiState.themeMode)

        coVerify(exactly = 1) { preferencesRepository.saveThemeMode(ThemeMode.LIGHT) }
        verify(exactly = 1) { appUiModeManager.setThemeMode(ThemeMode.LIGHT) }
    }

    @Test
    fun settingsViewModel_updateLanguage_savedLanguage() {
        every { appLocaleManager.setAppLanguage(any()) } just Runs

        initViewModel()
        viewModel.updateLanguage(AppLanguage.ENGLISH)

        assertEquals(AppLanguage.ENGLISH, settingsUiState.language)

        verify(exactly = 1) { appLocaleManager.setAppLanguage(AppLanguage.ENGLISH) }
    }

    @Test
    fun settingsViewModel_logout_successStatusWithClearedData() = runTest {
        mockLogout()

        initViewModel()
        viewModel.logout()
        advanceUntilIdle()

        assertEquals(RequestStatus.SUCCESS, settingsUiState.actionRequestStatus)

        verify(exactly = 1) { preferencesRepository.themeMode }
        coVerify(exactly = 1) { preferencesRepository.clearPreferences() }
        coVerify(exactly = 1) { commonLocalRepository.clearData() }
        confirmVerified(preferencesRepository, commonLocalRepository)
    }

    private fun mockGeneralMethods() {
        every { preferencesRepository.themeMode } returns flowOf(ThemeMode.SYSTEM)
        every { appLocaleManager.getAppLanguage() } returns AppLanguage.SYSTEM
    }

    private fun mockLogout() {
        coEvery { preferencesRepository.clearPreferences() } just Runs
        coEvery { commonLocalRepository.clearData() } just Runs
    }
}
