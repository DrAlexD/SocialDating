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
import xelagurd.socialdating.client.data.fake.FakeData
import xelagurd.socialdating.client.data.local.repository.LocalDefiningThemesRepository
import xelagurd.socialdating.client.data.local.repository.LocalStatementsRepository
import xelagurd.socialdating.client.data.remote.BAD_REQUEST
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

    private lateinit var viewModel: StatementAddingViewModel
    private val statementAddingUiState
        get() = viewModel.uiState.value

    private val userId = 1
    private val categoryId = 1

    private val localDefiningThemes = FakeData.definingThemes.take(3)
    private val statementFormData = FakeData.statementFormData
    private val statement = FakeData.statements[0]

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
            localStatementsRepository
        )
        viewModel.updateUiState(statementFormData)
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

        assertEquals(RequestStatus.FAILURE(BAD_REQUEST.toString()), statementAddingUiState.actionRequestStatus)
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
        every { savedStateHandle.get<Int>("userId") } returns userId
        every { savedStateHandle.get<Int>("categoryId") } returns categoryId
        every { localDefiningThemesRepository.getDefiningThemes(categoryId) } returns flowOf(localDefiningThemes)
    }

    private fun mockDataWithInternet() {
        coEvery {
            remoteStatementsRepository.addStatement(statementFormData.toStatementDetails())
        } returns Response.success(statement)
        coEvery { localStatementsRepository.insertStatements(listOf(statement)) } just Runs
    }

    private fun mockWrongData() {
        every { context.getString(any()) } returns ""
        coEvery {
            remoteStatementsRepository.addStatement(statementFormData.toStatementDetails())
        } returns Response.error(BAD_REQUEST, BAD_REQUEST.toString().toResponseBody())
    }

    private fun mockDataWithoutInternet() {
        every { context.getString(any()) } returns ""
        coEvery {
            remoteStatementsRepository.addStatement(statementFormData.toStatementDetails())
        } throws IOException()

        // FixMe: remove after adding server hosting
        every { localStatementsRepository.getStatements() } returns flowOf(FakeData.statements)
        coEvery { localStatementsRepository.insertStatements(listOf(FakeData.newStatement)) } just Runs
    }
}