package xelagurd.socialdating.client.test

import java.io.IOException
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
import xelagurd.socialdating.client.data.local.repository.LocalDefiningThemesRepository
import xelagurd.socialdating.client.data.local.repository.LocalStatementsRepository
import xelagurd.socialdating.client.data.local.repository.LocalUserStatementsRepository
import xelagurd.socialdating.client.data.model.DefiningTheme
import xelagurd.socialdating.client.data.model.Statement
import xelagurd.socialdating.client.data.model.UserStatement
import xelagurd.socialdating.client.data.model.enums.StatementReactionType.FULL_MAINTAIN
import xelagurd.socialdating.client.data.remote.repository.RemoteDefiningThemesRepository
import xelagurd.socialdating.client.data.remote.repository.RemoteStatementsRepository
import xelagurd.socialdating.client.data.remote.repository.RemoteUserStatementsRepository
import xelagurd.socialdating.client.mergeListsAsSets
import xelagurd.socialdating.client.ui.state.RequestStatus
import xelagurd.socialdating.client.ui.viewmodel.StatementsViewModel

@OptIn(ExperimentalCoroutinesApi::class)
class StatementsViewModelTest {
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val context: Context = mockk()
    private val savedStateHandle: SavedStateHandle = mockk()
    private val remoteUserStatementsRepository: RemoteUserStatementsRepository = mockk()
    private val localUserStatementsRepository: LocalUserStatementsRepository = mockk()
    private val remoteStatementsRepository: RemoteStatementsRepository = mockk()
    private val localStatementsRepository: LocalStatementsRepository = mockk()
    private val remoteDefiningThemesRepository: RemoteDefiningThemesRepository = mockk()
    private val localDefiningThemesRepository: LocalDefiningThemesRepository = mockk()

    private lateinit var viewModel: StatementsViewModel
    private lateinit var definingThemesFlow: MutableStateFlow<List<DefiningTheme>>
    private lateinit var userStatementsFlow: MutableStateFlow<List<UserStatement>>
    private lateinit var statementsFlow: MutableStateFlow<List<Statement>>
    private val statementsUiState
        get() = viewModel.uiState.value

    private val userId = 1
    private val categoryId = 1

    private val localDefiningThemes = listOf(
        DefiningTheme(1, "", "", "", categoryId)
    )
    private val remoteDefiningThemes = listOf(
        DefiningTheme(1, "", "", "", categoryId),
        DefiningTheme(2, "", "", "", categoryId)
    )
    private val localUserStatements = listOf(
        UserStatement(1, FULL_MAINTAIN, 1, 1)
    )
    private val remoteUserStatements = listOf(
        UserStatement(1, FULL_MAINTAIN, 1, 1),
        UserStatement(2, FULL_MAINTAIN, 1, 2)
    )
    private val localStatements = listOf(
        Statement(1, "", true, 1, 1),
        Statement(2, "", true, 1, 1)
    )
    private val remoteStatements = listOf(
        Statement(1, "", true, 1, 1),
        Statement(2, "", true, 1, 1),
        Statement(3, "", true, 1, 1),
        Statement(4, "", true, 2, 1),
        Statement(5, "", true, 2, 1),
    )
    private val chosenLocalStatements = localStatements.filter { it.id !in localUserStatements.map { it.statementId } }
    private val chosenRemoteStatements = remoteStatements.filter { it.id !in remoteUserStatements.map { it.statementId } }

    private fun List<DefiningTheme>.toIds() = this.map { it.id }

    @Before
    fun setup() {
        definingThemesFlow = MutableStateFlow(localDefiningThemes)
        userStatementsFlow = MutableStateFlow(localUserStatements)
        statementsFlow = MutableStateFlow(chosenLocalStatements)

        mockGeneralMethods()
    }

