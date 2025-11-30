package xelagurd.socialdating.client.test

import java.io.IOException
import kotlin.random.Random
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
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
import xelagurd.socialdating.client.TestUtils.mockkList
import xelagurd.socialdating.client.data.PreferencesRepository
import xelagurd.socialdating.client.data.fake.FakeData
import xelagurd.socialdating.client.data.local.repository.LocalDefiningThemesRepository
import xelagurd.socialdating.client.data.local.repository.LocalStatementsRepository
import xelagurd.socialdating.client.data.remote.ApiUtils.BAD_REQUEST
import xelagurd.socialdating.client.data.remote.repository.RemoteStatementsRepository
import xelagurd.socialdating.client.ui.navigation.StatementAddingDestination
import xelagurd.socialdating.client.ui.state.RequestStatus
import xelagurd.socialdating.client.ui.viewmodel.StatementAddingViewModel

@OptIn(ExperimentalCoroutinesApi::class)
class StatementAddingViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val context = mockk<Context>(relaxed = true)
    private val savedStateHandle = mockk<SavedStateHandle>()
    private val preferencesRepository = mockk<PreferencesRepository>()
    private val localDefiningThemesRepository = mockk<LocalDefiningThemesRepository>()
    private val remoteStatementsRepository = mockk<RemoteStatementsRepository>()
    private val localStatementsRepository = mockk<LocalStatementsRepository>()

    private lateinit var viewModel: StatementAddingViewModel
    private val statementAddingUiState
        get() = viewModel.uiState.value

    private val userId = Random.nextInt()
    private val categoryId = Random.nextInt()
    private val isOfflineModeFlow = flowOf(false)

    private val statementFormData = FakeData.statementFormData

    @Before
    fun setup() {
        mockGeneralMethods()
    }

    private fun initViewModel() {
        viewModel = StatementAddingViewModel(
            context,
            savedStateHandle,
            preferencesRepository,
            localDefiningThemesRepository,
            remoteStatementsRepository,
            localStatementsRepository
        )
        viewModel.updateUiState(statementFormData)
        viewModel.addStatement()
    }

    @Test
    fun statementAddingViewModel_statementAddingWithInternet() = runTest {
        mockDataWithInternet()

        initViewModel()
        advanceUntilIdle()

        assertEquals(RequestStatus.SUCCESS, statementAddingUiState.actionRequestStatus)

        verify(exactly = 1) { localDefiningThemesRepository.getDefiningThemes(any()) }
        coVerify(exactly = 1) { remoteStatementsRepository.addStatement(any()) }
        coVerify(exactly = 1) { localStatementsRepository.insertStatements(any()) }
        confirmVerified(localDefiningThemesRepository, localStatementsRepository, remoteStatementsRepository)
    }

    @Test
    fun statementAddingViewModel_statementAddingWithoutInternet() = runTest {
        mockDataWithoutInternet()

        initViewModel()
        advanceUntilIdle()

        assertEquals(RequestStatus.ERROR(), statementAddingUiState.actionRequestStatus)

        verify(exactly = 1) { localDefiningThemesRepository.getDefiningThemes(any()) }
        coVerify(exactly = 1) { remoteStatementsRepository.addStatement(any()) }
        confirmVerified(localDefiningThemesRepository, localStatementsRepository, remoteStatementsRepository)
    }

    @Test
    fun statementAddingViewModel_statementAddingWithWrongData() = runTest {
        mockWrongData()

        initViewModel()
        advanceUntilIdle()

        assertEquals(RequestStatus.FAILURE(BAD_REQUEST.toString()), statementAddingUiState.actionRequestStatus)

        verify(exactly = 1) { localDefiningThemesRepository.getDefiningThemes(any()) }
        coVerify(exactly = 1) { remoteStatementsRepository.addStatement(any()) }
        confirmVerified(localDefiningThemesRepository, localStatementsRepository, remoteStatementsRepository)
    }

    @Test
    fun statementAddingViewModel_retryStatementAddingWithInternet() = runTest {
        mockDataWithoutInternet()

        initViewModel()
        advanceUntilIdle()

        mockDataWithInternet()
        viewModel.addStatement()
        advanceUntilIdle()

        assertEquals(RequestStatus.SUCCESS, statementAddingUiState.actionRequestStatus)

        verify(exactly = 1) { localDefiningThemesRepository.getDefiningThemes(any()) }
        coVerify(exactly = 2) { remoteStatementsRepository.addStatement(any()) }
        coVerify(exactly = 1) { localStatementsRepository.insertStatements(any()) }
        confirmVerified(localDefiningThemesRepository, localStatementsRepository, remoteStatementsRepository)
    }

    @Test
    fun statementAddingViewModel_retryStatementAddingWithRightData() = runTest {
        mockWrongData()

        initViewModel()
        advanceUntilIdle()

        mockDataWithInternet()
        viewModel.addStatement()
        advanceUntilIdle()

        assertEquals(RequestStatus.SUCCESS, statementAddingUiState.actionRequestStatus)

        verify(exactly = 1) { localDefiningThemesRepository.getDefiningThemes(any()) }
        coVerify(exactly = 2) { remoteStatementsRepository.addStatement(any()) }
        coVerify(exactly = 1) { localStatementsRepository.insertStatements(any()) }
        confirmVerified(localDefiningThemesRepository, localStatementsRepository, remoteStatementsRepository)
    }

    private fun mockGeneralMethods() {
        every { savedStateHandle.get<Int>(StatementAddingDestination.userId) } returns userId
        every { savedStateHandle.get<Int>(StatementAddingDestination.categoryId) } returns categoryId
        every { preferencesRepository.isOfflineMode } returns isOfflineModeFlow
        every { localDefiningThemesRepository.getDefiningThemes(any()) } returns flowOf(mockkList())
    }

    private fun mockDataWithInternet() {
        coEvery { remoteStatementsRepository.addStatement(any()) } returns Response.success(mockk())
        coEvery { localStatementsRepository.insertStatements(any()) } just Runs
    }

    private fun mockWrongData() {
        coEvery { remoteStatementsRepository.addStatement(any()) } returns
                Response.error(BAD_REQUEST, BAD_REQUEST.toString().toResponseBody())
    }

    private fun mockDataWithoutInternet() {
        coEvery { remoteStatementsRepository.addStatement(any()) } throws IOException()
    }
}