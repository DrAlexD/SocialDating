package xelagurd.socialdating.client.test

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
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import retrofit2.Response
import xelagurd.socialdating.client.MainDispatcherRule
import xelagurd.socialdating.client.data.PreferencesRepository
import xelagurd.socialdating.client.data.fake.FakeDataSource
import xelagurd.socialdating.client.data.local.repository.LocalDefiningThemesRepository
import xelagurd.socialdating.client.data.local.repository.LocalStatementsRepository
import xelagurd.socialdating.client.data.model.DefiningTheme
import xelagurd.socialdating.client.data.model.Statement
import xelagurd.socialdating.client.data.model.details.StatementDetails
import xelagurd.socialdating.client.data.remote.repository.RemoteStatementsRepository
import xelagurd.socialdating.client.ui.state.RequestStatus
import xelagurd.socialdating.client.ui.viewmodel.StatementAddingViewModel

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

        // FixMe: Change to ERROR after implementing server
        assertEquals(RequestStatus.SUCCESS, statementAddingUiState.actionRequestStatus)
    }

    @Test
    fun statementAddingViewModel_statementAddingWithWrongData() = runTest {
        mockWrongData()

        initViewModel()
        advanceUntilIdle()

        assertEquals(RequestStatus.FAILURE("400"), statementAddingUiState.actionRequestStatus)
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
        coEvery { remoteStatementsRepository.addStatement(statementDetails) } returns
                Response.success(statement)
        coEvery { localStatementsRepository.insertStatement(statement) } just Runs
    }

    private fun mockWrongData() {
        every { context.getString(any()) } returns ""
        coEvery { remoteStatementsRepository.addStatement(statementDetails) } returns
                Response.error(400, "400".toResponseBody())
    }

    private fun mockDataWithoutInternet() {
        every { context.getString(any()) } returns ""
        coEvery { remoteStatementsRepository.addStatement(statementDetails) } throws IOException()

        // FixMe: remove after implementing server
        every { localStatementsRepository.getStatements() } returns flowOf(listOf(FakeDataSource.newStatement))
    }
}