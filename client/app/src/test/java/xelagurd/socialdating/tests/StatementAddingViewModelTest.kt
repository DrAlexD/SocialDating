package xelagurd.socialdating.tests

import java.io.IOException
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import android.content.Context
import androidx.lifecycle.SavedStateHandle
import io.mockk.Runs
import io.mockk.coEvery
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import junit.framework.TestCase.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import xelagurd.socialdating.MainDispatcherRule
import xelagurd.socialdating.PreferencesRepository
import xelagurd.socialdating.data.fake.FakeDataSource
import xelagurd.socialdating.data.local.repository.LocalDefiningThemesRepository
import xelagurd.socialdating.data.local.repository.LocalStatementsRepository
import xelagurd.socialdating.data.model.DefiningTheme
import xelagurd.socialdating.data.model.Statement
import xelagurd.socialdating.data.model.details.StatementDetails
import xelagurd.socialdating.data.remote.repository.RemoteStatementsRepository
import xelagurd.socialdating.ui.state.RequestStatus
import xelagurd.socialdating.ui.viewmodel.StatementAddingViewModel

@OptIn(ExperimentalCoroutinesApi::class)
class StatementAddingViewModelTest {
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val context: Context = mockk()
    private val savedStateHandle: SavedStateHandle = mockk()
    private val localDefiningThemesRepository: LocalDefiningThemesRepository = mockk()
    private val remoteStatementsRepository: RemoteStatementsRepository = mockk()
    private val localStatementsRepository: LocalStatementsRepository = mockk()
    private val preferencesRepository: PreferencesRepository = mockk()

    private lateinit var viewModel: StatementAddingViewModel
    private val statementAddingUiState
        get() = viewModel.uiState.value

    private val categoryId = 1

    private val localDefiningThemes = listOf(DefiningTheme(1, "", "", "", categoryId))

    private val statementDetails = StatementDetails("", false, 1, 1)
    private val statement = Statement(22, "", false, 1, 1)

    @Before
    fun setup() {
        mockGeneralMethods()
    }

    private fun initViewModel() {
        viewModel = StatementAddingViewModel(
            context,
            savedStateHandle,
            localDefiningThemesRepository,
            remoteStatementsRepository,
            localStatementsRepository,
            preferencesRepository
        )
        viewModel.updateUiState(statementDetails)
        viewModel.statementAdding()
    }

    @Test
    fun statementAddingViewModel_statementAddingWithInternet() = runTest {
        mockDataWithInternet()

        initViewModel()
        advanceUntilIdle()

        assertEquals(RequestStatus.SUCCESS, statementAddingUiState.actionRequestStatus)
    }

    @Test
    fun statementAddingViewModel_statementAddingWithoutInternet() = runTest {
        mockDataWithoutInternet()

        initViewModel()
        advanceUntilIdle()

        // TODO: Change to ERROR after implementing server
        assertEquals(RequestStatus.SUCCESS, statementAddingUiState.actionRequestStatus)
    }

    @Test
    fun statementAddingViewModel_statementAddingWithWrongData() = runTest {
        mockWrongData()

        initViewModel()
        advanceUntilIdle()

        assertEquals(RequestStatus.FAILURE(), statementAddingUiState.actionRequestStatus)
    }

    @Test
    fun statementAddingViewModel_retryStatementAddingWithInternet() = runTest {
        mockDataWithoutInternet()

        initViewModel()
        advanceUntilIdle()

        mockDataWithInternet()
        viewModel.statementAdding()
        advanceUntilIdle()

        assertEquals(RequestStatus.SUCCESS, statementAddingUiState.actionRequestStatus)
    }

    @Test
    fun statementAddingViewModel_retryStatementAddingWithRightData() = runTest {
        mockWrongData()

        initViewModel()
        advanceUntilIdle()

        mockDataWithInternet()
        viewModel.statementAdding()
        advanceUntilIdle()

        assertEquals(RequestStatus.SUCCESS, statementAddingUiState.actionRequestStatus)
    }

    private fun mockGeneralMethods() {
        every { preferencesRepository.currentUserId } returns flowOf(FakeDataSource.users[0].id)
        every { savedStateHandle.get<Int>("categoryId") } returns categoryId
        every { localDefiningThemesRepository.getDefiningThemes(categoryId) } returns
                flowOf(localDefiningThemes)
    }

    private fun mockDataWithInternet() {
        coEvery { remoteStatementsRepository.statementAdding(statementDetails) } returns statement
        coEvery { localStatementsRepository.insertStatement(statement) } just Runs
    }

    private fun mockWrongData() {
        every { context.getString(any()) } returns ""
        coEvery { remoteStatementsRepository.statementAdding(statementDetails) } returns null
    }

    private fun mockDataWithoutInternet() {
        every { context.getString(any()) } returns ""
        coEvery { remoteStatementsRepository.statementAdding(statementDetails) } throws IOException()

        every { localStatementsRepository.getStatements() } returns flowOf(listOf(FakeDataSource.newStatement)) // TODO: remove after implementing server
    }
}