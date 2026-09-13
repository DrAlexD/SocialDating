package xelagurd.socialdating.client.test

import java.io.IOException
import kotlin.random.Random
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.advanceTimeBy
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
import junit.framework.TestCase.assertFalse
import junit.framework.TestCase.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import retrofit2.Response
import xelagurd.socialdating.client.MainDispatcherRule
import xelagurd.socialdating.client.TestUtils.mockkList
import xelagurd.socialdating.client.data.PreferencesRepository
import xelagurd.socialdating.client.data.fake.FakeData
import xelagurd.socialdating.client.data.local.repository.CommonLocalRepository
import xelagurd.socialdating.client.data.local.repository.LocalStatementsRepository
import xelagurd.socialdating.client.data.model.enums.AppLanguage
import xelagurd.socialdating.client.data.model.Statement
import xelagurd.socialdating.client.data.model.dto.PageDto
import xelagurd.socialdating.client.data.model.dto.StatementDto
import xelagurd.socialdating.client.data.model.enums.StatementReactionType
import xelagurd.socialdating.client.data.remote.repository.RemoteDefiningThemesRepository
import xelagurd.socialdating.client.data.remote.repository.RemoteStatementsRepository
import xelagurd.socialdating.client.ui.navigation.StatementsDestination
import xelagurd.socialdating.client.ui.state.RequestStatus
import xelagurd.socialdating.client.ui.viewmodel.StatementsViewModel

