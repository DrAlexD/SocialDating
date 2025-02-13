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
import xelagurd.socialdating.client.data.PreferencesRepository
import xelagurd.socialdating.client.data.fake.FakeDataSource
import xelagurd.socialdating.client.data.local.repository.LocalDefiningThemesRepository
import xelagurd.socialdating.client.data.local.repository.LocalStatementsRepository
import xelagurd.socialdating.client.data.model.DefiningTheme
import xelagurd.socialdating.client.data.model.Statement
import xelagurd.socialdating.client.data.remote.repository.RemoteDefiningThemesRepository
import xelagurd.socialdating.client.data.remote.repository.RemoteStatementsRepository
import xelagurd.socialdating.client.mergeListsAsSets
import xelagurd.socialdating.client.ui.state.RequestStatus
import xelagurd.socialdating.client.ui.viewmodel.StatementsViewModel

@OptIn(ExperimentalCoroutinesApi::class)
class StatementsViewModelTest {
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val context: Context = mockk()
    private val savedStateHandle: SavedStateHandle = mockk()
    private val remoteStatementsRepository: RemoteStatementsRepository = mockk()
    private val localStatementsRepository: LocalStatementsRepository = mockk()
    private val remoteDefiningThemesRepository: RemoteDefiningThemesRepository = mockk()
    private val localDefiningThemesRepository: LocalDefiningThemesRepository = mockk()
    private val preferencesRepository: PreferencesRepository = mockk()

    private lateinit var viewModel: StatementsViewModel
    private lateinit var definingThemesFlow: MutableStateFlow<List<DefiningTheme>>
    private lateinit var statementsFlow: MutableStateFlow<List<Statement>>
    private val statementsUiState
        get() = viewModel.uiState.value

    private val categoryId = 1

    private val localDefiningThemes = listOf(
        DefiningTheme(1, "", "", "", categoryId)
    )
    private val remoteDefiningThemes = listOf(
        DefiningTheme(1, "", "", "", categoryId),
        DefiningTheme(2, "", "", "", categoryId)
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

    private fun List<DefiningTheme>.toIds() = this.map { it.id }

    @Before
    fun setup() {
        definingThemesFlow = MutableStateFlow(localDefiningThemes)
        statementsFlow = MutableStateFlow(localStatements)

        mockGeneralMethods()
    }

    private fun initViewModel() {
        viewModel = StatementsViewModel(
            context,
            savedStateHandle,
            remoteStatementsRepository,
            localStatementsRepository,
            remoteDefiningThemesRepository,
            localDefiningThemesRepository,
            preferencesRepository
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
            mergeListsAsSets(localStatements, remoteStatements),
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
            localStatements,
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
            localStatements,
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
            mergeListsAsSets(localStatements, remoteStatements),
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
            mergeListsAsSets(localStatements, remoteStatements),
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
            mergeListsAsSets(localStatements, remoteStatements),
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
            localStatements,
            statementsUiState.entities
        )
    }

    private fun mockLocalStatements() {
        every { localStatementsRepository.getStatements(localDefiningThemes.toIds()) } returns statementsFlow
        every { localStatementsRepository.getStatements(remoteDefiningThemes.toIds()) } returns statementsFlow
    }

    private fun mockGeneralMethods() {
        every { preferencesRepository.currentUserId } returns flowOf(FakeDataSource.users[0].id)
        every { savedStateHandle.get<Int>("categoryId") } returns categoryId
        every { localDefiningThemesRepository.getDefiningThemes(categoryId) } returns definingThemesFlow
        mockLocalStatements()
    }

    private fun mockDataWithInternet() {
        coEvery { remoteDefiningThemesRepository.getDefiningThemes(listOf(categoryId)) } returns
                Response.success(remoteDefiningThemes)
        coEvery { remoteStatementsRepository.getStatements(remoteDefiningThemes.toIds()) } returns
                Response.success(remoteStatements)

        coEvery { localDefiningThemesRepository.insertDefiningThemes(remoteDefiningThemes) } answers {
            definingThemesFlow.value =
                mergeListsAsSets(definingThemesFlow.value, remoteDefiningThemes)
        }
        coEvery { localStatementsRepository.insertStatements(remoteStatements) } answers {
            statementsFlow.value = mergeListsAsSets(statementsFlow.value, remoteStatements)
        }
    }

    private fun mockEmptyData() {
        every { context.getString(any()) } returns ""
        coEvery { remoteDefiningThemesRepository.getDefiningThemes(listOf(categoryId)) } returns
                Response.error(404, "404".toResponseBody())
        coEvery { remoteStatementsRepository.getStatements(emptyList()) } returns
                Response.error(404, "404".toResponseBody())

        coEvery { localDefiningThemesRepository.insertDefiningThemes(emptyList()) } just Runs
    }

    private fun mockDataWithoutInternet() {
        every { context.getString(any()) } returns ""
        coEvery { remoteDefiningThemesRepository.getDefiningThemes(listOf(categoryId)) } throws IOException()

        // TODO: remove after implementing server
        every { localDefiningThemesRepository.getDefiningThemes() } returns flowOf(
            localDefiningThemes
        )
        every { localStatementsRepository.getStatements() } returns flowOf(localStatements)
    }
}