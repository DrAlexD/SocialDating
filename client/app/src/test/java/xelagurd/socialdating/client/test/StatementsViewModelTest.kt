package xelagurd.socialdating.client.test

import java.io.IOException
import kotlin.random.Random
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import android.content.Context
import androidx.lifecycle.SavedStateHandle
import io.mockk.Runs
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.confirmVerified
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.verify
import junit.framework.TestCase.assertEquals
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import retrofit2.Response
import xelagurd.socialdating.client.MainDispatcherRule
import xelagurd.socialdating.client.data.local.repository.LocalDefiningThemesRepository
import xelagurd.socialdating.client.data.local.repository.LocalStatementsRepository
import xelagurd.socialdating.client.data.model.Statement
import xelagurd.socialdating.client.data.remote.NOT_FOUND
import xelagurd.socialdating.client.data.remote.repository.RemoteDefiningThemesRepository
import xelagurd.socialdating.client.data.remote.repository.RemoteStatementsRepository
import xelagurd.socialdating.client.mockkList
import xelagurd.socialdating.client.ui.navigation.StatementsDestination
import xelagurd.socialdating.client.ui.state.RequestStatus
import xelagurd.socialdating.client.ui.viewmodel.StatementsViewModel

@OptIn(ExperimentalCoroutinesApi::class)
class StatementsViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val context = mockk<Context>(relaxed = true)
    private val savedStateHandle = mockk<SavedStateHandle>()
    private val remoteStatementsRepository = mockk<RemoteStatementsRepository>()
    private val localStatementsRepository = mockk<LocalStatementsRepository>()
    private val remoteDefiningThemesRepository = mockk<RemoteDefiningThemesRepository>()
    private val localDefiningThemesRepository = mockk<LocalDefiningThemesRepository>()

    private lateinit var viewModel: StatementsViewModel
    private lateinit var statementsFlow: MutableStateFlow<List<Statement>>
    private val statementsUiState
        get() = viewModel.uiState.value

    private val userId = Random.nextInt()
    private val categoryId = Random.nextInt()

    @Before
    fun setup() {
        statementsFlow = MutableStateFlow(mockkList())

        mockGeneralMethods()
    }

    private fun initViewModel() {
        viewModel = StatementsViewModel(
            context,
            savedStateHandle,
            remoteStatementsRepository,
            localStatementsRepository,
            remoteDefiningThemesRepository,
            localDefiningThemesRepository
        )
    }

    private fun TestScope.setupUiStateCollecting() {
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
            viewModel.uiState.collect {}
        }
    }

    @Test
    fun statementsViewModel_checkStateWithInternet() = runTest {
        mockDataWithInternet()

        initViewModel()
        setupUiStateCollecting()
        advanceUntilIdle()

        assertEquals(RequestStatus.SUCCESS, statementsUiState.dataRequestStatus)

        verify(exactly = 1) { localStatementsRepository.getStatements(any()) }
        coVerify(exactly = 1) { remoteDefiningThemesRepository.getDefiningThemes(any()) }
        coVerify(exactly = 1) { remoteStatementsRepository.getStatements(any(), any()) }
        coVerify(exactly = 1) { localDefiningThemesRepository.insertDefiningThemes(any()) }
        coVerify(exactly = 1) { localStatementsRepository.replaceStatements(any(), any()) }
        confirmVerified(
            localDefiningThemesRepository,
            remoteDefiningThemesRepository,
            localStatementsRepository,
            remoteStatementsRepository
        )
    }

    @Test
    fun statementsViewModel_checkStateWithEmptyData() = runTest {
        mockEmptyData()

        initViewModel()
        setupUiStateCollecting()
        advanceUntilIdle()

        assertEquals(RequestStatus.FAILURE(), statementsUiState.dataRequestStatus)

        verify(exactly = 1) { localStatementsRepository.getStatements(any()) }
        coVerify(exactly = 1) { remoteDefiningThemesRepository.getDefiningThemes(any()) }
        confirmVerified(
            localDefiningThemesRepository,
            remoteDefiningThemesRepository,
            localStatementsRepository,
            remoteStatementsRepository
        )
    }

    @Test
    fun statementsViewModel_checkStateWithoutInternet() = runTest {
        mockDataWithoutInternet()

        initViewModel()
        setupUiStateCollecting()
        advanceUntilIdle()

        assertEquals(RequestStatus.ERROR(), statementsUiState.dataRequestStatus)

        verify(exactly = 1) { localStatementsRepository.getStatements(any()) }
        coVerify(exactly = 1) { remoteDefiningThemesRepository.getDefiningThemes(any()) }
        coVerify(exactly = 1) { localDefiningThemesRepository.getDefiningThemes() } // FixMe: remove after adding server hosting
        coVerify(exactly = 1) { localStatementsRepository.getStatements() } // FixMe: remove after adding server hosting
        confirmVerified(
            localDefiningThemesRepository,
            remoteDefiningThemesRepository,
            localStatementsRepository,
            remoteStatementsRepository
        )
    }

    @Test
    fun statementsViewModel_checkRefreshedSuccessStateWithoutInternet() = runTest {
        mockDataWithInternet()

        initViewModel()
        setupUiStateCollecting()
        advanceUntilIdle()

        mockDataWithoutInternet()

        viewModel.getStatements()
        advanceUntilIdle()

        assertEquals(RequestStatus.ERROR(), statementsUiState.dataRequestStatus)

        verify(exactly = 1) { localStatementsRepository.getStatements(any()) }
        coVerify(exactly = 2) { remoteDefiningThemesRepository.getDefiningThemes(any()) }
        coVerify(exactly = 1) { remoteStatementsRepository.getStatements(any(), any()) }
        coVerify(exactly = 1) { localDefiningThemesRepository.insertDefiningThemes(any()) }
        coVerify(exactly = 1) { localStatementsRepository.replaceStatements(any(), any()) }
        coVerify(exactly = 1) { localDefiningThemesRepository.getDefiningThemes() } // FixMe: remove after adding server hosting
        coVerify(exactly = 1) { localStatementsRepository.getStatements() } // FixMe: remove after adding server hosting
        confirmVerified(
            localDefiningThemesRepository,
            remoteDefiningThemesRepository,
            localStatementsRepository,
            remoteStatementsRepository
        )
    }

    @Test
    fun statementsViewModel_checkRefreshedErrorStateWithInternet() = runTest {
        mockDataWithoutInternet()

        initViewModel()
        setupUiStateCollecting()
        advanceUntilIdle()

        mockDataWithInternet()

        viewModel.getStatements()
        advanceUntilIdle()

        assertEquals(RequestStatus.SUCCESS, statementsUiState.dataRequestStatus)

        verify(exactly = 1) { localStatementsRepository.getStatements(any()) }
        coVerify(exactly = 2) { remoteDefiningThemesRepository.getDefiningThemes(any()) }
        coVerify(exactly = 1) { remoteStatementsRepository.getStatements(any(), any()) }
        coVerify(exactly = 1) { localDefiningThemesRepository.insertDefiningThemes(any()) }
        coVerify(exactly = 1) { localStatementsRepository.replaceStatements(any(), any()) }
        coVerify(exactly = 1) { localDefiningThemesRepository.getDefiningThemes() } // FixMe: remove after adding server hosting
        coVerify(exactly = 1) { localStatementsRepository.getStatements() } // FixMe: remove after adding server hosting
        confirmVerified(
            localDefiningThemesRepository,
            remoteDefiningThemesRepository,
            localStatementsRepository,
            remoteStatementsRepository
        )
    }

    @Test
    fun statementsViewModel_checkRefreshedSuccessStateWithInternet() = runTest {
        mockDataWithInternet()

        initViewModel()
        setupUiStateCollecting()
        advanceUntilIdle()

        viewModel.getStatements()
        advanceUntilIdle()

        assertEquals(RequestStatus.SUCCESS, statementsUiState.dataRequestStatus)

        verify(exactly = 1) { localStatementsRepository.getStatements(any()) }
        coVerify(exactly = 2) { remoteDefiningThemesRepository.getDefiningThemes(any()) }
        coVerify(exactly = 2) { remoteStatementsRepository.getStatements(any(), any()) }
        coVerify(exactly = 2) { localDefiningThemesRepository.insertDefiningThemes(any()) }
        coVerify(exactly = 2) { localStatementsRepository.replaceStatements(any(), any()) }
        confirmVerified(
            localDefiningThemesRepository,
            remoteDefiningThemesRepository,
            localStatementsRepository,
            remoteStatementsRepository
        )
    }

    @Test
    fun statementsViewModel_checkRefreshedErrorStateWithoutInternet() = runTest {
        mockDataWithoutInternet()

        initViewModel()
        setupUiStateCollecting()
        advanceUntilIdle()

        viewModel.getStatements()
        advanceUntilIdle()

        assertEquals(RequestStatus.ERROR(), statementsUiState.dataRequestStatus)

        verify(exactly = 1) { localStatementsRepository.getStatements(any()) }
        coVerify(exactly = 2) { remoteDefiningThemesRepository.getDefiningThemes(any()) }
        coVerify(exactly = 2) { localDefiningThemesRepository.getDefiningThemes() } // FixMe: remove after adding server hosting
        coVerify(exactly = 2) { localStatementsRepository.getStatements() } // FixMe: remove after adding server hosting
        confirmVerified(
            localDefiningThemesRepository,
            remoteDefiningThemesRepository,
            localStatementsRepository,
            remoteStatementsRepository
        )
    }

    private fun mockGeneralMethods() {
        every { savedStateHandle.get<Int>(StatementsDestination.userId) } returns userId
        every { savedStateHandle.get<Int>(StatementsDestination.categoryId) } returns categoryId
        every { localStatementsRepository.getStatements(any()) } returns statementsFlow
    }

    private fun mockDataWithInternet() {
        coEvery { remoteDefiningThemesRepository.getDefiningThemes(any()) } returns
                Response.success(mockkList(relaxed = true))
        coEvery { remoteStatementsRepository.getStatements(any(), any()) } returns
                Response.success(mockkList())

        coEvery { localDefiningThemesRepository.insertDefiningThemes(any()) } just Runs
        coEvery { localStatementsRepository.replaceStatements(any(), any()) } just Runs
    }

    private fun mockEmptyData() {
        coEvery { remoteDefiningThemesRepository.getDefiningThemes(any()) } returns
                Response.error(NOT_FOUND, NOT_FOUND.toString().toResponseBody())
    }

    private fun mockDataWithoutInternet() {
        coEvery { remoteDefiningThemesRepository.getDefiningThemes(any()) } throws IOException()

        // FixMe: remove after adding server hosting
        every { localDefiningThemesRepository.getDefiningThemes() } returns flowOf(mockkList())
        every { localStatementsRepository.getStatements() } returns flowOf(mockkList())
    }
}