@OptIn(ExperimentalCoroutinesApi::class)
class StatementsViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val context = mockk<Context>(relaxed = true)
    private val savedStateHandle = mockk<SavedStateHandle>()
    private val preferencesRepository = mockk<PreferencesRepository>()
    private val remoteStatementsRepository = mockk<RemoteStatementsRepository>()
    private val localStatementsRepository = mockk<LocalStatementsRepository>()
    private val remoteDefiningThemesRepository = mockk<RemoteDefiningThemesRepository>()
    private val commonLocalRepository = mockk<CommonLocalRepository>()

    private lateinit var viewModel: StatementsViewModel
    private lateinit var statementsFlow: MutableStateFlow<List<Statement>>
    private val statementsUiState
        get() = viewModel.uiState.value

    private val userId = Random.nextInt(1, Int.MAX_VALUE)
    private val categoryId = Random.nextInt(1, Int.MAX_VALUE)
    private val isOfflineModeFlow = flowOf(false)
    private val languageChangesFlow = MutableSharedFlow<AppLanguage>()

    private val statement = FakeData.mainStatement

    private val nextCursor = "a1b2c3d4:${"0".repeat(32)}"
    private val pageStatements = mockkList<StatementDto>()
    private val pageSize = pageStatements.size
    private val requestDelayMillis = 1000L

    @Before
    fun setup() {
        statementsFlow = MutableStateFlow(mockkList())

        mockGeneralMethods()
    }

    private fun initViewModel() {
        viewModel = StatementsViewModel(
            context,
            savedStateHandle,
            preferencesRepository,
            remoteStatementsRepository,
            localStatementsRepository,
            remoteDefiningThemesRepository,
            commonLocalRepository
        )
    }

    private fun TestScope.setupUiStateCollecting() {
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
            viewModel.uiState.collect {}
        }
    }

    @Test
    fun statementsViewModel_withInternet_successStatus() = runTest {
        mockDataWithInternet()

        initViewModel()
        setupUiStateCollecting()
        advanceUntilIdle()

        assertEquals(RequestStatus.SUCCESS, statementsUiState.dataRequestStatus)

        verify(exactly = 1) { localStatementsRepository.getStatements(any()) }
        coVerify(exactly = 1) { remoteDefiningThemesRepository.getDefiningThemes(any(), any()) }
        coVerify(exactly = 1) { remoteStatementsRepository.getStatements(any(), any(), null, any()) }
        coVerify(exactly = 1) {
            commonLocalRepository.updateStatementsScreenData(any(), any(), any(), 1, true)
        }
        confirmVerified(
            remoteStatementsRepository,
            localStatementsRepository,
            remoteDefiningThemesRepository,
            commonLocalRepository
        )
    }

    @Test
    fun statementsViewModel_withEmptyRemoteDefiningThemes_successStatus() = runTest {
        mockEmptyData()

        initViewModel()
        setupUiStateCollecting()
        advanceUntilIdle()

        assertEquals(RequestStatus.SUCCESS, statementsUiState.dataRequestStatus)

        verify(exactly = 1) { localStatementsRepository.getStatements(any()) }
        coVerify(exactly = 1) { remoteDefiningThemesRepository.getDefiningThemes(any(), any()) }
        confirmVerified(
            remoteStatementsRepository,
            localStatementsRepository,
            remoteDefiningThemesRepository,
            commonLocalRepository
        )
    }

    @Test
    fun statementsViewModel_withoutInternet_errorStatus() = runTest {
        mockDataWithoutInternet()

        initViewModel()
        setupUiStateCollecting()
        advanceUntilIdle()

        assertEquals(RequestStatus.ERROR(), statementsUiState.dataRequestStatus)

        verify(exactly = 1) { localStatementsRepository.getStatements(any()) }
        coVerify(exactly = 1) { remoteDefiningThemesRepository.getDefiningThemes(any(), any()) }
        confirmVerified(
            remoteStatementsRepository,
            localStatementsRepository,
            remoteDefiningThemesRepository,
            commonLocalRepository
        )
    }

    @Test
    fun statementsViewModel_refreshWithoutInternetAfterSuccess_errorStatus() = runTest {
        mockDataWithInternet()

        initViewModel()
        setupUiStateCollecting()
        advanceUntilIdle()

        mockDataWithoutInternet()

        viewModel.getStatements()
        advanceUntilIdle()

        assertEquals(RequestStatus.ERROR(), statementsUiState.dataRequestStatus)

        verify(exactly = 1) { localStatementsRepository.getStatements(any()) }
        coVerify(exactly = 2) { remoteDefiningThemesRepository.getDefiningThemes(any(), any()) }
        coVerify(exactly = 1) { remoteStatementsRepository.getStatements(any(), any(), any(), any()) }
        coVerify(exactly = 1) {
            commonLocalRepository.updateStatementsScreenData(any(), any(), any(), any(), any())
        }
        confirmVerified(
            remoteStatementsRepository,
            localStatementsRepository,
            remoteDefiningThemesRepository,
            commonLocalRepository
        )
    }

    @Test
    fun statementsViewModel_refreshWithInternetAfterError_successStatus() = runTest {
        mockDataWithoutInternet()

        initViewModel()
        setupUiStateCollecting()
        advanceUntilIdle()

        mockDataWithInternet()

        viewModel.getStatements()
        advanceUntilIdle()

        assertEquals(RequestStatus.SUCCESS, statementsUiState.dataRequestStatus)

        verify(exactly = 1) { localStatementsRepository.getStatements(any()) }
        coVerify(exactly = 2) { remoteDefiningThemesRepository.getDefiningThemes(any(), any()) }
        coVerify(exactly = 1) { remoteStatementsRepository.getStatements(any(), any(), any(), any()) }
        coVerify(exactly = 1) {
            commonLocalRepository.updateStatementsScreenData(any(), any(), any(), any(), any())
        }
        confirmVerified(
            remoteStatementsRepository,
            localStatementsRepository,
            remoteDefiningThemesRepository,
            commonLocalRepository
        )
    }

    @Test
    fun statementsViewModel_refreshWithInternetAfterSuccess_successStatus() = runTest {
        mockDataWithInternet()

        initViewModel()
        setupUiStateCollecting()
        advanceUntilIdle()

        viewModel.getStatements()
        advanceUntilIdle()

        assertEquals(RequestStatus.SUCCESS, statementsUiState.dataRequestStatus)

        verify(exactly = 1) { localStatementsRepository.getStatements(any()) }
        coVerify(exactly = 2) { remoteDefiningThemesRepository.getDefiningThemes(any(), any()) }
        coVerify(exactly = 2) { remoteStatementsRepository.getStatements(any(), any(), null, any()) }
        coVerify(exactly = 2) {
            commonLocalRepository.updateStatementsScreenData(any(), any(), any(), 1, true)
        }
        confirmVerified(
            remoteStatementsRepository,
            localStatementsRepository,
            remoteDefiningThemesRepository,
            commonLocalRepository
        )
    }

    @Test
    fun statementsViewModel_refreshWithoutInternetAfterError_errorStatus() = runTest {
        mockDataWithoutInternet()

        initViewModel()
        setupUiStateCollecting()
        advanceUntilIdle()

        viewModel.getStatements()
        advanceUntilIdle()

        assertEquals(RequestStatus.ERROR(), statementsUiState.dataRequestStatus)

        verify(exactly = 1) { localStatementsRepository.getStatements(any()) }
        coVerify(exactly = 2) { remoteDefiningThemesRepository.getDefiningThemes(any(), any()) }
        confirmVerified(
            remoteStatementsRepository,
            localStatementsRepository,
            remoteDefiningThemesRepository,
            commonLocalRepository
        )
    }

    @Test
    fun statementsViewModel_withEmptyRemoteStatements_successStatusAndLastPage() = runTest {
        mockEmptyStatements()

        initViewModel()
        setupUiStateCollecting()
        advanceUntilIdle()

        assertEquals(RequestStatus.SUCCESS, statementsUiState.dataRequestStatus)
        assertTrue(statementsUiState.isLastPage)

        verify(exactly = 1) { localStatementsRepository.getStatements(any()) }
        coVerify(exactly = 1) { remoteDefiningThemesRepository.getDefiningThemes(any(), any()) }
        coVerify(exactly = 1) { remoteStatementsRepository.getStatements(any(), any(), any(), any()) }
        confirmVerified(
            remoteStatementsRepository,
            localStatementsRepository,
            remoteDefiningThemesRepository,
            commonLocalRepository
        )
    }

    @Test
    fun statementsViewModel_loadingFirstPage_hidesStatementsOfPreviousSession() = runTest {
        mockDataWithInternet()

        initViewModel()
        setupUiStateCollecting()
        advanceUntilIdle()

        assertEquals(statementsFlow.value, statementsUiState.entities)

        coEvery { remoteDefiningThemesRepository.getDefiningThemes(any(), any()) } coAnswers {
            delay(requestDelayMillis)
            Response.success(mockkList(relaxed = true))
        }

        viewModel.getStatements()
        advanceTimeBy(requestDelayMillis / 2)

        assertEquals(RequestStatus.LOADING, statementsUiState.dataRequestStatus)
        assertEquals(listOf<Statement>(), statementsUiState.entities)

        advanceUntilIdle()

        assertEquals(RequestStatus.SUCCESS, statementsUiState.dataRequestStatus)
        assertEquals(statementsFlow.value, statementsUiState.entities)
    }

    @Test
    fun statementsViewModel_nextPageWithInternet_continuesPagingByCursor() = runTest {
        mockDataWithInternet(firstPageNextCursor = nextCursor)

        initViewModel()
        setupUiStateCollecting()
        advanceUntilIdle()

        assertFalse(statementsUiState.isLastPage)

        viewModel.getNextStatements()
        advanceUntilIdle()

        assertEquals(RequestStatus.SUCCESS, statementsUiState.nextPageRequestStatus)
        assertTrue(statementsUiState.isLastPage)

        verify(exactly = 1) { localStatementsRepository.getStatements(any()) }
        coVerify(exactly = 1) { remoteDefiningThemesRepository.getDefiningThemes(any(), any()) }
        coVerify(exactly = 1) { remoteStatementsRepository.getStatements(any(), any(), null, any()) }
        coVerify(exactly = 1) { remoteStatementsRepository.getStatements(any(), any(), nextCursor, any()) }
        coVerify(exactly = 1) {
            commonLocalRepository.updateStatementsScreenData(any(), any(), any(), 1, true)
        }
        coVerify(exactly = 1) {
            commonLocalRepository.updateStatementsScreenData(any(), any(), any(), pageSize + 1, false)
        }
        confirmVerified(
            remoteStatementsRepository,
            localStatementsRepository,
            remoteDefiningThemesRepository,
            commonLocalRepository
        )
    }

    @Test
    fun statementsViewModel_nextPageAfterLastPage_doesNothing() = runTest {
        mockDataWithInternet()

        initViewModel()
        setupUiStateCollecting()
        advanceUntilIdle()

        assertTrue(statementsUiState.isLastPage)

        viewModel.getNextStatements()
        advanceUntilIdle()

        verify(exactly = 1) { localStatementsRepository.getStatements(any()) }
        coVerify(exactly = 1) { remoteDefiningThemesRepository.getDefiningThemes(any(), any()) }
        coVerify(exactly = 1) { remoteStatementsRepository.getStatements(any(), any(), any(), any()) }
        coVerify(exactly = 1) {
            commonLocalRepository.updateStatementsScreenData(any(), any(), any(), any(), any())
        }
        confirmVerified(
            remoteStatementsRepository,
            localStatementsRepository,
            remoteDefiningThemesRepository,
            commonLocalRepository
        )
    }

    @Test
    fun statementsViewModel_nextPageAfterFailedFirstPage_doesNothing() = runTest {
        mockDataWithoutInternet()

        initViewModel()
        setupUiStateCollecting()
        advanceUntilIdle()

        viewModel.getNextStatements()
        advanceUntilIdle()

        assertEquals(RequestStatus.ERROR(), statementsUiState.dataRequestStatus)
        assertEquals(RequestStatus.UNDEFINED, statementsUiState.nextPageRequestStatus)

        verify(exactly = 1) { localStatementsRepository.getStatements(any()) }
        coVerify(exactly = 1) { remoteDefiningThemesRepository.getDefiningThemes(any(), any()) }
        confirmVerified(
            remoteStatementsRepository,
            localStatementsRepository,
            remoteDefiningThemesRepository,
            commonLocalRepository
        )
    }

    @Test
    fun statementsViewModel_nextPageWithoutInternet_errorStatusOfNextPageOnly() = runTest {
        mockDataWithInternet(firstPageNextCursor = nextCursor)

        initViewModel()
        setupUiStateCollecting()
        advanceUntilIdle()

        coEvery { remoteStatementsRepository.getStatements(any(), any(), any(), any()) } throws IOException()

        viewModel.getNextStatements()
        advanceUntilIdle()

        assertEquals(RequestStatus.SUCCESS, statementsUiState.dataRequestStatus)
        assertEquals(RequestStatus.ERROR(), statementsUiState.nextPageRequestStatus)
        assertFalse(statementsUiState.isLastPage)
    }

    @Test
    fun statementsViewModel_emptyNextPage_lastPage() = runTest {
        mockDataWithInternet(firstPageNextCursor = nextCursor)

        initViewModel()
        setupUiStateCollecting()
        advanceUntilIdle()

        coEvery { remoteStatementsRepository.getStatements(any(), any(), any(), any()) } returns
                Response.success(null)

        viewModel.getNextStatements()
        advanceUntilIdle()

        assertEquals(RequestStatus.SUCCESS, statementsUiState.nextPageRequestStatus)
        assertTrue(statementsUiState.isLastPage)
    }

    @Test
    fun statementsViewModel_statementReactionWithInternet_deletedStatement() = runTest {
        mockDataWithInternet()
        mockStatementReactionWithInternet()

        initViewModel()
        setupUiStateCollecting()
        advanceUntilIdle()

        viewModel.onStatementReactionClick(statement, StatementReactionType.FULL_MAINTAIN)
        advanceUntilIdle()

        coVerify(exactly = 1) { remoteStatementsRepository.processStatementReaction(any()) }
        coVerify(exactly = 1) { localStatementsRepository.deleteStatement(statement) }
    }

    @Test
    fun statementsViewModel_statementReactionWithoutInternet_notDeletedStatement() = runTest {
        mockDataWithInternet()
        mockStatementReactionWithoutInternet()

        initViewModel()
        setupUiStateCollecting()
        advanceUntilIdle()

        viewModel.onStatementReactionClick(statement, StatementReactionType.FULL_MAINTAIN)
        advanceUntilIdle()

        coVerify(exactly = 1) { remoteStatementsRepository.processStatementReaction(any()) }
        coVerify(exactly = 0) { localStatementsRepository.deleteStatement(any()) }
    }

    @Test
    fun statementsViewModel_appLanguageChange_reloadedStatements() = runTest {
        mockDataWithInternet()

        initViewModel()
        setupUiStateCollecting()
        advanceUntilIdle()

        languageChangesFlow.emit(AppLanguage.ENGLISH)
        advanceUntilIdle()

        assertEquals(RequestStatus.SUCCESS, statementsUiState.dataRequestStatus)

        coVerify(exactly = 2) { remoteStatementsRepository.getStatements(any(), any(), null, any()) }
    }

    private fun mockGeneralMethods() {
        every { savedStateHandle.get<Int>(StatementsDestination.userId) } returns userId
        every { savedStateHandle.get<Int>(StatementsDestination.categoryId) } returns categoryId
        every { preferencesRepository.isOfflineMode } returns isOfflineModeFlow
        every { preferencesRepository.languageChanges } returns languageChangesFlow
        every { localStatementsRepository.getStatements(any()) } returns statementsFlow
    }

    private fun mockDataWithInternet(firstPageNextCursor: String? = null) {
        coEvery { remoteDefiningThemesRepository.getDefiningThemes(any(), any()) } returns
                Response.success(mockkList(relaxed = true))
        coEvery { remoteStatementsRepository.getStatements(any(), any(), null, any()) } returns
                Response.success(PageDto(pageStatements, firstPageNextCursor))
        coEvery { remoteStatementsRepository.getStatements(any(), any(), firstPageNextCursor ?: "", any()) } returns
                Response.success(PageDto(pageStatements))

        coEvery {
            commonLocalRepository.updateStatementsScreenData(any(), any(), any(), any(), any())
        } just Runs
    }

    private fun mockStatementReactionWithInternet() {
        coEvery { remoteStatementsRepository.processStatementReaction(any()) } returns Response.success(mockk())
        coEvery { localStatementsRepository.deleteStatement(any()) } just Runs
    }

    private fun mockStatementReactionWithoutInternet() {
        coEvery { remoteStatementsRepository.processStatementReaction(any()) } throws IOException()
    }

    private fun mockEmptyData() {
        coEvery { remoteDefiningThemesRepository.getDefiningThemes(any(), any()) } returns Response.success(null)
    }

    private fun mockEmptyStatements() {
        coEvery { remoteDefiningThemesRepository.getDefiningThemes(any(), any()) } returns
                Response.success(mockkList(relaxed = true))
        coEvery { remoteStatementsRepository.getStatements(any(), any(), any(), any()) } returns
                Response.success(null)
    }

    private fun mockDataWithoutInternet() {
        coEvery { remoteDefiningThemesRepository.getDefiningThemes(any(), any()) } throws IOException()
    }
}
