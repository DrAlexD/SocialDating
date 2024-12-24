package xelagurd.socialdating

import java.io.IOException
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import androidx.lifecycle.SavedStateHandle
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import junit.framework.TestCase.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import xelagurd.socialdating.data.fake.FakeDataSource
import xelagurd.socialdating.data.local.repository.LocalStatementsRepository
import xelagurd.socialdating.data.model.Statement
import xelagurd.socialdating.data.network.repository.RemoteStatementsRepository
import xelagurd.socialdating.ui.state.InternetStatus
import xelagurd.socialdating.ui.viewmodel.StatementsViewModel

@OptIn(ExperimentalCoroutinesApi::class)
class StatementsViewModelTest {
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val savedStateHandle: SavedStateHandle = mockk()
    private val remoteRepository: RemoteStatementsRepository = mockk()
    private val localRepository: LocalStatementsRepository = mockk()

    private lateinit var viewModel: StatementsViewModel
    private lateinit var state: MutableStateFlow<List<Statement>>

    private val categoryId = 1
    private val localStatements = listOf(Statement(1, "Database Statement", categoryId))
    private val remoteStatements = listOf(Statement(2, "Remote Statement", categoryId))

    @Before
    fun setup() {
        state = MutableStateFlow(localStatements)
        every { localRepository.getStatements(categoryId) } returns state
        every { savedStateHandle.get<Int>("categoryId") } returns categoryId

        viewModel = StatementsViewModel(savedStateHandle, remoteRepository, localRepository)

        assertEquals(InternetStatus.LOADING, viewModel.internetStatus)
    }

    @Test
    fun statementsViewModel_CheckDataWithInternet() = runTest {
        assertEquals(localStatements, localRepository.getStatements(categoryId).first())

        coEvery { remoteRepository.getStatements(categoryId) } returns remoteStatements
        coEvery { localRepository.insertStatements(remoteStatements) } answers {
            state.value = (state.value.toSet() + remoteStatements).toList()
        }

        advanceUntilIdle()

        assertEquals(InternetStatus.ONLINE, viewModel.internetStatus)
        assertEquals(
            localStatements + remoteStatements,
            localRepository.getStatements(categoryId).first()
        )
    }

    @Test
    fun statementsViewModel_CheckDataWithoutInternet() = runTest {
        assertEquals(localStatements, localRepository.getStatements(categoryId).first())

        coEvery { remoteRepository.getStatements(categoryId) } throws IOException()
        coEvery { localRepository.insertStatements(FakeDataSource.statements) } answers {
            state.value = (state.value.toSet() + FakeDataSource.statements).toList()
        }

        advanceUntilIdle()

        assertEquals(InternetStatus.OFFLINE, viewModel.internetStatus)
        assertEquals(
            localStatements + FakeDataSource.statements,
            localRepository.getStatements(categoryId).first()
        )
    }

    @Test
    fun statementsViewModel_CheckRefreshedOnlineDataWithoutInternet() = runTest {
        assertEquals(localStatements, localRepository.getStatements(categoryId).first())

        coEvery { remoteRepository.getStatements(categoryId) } returns remoteStatements
        coEvery { localRepository.insertStatements(remoteStatements) } answers {
            state.value = (state.value.toSet() + remoteStatements).toList()
        }

        advanceUntilIdle()

        assertEquals(InternetStatus.ONLINE, viewModel.internetStatus)
        assertEquals(
            localStatements + remoteStatements,
            localRepository.getStatements(categoryId).first()
        )

        coEvery { remoteRepository.getStatements(categoryId) } throws IOException()
        coEvery { localRepository.insertStatements(FakeDataSource.statements) } answers {
            state.value = (state.value.toSet() + FakeDataSource.statements).toList()
        }

        viewModel.getStatements()

        advanceUntilIdle()

        assertEquals(InternetStatus.OFFLINE, viewModel.internetStatus)
        assertEquals(
            localStatements + remoteStatements + FakeDataSource.statements,
            localRepository.getStatements(categoryId).first()
        )
    }

    @Test
    fun statementsViewModel_CheckRefreshedOfflineDataWithInternet() = runTest {
        assertEquals(localStatements, localRepository.getStatements(categoryId).first())

        coEvery { remoteRepository.getStatements(categoryId) } throws IOException()
        coEvery { localRepository.insertStatements(FakeDataSource.statements) } answers {
            state.value = (state.value.toSet() + FakeDataSource.statements).toList()
        }

        advanceUntilIdle()

        assertEquals(InternetStatus.OFFLINE, viewModel.internetStatus)
        assertEquals(
            localStatements + FakeDataSource.statements,
            localRepository.getStatements(categoryId).first()
        )

        coEvery { remoteRepository.getStatements(categoryId) } returns remoteStatements
        coEvery { localRepository.insertStatements(remoteStatements) } answers {
            state.value = (state.value.toSet() + remoteStatements).toList()
        }

        viewModel.getStatements()

        advanceUntilIdle()

        assertEquals(InternetStatus.ONLINE, viewModel.internetStatus)
        assertEquals(
            localStatements + FakeDataSource.statements + remoteStatements,
            localRepository.getStatements(categoryId).first()
        )
    }

    @Test
    fun statementsViewModel_CheckRefreshedOnlineDataWithInternet() = runTest {
        assertEquals(localStatements, localRepository.getStatements(categoryId).first())

        coEvery { remoteRepository.getStatements(categoryId) } returns remoteStatements
        coEvery { localRepository.insertStatements(remoteStatements) } answers {
            state.value = (state.value.toSet() + remoteStatements).toList()
        }

        advanceUntilIdle()

        assertEquals(InternetStatus.ONLINE, viewModel.internetStatus)
        assertEquals(
            localStatements + remoteStatements,
            localRepository.getStatements(categoryId).first()
        )

        viewModel.getStatements()

        advanceUntilIdle()

        assertEquals(InternetStatus.ONLINE, viewModel.internetStatus)
        assertEquals(
            localStatements + remoteStatements,
            localRepository.getStatements(categoryId).first()
        )
    }

    @Test
    fun statementsViewModel_CheckRefreshedOfflineDataWithoutInternet() = runTest {
        assertEquals(localStatements, localRepository.getStatements(categoryId).first())

        coEvery { remoteRepository.getStatements(categoryId) } throws IOException()
        coEvery { localRepository.insertStatements(FakeDataSource.statements) } answers {
            state.value = (state.value.toSet() + FakeDataSource.statements).toList()
        }

        advanceUntilIdle()

        assertEquals(InternetStatus.OFFLINE, viewModel.internetStatus)
        assertEquals(
            localStatements + FakeDataSource.statements,
            localRepository.getStatements(categoryId).first()
        )

        viewModel.getStatements()

        advanceUntilIdle()

        assertEquals(InternetStatus.OFFLINE, viewModel.internetStatus)
        assertEquals(
            localStatements + FakeDataSource.statements,
            localRepository.getStatements(categoryId).first()
        )
    }
}