package xelagurd.socialdating.client.test

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import io.mockk.Runs
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.verify
import junit.framework.TestCase.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import xelagurd.socialdating.client.MainDispatcherRule
import xelagurd.socialdating.client.data.AppUiModeManager
import xelagurd.socialdating.client.data.PreferencesRepository
import xelagurd.socialdating.client.data.model.enums.ThemeMode
import xelagurd.socialdating.client.ui.viewmodel.MainViewModel

@OptIn(ExperimentalCoroutinesApi::class)
class MainViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val preferencesRepository = mockk<PreferencesRepository>()
    private val appUiModeManager = mockk<AppUiModeManager>()

    private lateinit var viewModel: MainViewModel
    private lateinit var themeModeFlow: MutableStateFlow<ThemeMode>

    @Before
    fun setup() {
        themeModeFlow = MutableStateFlow(ThemeMode.DARK)
    }

    private fun initViewModel() {
        every { preferencesRepository.themeMode } returns themeModeFlow
        every { appUiModeManager.setThemeMode(any()) } just Runs

        viewModel = MainViewModel(preferencesRepository, appUiModeManager)
    }

    private fun TestScope.setupThemeModeCollecting() {
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
            viewModel.themeMode.collect {}
        }
    }

    @Test
    fun mainViewModel_savedThemeMode_sameInitialThemeMode() = runTest {
        initViewModel()

        assertEquals(ThemeMode.DARK, viewModel.themeMode.value)
    }

    @Test
    fun mainViewModel_init_appliedSavedThemeMode() = runTest {
        initViewModel()

        verify(exactly = 1) { appUiModeManager.setThemeMode(ThemeMode.DARK) }
    }

    @Test
    fun mainViewModel_changedSavedThemeMode_updatedThemeMode() = runTest {
        initViewModel()
        setupThemeModeCollecting()
        advanceUntilIdle()

        themeModeFlow.value = ThemeMode.LIGHT
        advanceUntilIdle()

        assertEquals(ThemeMode.LIGHT, viewModel.themeMode.value)
    }
}