    private fun initViewModel() {
        viewModel = StatementsViewModel(
            context,
            savedStateHandle,
            remoteUserStatementsRepository,
            localUserStatementsRepository,
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
        assertEquals(
            mergeListsAsSets(chosenLocalStatements, chosenRemoteStatements),
            statementsUiState.entities
        )
    }

    @Test
    fun statementsViewModel_checkStateWithEmptyData() = runTest {
        mockEmptyData()

        initViewModel()
        setupUiStateCollecting()
        advanceUntilIdle()

        assertEquals(RequestStatus.FAILURE(), statementsUiState.dataRequestStatus)
        assertEquals(
            chosenLocalStatements,
            statementsUiState.entities
        )
    }

    @Test
    fun statementsViewModel_checkStateWithoutInternet() = runTest {
        mockDataWithoutInternet()

        initViewModel()
        setupUiStateCollecting()
        advanceUntilIdle()

        assertEquals(RequestStatus.ERROR(), statementsUiState.dataRequestStatus)
        assertEquals(
            chosenLocalStatements,
            statementsUiState.entities
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
        assertEquals(
            mergeListsAsSets(chosenLocalStatements, chosenRemoteStatements),
            statementsUiState.entities
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
        assertEquals(
            mergeListsAsSets(chosenLocalStatements, chosenRemoteStatements),
            statementsUiState.entities
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
        assertEquals(
            mergeListsAsSets(chosenLocalStatements, chosenRemoteStatements),
            statementsUiState.entities
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
        assertEquals(
            chosenLocalStatements,
            statementsUiState.entities
        )
    }

    private fun mockGeneralMethods() {
        every { savedStateHandle.get<Int>("userId") } returns userId
        every { savedStateHandle.get<Int>("categoryId") } returns categoryId
        every { localStatementsRepository.getStatements(userId, categoryId) } returns statementsFlow
    }

    private fun mockDataWithInternet() {
        coEvery { remoteDefiningThemesRepository.getDefiningThemes(categoryId) } returns
                Response.success(remoteDefiningThemes)
        coEvery { remoteStatementsRepository.getStatements(userId, remoteDefiningThemes.toIds()) } returns
                Response.success(chosenRemoteStatements)
        coEvery { remoteUserStatementsRepository.getUserStatements(userId, remoteDefiningThemes.toIds()) } returns
                Response.success(remoteUserStatements)

        coEvery { localDefiningThemesRepository.insertDefiningThemes(remoteDefiningThemes) } answers {
            definingThemesFlow.value =
                mergeListsAsSets(definingThemesFlow.value, remoteDefiningThemes)
        }
        coEvery { localStatementsRepository.insertStatements(chosenRemoteStatements) } answers {
            statementsFlow.value = mergeListsAsSets(statementsFlow.value, chosenRemoteStatements)
        }
        coEvery { localUserStatementsRepository.insertUserStatements(remoteUserStatements) } answers {
            userStatementsFlow.value = mergeListsAsSets(userStatementsFlow.value, remoteUserStatements)
        }
    }

    private fun mockEmptyData() {
        every { context.getString(any()) } returns ""
        coEvery { remoteDefiningThemesRepository.getDefiningThemes(categoryId) } returns
                Response.error(404, "404".toResponseBody())
        coEvery { remoteStatementsRepository.getStatements(userId, emptyList()) } returns
                Response.error(404, "404".toResponseBody())
        coEvery { remoteUserStatementsRepository.getUserStatements(userId, emptyList()) } returns
                Response.error(404, "404".toResponseBody())

        coEvery { localDefiningThemesRepository.insertDefiningThemes(emptyList()) } just Runs
    }

    private fun mockDataWithoutInternet() {
        every { context.getString(any()) } returns ""
        coEvery { remoteDefiningThemesRepository.getDefiningThemes(categoryId) } throws IOException()

        // FixMe: remove after adding server hosting
        every { localDefiningThemesRepository.getDefiningThemes() } returns flowOf(
            localDefiningThemes
        )
        every { localStatementsRepository.getStatements() } returns flowOf(chosenLocalStatements)
        every { localUserStatementsRepository.getUserStatements() } returns flowOf(localUserStatements)
    }
}