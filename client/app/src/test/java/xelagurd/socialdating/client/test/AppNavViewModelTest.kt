package xelagurd.socialdating.client.test

import kotlin.random.Random
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import io.mockk.every
import io.mockk.mockk
import junit.framework.TestCase.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import xelagurd.socialdating.client.MainDispatcherRule
import xelagurd.socialdating.client.data.PreferencesRepository
import xelagurd.socialdating.client.ui.navigation.AppNavViewModel

@OptIn(ExperimentalCoroutinesApi::class)
class AppNavViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val preferencesRepository = mockk<PreferencesRepository>()

    private lateinit var viewModel: AppNavViewModel
    private lateinit var currentUserIdFlow: MutableStateFlow<Int>

    private val currentUserId = Random.nextInt()

    @Before
    fun setup() {
        currentUserIdFlow = MutableStateFlow(currentUserId)
    }

    private fun initViewModel() {
        every { preferencesRepository.currentUserId } returns currentUserIdFlow

        viewModel = AppNavViewModel(preferencesRepository)
    }

    private fun TestScope.setupCurrentUserIdCollecting() {
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
            viewModel.currentUserId.collect {}
        }
    }

    @Test
    fun appNavViewModel_savedCurrentUserId_sameInitialCurrentUserId() = runTest {
        initViewModel()

        assertEquals(currentUserId, viewModel.currentUserId.value)
    }

    @Test
    fun appNavViewModel_changedSavedCurrentUserId_updatedCurrentUserId() = runTest {
        val updatedCurrentUserId = currentUserId + 1

        initViewModel()
        setupCurrentUserIdCollecting()
        advanceUntilIdle()

        currentUserIdFlow.value = updatedCurrentUserId
        advanceUntilIdle()

        assertEquals(updatedCurrentUserId, viewModel.currentUserId.value)
    }
}