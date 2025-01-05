package xelagurd.socialdating.tests

import java.io.IOException
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
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
import xelagurd.socialdating.data.fake.FakeDataSource
import xelagurd.socialdating.data.local.repository.LocalDefiningThemesRepository
import xelagurd.socialdating.data.local.repository.LocalStatementsRepository
import xelagurd.socialdating.data.local.repository.LocalUsersRepository
import xelagurd.socialdating.data.model.DefiningTheme
import xelagurd.socialdating.data.model.Statement
import xelagurd.socialdating.data.remote.repository.RemoteDefiningThemesRepository
import xelagurd.socialdating.data.remote.repository.RemoteStatementsRepository
import xelagurd.socialdating.mergeListsAsSets
import xelagurd.socialdating.ui.state.InternetStatus
import xelagurd.socialdating.ui.viewmodel.StatementsViewModel

@OptIn(ExperimentalCoroutinesApi::class)
class StatementsViewModelTest {
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val savedStateHandle: SavedStateHandle = mockk()
    private val remoteStatementsRepository: RemoteStatementsRepository = mockk()
    private val localStatementsRepository: LocalStatementsRepository = mockk()
    private val remoteDefiningThemesRepository: RemoteDefiningThemesRepository = mockk()
    private val localDefiningThemesRepository: LocalDefiningThemesRepository = mockk()
    private val localUsersRepository: LocalUsersRepository =
        mockk() // TODO: Remove after adding login screen

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
        Statement(1, "", 1, 1),
        Statement(2, "", 1, 1)
    )
    private val remoteStatements = listOf(
        Statement(1, "", 1, 1),
        Statement(2, "", 1, 1),
        Statement(3, "", 1, 1),
        Statement(4, "", 2, 1),
        Statement(5, "", 2, 1),
    )

    private fun List<DefiningTheme>.toIds() = this.map { it.id }
    private fun MutableStateFlow<List<DefiningTheme>>.toIds() = this.value.map { it.id }

    @Before
    fun setup() {
        definingThemesFlow = MutableStateFlow(localDefiningThemes)
        statementsFlow = MutableStateFlow(localStatements)

        mockGeneralMethods()

        viewModel = StatementsViewModel(
            savedStateHandle,
            remoteStatementsRepository,
            localStatementsRepository,
            remoteDefiningThemesRepository,
            localDefiningThemesRepository,
            localUsersRepository
        )
    }

    private fun TestScope.setupUiStateCollecting() {
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
            viewModel.uiState.collect {}
        }
    }

    @Test
    fun statementsViewModel_checkDataWithInternet() = runTest {
        setupUiStateCollecting()

        mockDataWithInternet()
        advanceUntilIdle()

        assertEquals(InternetStatus.ONLINE, statementsUiState.internetStatus)
        assertEquals(
            mergeListsAsSets(localStatements, remoteStatements),
            statementsUiState.statements
        )
    }

    @Test
    fun statementsViewModel_checkDataWithoutInternet() = runTest {
        setupUiStateCollecting()

        mockDataWithoutInternet()
        advanceUntilIdle()

        assertEquals(InternetStatus.OFFLINE, statementsUiState.internetStatus)
        assertEquals(
            mergeListsAsSets(localStatements, FakeDataSource.statements),
            statementsUiState.statements
        )
    }

    @Test
    fun statementsViewModel_checkRefreshedOnlineDataWithoutInternet() = runTest {
        setupUiStateCollecting()

        mockDataWithInternet()
        advanceUntilIdle()

        mockDataWithoutInternet()
        viewModel.getStatements()
        advanceUntilIdle()

        assertEquals(InternetStatus.OFFLINE, statementsUiState.internetStatus)
        assertEquals(
            mergeListsAsSets(localStatements, remoteStatements, FakeDataSource.statements),
            statementsUiState.statements
        )
    }

    @Test
    fun statementsViewModel_checkRefreshedOfflineDataWithInternet() = runTest {
        setupUiStateCollecting()

        mockDataWithoutInternet()
        advanceUntilIdle()

        mockDataWithInternet()
        viewModel.getStatements()
        advanceUntilIdle()

        assertEquals(InternetStatus.ONLINE, statementsUiState.internetStatus)
        assertEquals(
            mergeListsAsSets(localStatements, FakeDataSource.statements, remoteStatements),
            statementsUiState.statements
        )
    }

    @Test
    fun statementsViewModel_checkRefreshedOnlineDataWithInternet() = runTest {
        setupUiStateCollecting()

        mockDataWithInternet()
        advanceUntilIdle()

        viewModel.getStatements()
        advanceUntilIdle()

        assertEquals(InternetStatus.ONLINE, statementsUiState.internetStatus)
        assertEquals(
            mergeListsAsSets(localStatements, remoteStatements),
            statementsUiState.statements
        )
    }

    @Test
    fun statementsViewModel_checkRefreshedOfflineDataWithoutInternet() = runTest {
        setupUiStateCollecting()

        mockDataWithoutInternet()
        advanceUntilIdle()

        viewModel.getStatements()
        advanceUntilIdle()

        assertEquals(InternetStatus.OFFLINE, statementsUiState.internetStatus)
        assertEquals(
            mergeListsAsSets(localStatements, FakeDataSource.statements),
            statementsUiState.statements
        )
    }

    private fun mockLocalStatements() {
        every { localStatementsRepository.getStatements(localDefiningThemes.toIds()) } returns statementsFlow
        every { localStatementsRepository.getStatements(remoteDefiningThemes.toIds()) } returns statementsFlow
        every { localStatementsRepository.getStatements(FakeDataSource.definingThemes.toIds()) } returns statementsFlow
    }

    private fun mockGeneralMethods() {
        every { savedStateHandle.get<Int>("categoryId") } returns categoryId
        every { localDefiningThemesRepository.getDefiningThemes(categoryId) } returns definingThemesFlow
        mockLocalStatements()
        coEvery { localUsersRepository.insertUser(FakeDataSource.users[0]) } just Runs
    }

    private fun mockDataWithInternet() {
        coEvery { remoteDefiningThemesRepository.getDefiningThemes(categoryId) } returns remoteDefiningThemes
        coEvery { remoteStatementsRepository.getStatements(remoteDefiningThemes.toIds()) } returns remoteStatements

        coEvery { localDefiningThemesRepository.insertDefiningThemes(remoteDefiningThemes) } answers {
            definingThemesFlow.value =
                mergeListsAsSets(definingThemesFlow.value, remoteDefiningThemes)
        }
        coEvery { localStatementsRepository.insertStatements(remoteStatements) } answers {
            statementsFlow.value = mergeListsAsSets(statementsFlow.value, remoteStatements)
        }
    }

    private fun mockDataWithoutInternet() {
        coEvery { remoteDefiningThemesRepository.getDefiningThemes(categoryId) } throws IOException()

        coEvery { localDefiningThemesRepository.insertDefiningThemes(FakeDataSource.definingThemes) } answers {
            definingThemesFlow.value =
                mergeListsAsSets(definingThemesFlow.value, FakeDataSource.definingThemes)
        }
        coEvery { localStatementsRepository.insertStatements(FakeDataSource.statements) } answers {
            statementsFlow.value = mergeListsAsSets(statementsFlow.value, FakeDataSource.statements)
        }
    }
}