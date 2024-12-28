package xelagurd.socialdating.tests

import java.io.IOException
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
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
import xelagurd.socialdating.data.network.repository.RemoteDefiningThemesRepository
import xelagurd.socialdating.data.network.repository.RemoteStatementsRepository
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
    private val localUsersRepository: LocalUsersRepository = mockk() // TODO: Remove after adding login screen

    private lateinit var viewModel: StatementsViewModel
    private lateinit var definingThemesFlow: MutableStateFlow<List<DefiningTheme>>
    private lateinit var statementsFlow: MutableStateFlow<List<Statement>>

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

    @Test
    fun statementsViewModel_checkInitialState() = runTest {
        mockDataWithInternet()
        mockLocalStatements()

        assertEquals(InternetStatus.LOADING, viewModel.internetStatus)
        assertEquals(
            localStatements,
            localStatementsRepository.getStatements(definingThemesFlow.toIds()).first()
        )
    }

    @Test
    fun statementsViewModel_checkDataWithInternet() = runTest {
        mockDataWithInternet()
        advanceUntilIdle()
        mockLocalStatements()

        assertEquals(InternetStatus.ONLINE, viewModel.internetStatus)
        assertEquals(
            mergeListsAsSets(localStatements, remoteStatements),
            localStatementsRepository.getStatements(definingThemesFlow.toIds()).first()
        )
    }

    @Test
    fun statementsViewModel_checkDataWithoutInternet() = runTest {
        mockDataWithoutInternet()
        advanceUntilIdle()
        mockLocalStatements()

        assertEquals(InternetStatus.OFFLINE, viewModel.internetStatus)
        assertEquals(
            mergeListsAsSets(localStatements, FakeDataSource.statements),
            localStatementsRepository.getStatements(definingThemesFlow.toIds()).first()
        )
    }

    @Test
    fun statementsViewModel_checkRefreshedOnlineDataWithoutInternet() = runTest {
        mockDataWithInternet()
        advanceUntilIdle()

        mockDataWithoutInternet()
        viewModel.getStatements()
        advanceUntilIdle()
        mockLocalStatements()

        assertEquals(InternetStatus.OFFLINE, viewModel.internetStatus)
        assertEquals(
            mergeListsAsSets(localStatements, remoteStatements, FakeDataSource.statements),
            localStatementsRepository.getStatements(definingThemesFlow.toIds()).first()
        )
    }

    @Test
    fun statementsViewModel_checkRefreshedOfflineDataWithInternet() = runTest {
        mockDataWithoutInternet()
        advanceUntilIdle()

        mockDataWithInternet()
        viewModel.getStatements()
        advanceUntilIdle()
        mockLocalStatements()

        assertEquals(InternetStatus.ONLINE, viewModel.internetStatus)
        assertEquals(
            mergeListsAsSets(localStatements, FakeDataSource.statements, remoteStatements),
            localStatementsRepository.getStatements(definingThemesFlow.toIds()).first()
        )
    }

    @Test
    fun statementsViewModel_checkRefreshedOnlineDataWithInternet() = runTest {
        mockDataWithInternet()
        advanceUntilIdle()

        viewModel.getStatements()
        advanceUntilIdle()
        mockLocalStatements()

        assertEquals(InternetStatus.ONLINE, viewModel.internetStatus)
        assertEquals(
            mergeListsAsSets(localStatements, remoteStatements),
            localStatementsRepository.getStatements(definingThemesFlow.toIds()).first()
        )
    }

    @Test
    fun statementsViewModel_checkRefreshedOfflineDataWithoutInternet() = runTest {
        mockDataWithoutInternet()
        advanceUntilIdle()

        viewModel.getStatements()
        advanceUntilIdle()
        mockLocalStatements()

        assertEquals(InternetStatus.OFFLINE, viewModel.internetStatus)
        assertEquals(
            mergeListsAsSets(localStatements, FakeDataSource.statements),
            localStatementsRepository.getStatements(definingThemesFlow.toIds()).first()
        )
    }

    private fun mockLocalStatements() {
        every { localStatementsRepository.getStatements(definingThemesFlow.toIds()) } returns statementsFlow
    }

    private fun mockGeneralMethods() {
        every { savedStateHandle.get<Int>("categoryId") } returns categoryId
        every { localDefiningThemesRepository.getDefiningThemes(categoryId) } returns definingThemesFlow
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