package xelagurd.socialdating.client.test

import java.io.IOException
import kotlin.random.Random
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import android.content.Context
import androidx.lifecycle.SavedStateHandle
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.confirmVerified
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.runs
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertFalse
import junit.framework.TestCase.assertTrue
import org.junit.Rule
import org.junit.Test
import retrofit2.Response
import xelagurd.socialdating.client.MainDispatcherRule
import xelagurd.socialdating.client.data.PreferencesRepository
import xelagurd.socialdating.client.data.fake.FakeData
import xelagurd.socialdating.client.data.local.repository.LocalCategoriesRepository
import xelagurd.socialdating.client.data.model.dto.PageDto
import xelagurd.socialdating.client.data.model.dto.SimilarUserDto
import xelagurd.socialdating.client.data.model.enums.AppLanguage
import xelagurd.socialdating.client.data.model.enums.Gender.FEMALE
import xelagurd.socialdating.client.data.remote.repository.RemoteUserCategoriesRepository
import xelagurd.socialdating.client.ui.form.SimilarUsersFilterFormData
import xelagurd.socialdating.client.ui.navigation.SimilarUsersDestination
import xelagurd.socialdating.client.ui.state.RequestStatus
import xelagurd.socialdating.client.ui.viewmodel.SimilarUsersViewModel

@OptIn(ExperimentalCoroutinesApi::class)
class SimilarUsersViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val context = mockk<Context>(relaxed = true)
    private val savedStateHandle = mockk<SavedStateHandle>()
    private val preferencesRepository = mockk<PreferencesRepository>()
    private val remoteUserCategoriesRepository = mockk<RemoteUserCategoriesRepository>()
    private val localCategoriesRepository = mockk<LocalCategoriesRepository>()

    private lateinit var viewModel: SimilarUsersViewModel
    private val similarUsersUiState
        get() = viewModel.uiState.value

    private val userId = Random.nextInt(1, Int.MAX_VALUE)
    private var isOfflineMode = false
    private val languageChangesFlow = MutableSharedFlow<AppLanguage>()

    private val categories = FakeData.categories
    private val savedFilterDetails = FakeData.similarUsersFilterDetails
    private val savedFormData = FakeData.similarUsersFilterFormData
    private val changedFormData = savedFormData.toggleGender(FEMALE)
    private val changedFilterDetails = changedFormData.toSimilarUsersFilterDetails()
    private val invalidFormData = savedFormData.copy(minDifferenceNumber = "", maxDifferenceNumber = "")

    private val similarUsers = FakeData.similarUsers
    private val nextCursor = "3:${Random.nextInt(1, Int.MAX_VALUE)}"
    private val nextPageSimilarUsers = similarUsers.map { it.copy(id = it.id + similarUsers.size) }

    private fun initViewModel() {
        mockGeneralMethods()

        viewModel = SimilarUsersViewModel(
            context,
            savedStateHandle,
            preferencesRepository,
            remoteUserCategoriesRepository,
            localCategoriesRepository
        )
    }

    private fun TestScope.setupUiStateCollecting() {
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
            viewModel.uiState.collect {}
        }
    }

    private fun TestScope.initViewModelAndCalculate() {
        initViewModel()
        setupUiStateCollecting()
        advanceUntilIdle()

        viewModel.calculateSimilarUsers()
        advanceUntilIdle()
    }

    @Test
    fun similarUsersViewModel_initialization_notCalculatedWithSavedFilterAndCategories() = runTest {
        mockDataWithInternet()

        initViewModel()
        setupUiStateCollecting()
        advanceUntilIdle()

        assertEquals(RequestStatus.NOT_REQUESTED, similarUsersUiState.dataRequestStatus)
        assertFalse(similarUsersUiState.isCalculated())
        assertEquals(listOf<SimilarUserDto>(), similarUsersUiState.entities)
        assertEquals(savedFormData, similarUsersUiState.formData)
        assertEquals(categories, similarUsersUiState.categories)

        coVerify(exactly = 0) { remoteUserCategoriesRepository.getSimilarUsers(any(), any(), any(), any()) }
        confirmVerified(remoteUserCategoriesRepository)
    }

    @Test
    fun similarUsersViewModel_calculateWithInternet_successStatusWithSimilarUsers() = runTest {
        mockDataWithInternet()

        initViewModelAndCalculate()

        assertEquals(RequestStatus.SUCCESS, similarUsersUiState.dataRequestStatus)
        assertTrue(similarUsersUiState.isCalculated())
        assertEquals(similarUsers, similarUsersUiState.entities)
        assertTrue(similarUsersUiState.isLastPage)

        coVerify(exactly = 1) {
            remoteUserCategoriesRepository.getSimilarUsers(userId, savedFilterDetails, null, any())
        }
        confirmVerified(remoteUserCategoriesRepository)
    }

    @Test
    fun similarUsersViewModel_calculateWithEmptyRemoteSimilarUsers_successStatusWithoutSimilarUsers() = runTest {
        mockEmptySimilarUsers()

        initViewModelAndCalculate()

        assertEquals(RequestStatus.SUCCESS, similarUsersUiState.dataRequestStatus)
        assertEquals(listOf<SimilarUserDto>(), similarUsersUiState.entities)
        assertTrue(similarUsersUiState.isLastPage)

        coVerify(exactly = 1) { remoteUserCategoriesRepository.getSimilarUsers(any(), any(), any(), any()) }
        confirmVerified(remoteUserCategoriesRepository)
    }

    @Test
    fun similarUsersViewModel_calculateWithoutInternet_errorStatus() = runTest {
        mockDataWithoutInternet()

        initViewModelAndCalculate()

        assertEquals(RequestStatus.ERROR(), similarUsersUiState.dataRequestStatus)

        coVerify(exactly = 1) { remoteUserCategoriesRepository.getSimilarUsers(any(), any(), any(), any()) }
        confirmVerified(remoteUserCategoriesRepository)
    }

    @Test
    fun similarUsersViewModel_calculateWithInternetAfterError_successStatus() = runTest {
        mockDataWithoutInternet()

        initViewModelAndCalculate()

        mockDataWithInternet()

        viewModel.calculateSimilarUsers()
        advanceUntilIdle()

        assertEquals(RequestStatus.SUCCESS, similarUsersUiState.dataRequestStatus)

        coVerify(exactly = 2) { remoteUserCategoriesRepository.getSimilarUsers(any(), any(), any(), any()) }
        confirmVerified(remoteUserCategoriesRepository)
    }

    @Test
    fun similarUsersViewModel_calculateWithoutInternetAfterSuccess_errorStatus() = runTest {
        mockDataWithInternet()

        initViewModelAndCalculate()

        mockDataWithoutInternet()

        viewModel.calculateSimilarUsers()
        advanceUntilIdle()

        assertEquals(RequestStatus.ERROR(), similarUsersUiState.dataRequestStatus)

        coVerify(exactly = 2) { remoteUserCategoriesRepository.getSimilarUsers(any(), any(), any(), any()) }
        confirmVerified(remoteUserCategoriesRepository)
    }

    @Test
    fun similarUsersViewModel_calculateWhileCalculating_calculatedOnce() = runTest {
        val remoteResponse = CompletableDeferred<Response<PageDto<SimilarUserDto>>>()
        coEvery { remoteUserCategoriesRepository.getSimilarUsers(any(), any(), any(), any()) } coAnswers {
            remoteResponse.await()
        }

        initViewModel()
        setupUiStateCollecting()
        advanceUntilIdle()

        viewModel.calculateSimilarUsers()
        advanceUntilIdle()
        viewModel.calculateSimilarUsers()
        advanceUntilIdle()

        remoteResponse.complete(Response.success(PageDto(similarUsers)))
        advanceUntilIdle()

        assertEquals(RequestStatus.SUCCESS, similarUsersUiState.dataRequestStatus)
        assertEquals(similarUsers, similarUsersUiState.entities)

        coVerify(exactly = 1) { remoteUserCategoriesRepository.getSimilarUsers(any(), any(), any(), any()) }
        confirmVerified(remoteUserCategoriesRepository)
    }

    @Test
    fun similarUsersViewModel_updateUiState_updatedAndSavedFilterWithoutCalculation() = runTest {
        mockDataWithInternet()

        initViewModel()
        setupUiStateCollecting()
        advanceUntilIdle()

        viewModel.updateUiState(changedFormData)
        advanceUntilIdle()

        assertEquals(changedFormData, similarUsersUiState.formData)
        assertFalse(similarUsersUiState.isCalculated())

        coVerify(exactly = 1) { preferencesRepository.saveSimilarUsersFilterDetails(changedFilterDetails) }
        coVerify(exactly = 0) { remoteUserCategoriesRepository.getSimilarUsers(any(), any(), any(), any()) }
        confirmVerified(remoteUserCategoriesRepository)
    }

    @Test
    fun similarUsersViewModel_updateUiStateWithInvalidForm_updatedButNotSavedFilter() = runTest {
        mockDataWithInternet()

        initViewModel()
        setupUiStateCollecting()
        advanceUntilIdle()

        viewModel.updateUiState(invalidFormData)
        advanceUntilIdle()

        assertEquals(invalidFormData, similarUsersUiState.formData)

        coVerify(exactly = 0) { preferencesRepository.saveSimilarUsersFilterDetails(any()) }
    }

    @Test
    fun similarUsersViewModel_calculateWithInvalidForm_notCalculated() = runTest {
        mockDataWithInternet()

        initViewModel()
        setupUiStateCollecting()
        advanceUntilIdle()

        viewModel.updateUiState(invalidFormData)
        viewModel.calculateSimilarUsers()
        advanceUntilIdle()

        assertFalse(similarUsersUiState.isCalculated())

        coVerify(exactly = 0) { remoteUserCategoriesRepository.getSimilarUsers(any(), any(), any(), any()) }
        confirmVerified(remoteUserCategoriesRepository)
    }

    @Test
    fun similarUsersViewModel_calculateAfterFilterChange_calculatedWithChangedFilter() = runTest {
        mockDataWithInternet()

        initViewModelAndCalculate()

        viewModel.updateUiState(changedFormData)
        viewModel.calculateSimilarUsers()
        advanceUntilIdle()

        assertEquals(changedFilterDetails, similarUsersUiState.calculatedFilterDetails)

        coVerify(exactly = 1) {
            remoteUserCategoriesRepository.getSimilarUsers(userId, savedFilterDetails, null, any())
        }
        coVerify(exactly = 1) {
            remoteUserCategoriesRepository.getSimilarUsers(userId, changedFilterDetails, null, any())
        }
        confirmVerified(remoteUserCategoriesRepository)
    }

    @Test
    fun similarUsersViewModel_calculateWithoutInternetAfterFilterChange_keptFilterOfDisplayedSimilarUsers() = runTest {
        mockDataWithInternet()

        initViewModelAndCalculate()

        mockDataWithoutInternet()

        viewModel.updateUiState(changedFormData)
        viewModel.calculateSimilarUsers()
        advanceUntilIdle()

        assertEquals(RequestStatus.ERROR(), similarUsersUiState.dataRequestStatus)
        assertEquals(similarUsers, similarUsersUiState.entities)
        assertEquals(savedFilterDetails, similarUsersUiState.calculatedFilterDetails)

        coVerify(exactly = 1) {
            remoteUserCategoriesRepository.getSimilarUsers(userId, changedFilterDetails, null, any())
        }
    }

    @Test
    fun similarUsersViewModel_refreshAfterFilterChange_recalculatedWithCalculatedFilter() = runTest {
        mockDataWithInternet()

        initViewModelAndCalculate()

        viewModel.updateUiState(changedFormData)
        viewModel.refreshSimilarUsers()
        advanceUntilIdle()

        assertEquals(RequestStatus.SUCCESS, similarUsersUiState.dataRequestStatus)
        assertEquals(changedFormData, similarUsersUiState.formData)
        assertEquals(savedFilterDetails, similarUsersUiState.calculatedFilterDetails)

        coVerify(exactly = 2) {
            remoteUserCategoriesRepository.getSimilarUsers(userId, savedFilterDetails, null, any())
        }
        confirmVerified(remoteUserCategoriesRepository)
    }

    @Test
    fun similarUsersViewModel_refreshWithInvalidForm_recalculatedWithCalculatedFilter() = runTest {
        mockDataWithInternet()

        initViewModelAndCalculate()

        viewModel.updateUiState(invalidFormData)
        viewModel.refreshSimilarUsers()
        advanceUntilIdle()

        assertEquals(RequestStatus.SUCCESS, similarUsersUiState.dataRequestStatus)

        coVerify(exactly = 2) {
            remoteUserCategoriesRepository.getSimilarUsers(userId, savedFilterDetails, null, any())
        }
        confirmVerified(remoteUserCategoriesRepository)
    }

    @Test
    fun similarUsersViewModel_nextPageWithInternet_addsPageToLoadedSimilarUsers() = runTest {
        mockDataWithInternet(firstPageNextCursor = nextCursor)

        initViewModelAndCalculate()

        assertFalse(similarUsersUiState.isLastPage)

        viewModel.getNextSimilarUsers()
        advanceUntilIdle()

        assertEquals(RequestStatus.SUCCESS, similarUsersUiState.nextPageRequestStatus)
        assertEquals(similarUsers + nextPageSimilarUsers, similarUsersUiState.entities)
        assertTrue(similarUsersUiState.isLastPage)

        coVerify(exactly = 1) { remoteUserCategoriesRepository.getSimilarUsers(any(), any(), null, any()) }
        coVerify(exactly = 1) { remoteUserCategoriesRepository.getSimilarUsers(any(), any(), nextCursor, any()) }
        confirmVerified(remoteUserCategoriesRepository)
    }

    @Test
    fun similarUsersViewModel_nextPageAfterFilterChange_loadedWithCalculatedFilter() = runTest {
        mockDataWithInternet(firstPageNextCursor = nextCursor)

        initViewModelAndCalculate()

        viewModel.updateUiState(changedFormData)
        viewModel.getNextSimilarUsers()
        advanceUntilIdle()

        assertEquals(similarUsers + nextPageSimilarUsers, similarUsersUiState.entities)
        assertEquals(changedFormData, similarUsersUiState.formData)
        assertEquals(savedFilterDetails, similarUsersUiState.calculatedFilterDetails)

        coVerify(exactly = 1) {
            remoteUserCategoriesRepository.getSimilarUsers(userId, savedFilterDetails, null, any())
        }
        coVerify(exactly = 1) {
            remoteUserCategoriesRepository.getSimilarUsers(userId, savedFilterDetails, nextCursor, any())
        }
        confirmVerified(remoteUserCategoriesRepository)
    }

    @Test
    fun similarUsersViewModel_nextPageWithAlreadyLoadedUser_keepsUniqueSimilarUsers() = runTest {
        mockDataWithInternet(firstPageNextCursor = nextCursor)
        coEvery { remoteUserCategoriesRepository.getSimilarUsers(any(), any(), nextCursor, any()) } returns
                Response.success(PageDto(listOf(similarUsers.last()) + nextPageSimilarUsers))

        initViewModelAndCalculate()

        viewModel.getNextSimilarUsers()
        advanceUntilIdle()

        assertEquals(similarUsers + nextPageSimilarUsers, similarUsersUiState.entities)

        val loadedIds = similarUsersUiState.entities.map { it.id }
        assertEquals(loadedIds.distinct(), loadedIds)
    }

    @Test
    fun similarUsersViewModel_nextPageBeforeCalculation_doesNothing() = runTest {
        mockDataWithInternet(firstPageNextCursor = nextCursor)

        initViewModel()
        setupUiStateCollecting()
        advanceUntilIdle()

        viewModel.getNextSimilarUsers()
        advanceUntilIdle()

        coVerify(exactly = 0) { remoteUserCategoriesRepository.getSimilarUsers(any(), any(), any(), any()) }
        confirmVerified(remoteUserCategoriesRepository)
    }

    @Test
    fun similarUsersViewModel_nextPageAfterLastPage_doesNothing() = runTest {
        mockDataWithInternet()

        initViewModelAndCalculate()

        viewModel.getNextSimilarUsers()
        advanceUntilIdle()

        coVerify(exactly = 1) { remoteUserCategoriesRepository.getSimilarUsers(any(), any(), any(), any()) }
        confirmVerified(remoteUserCategoriesRepository)
    }

    @Test
    fun similarUsersViewModel_nextPageWithoutInternet_errorStatusOfNextPageOnly() = runTest {
        mockDataWithInternet(firstPageNextCursor = nextCursor)

        initViewModelAndCalculate()

        coEvery { remoteUserCategoriesRepository.getSimilarUsers(any(), any(), any(), any()) } throws IOException()

        viewModel.getNextSimilarUsers()
        advanceUntilIdle()

        assertEquals(RequestStatus.SUCCESS, similarUsersUiState.dataRequestStatus)
        assertEquals(RequestStatus.ERROR(), similarUsersUiState.nextPageRequestStatus)
        assertEquals(similarUsers, similarUsersUiState.entities)
        assertFalse(similarUsersUiState.isLastPage)
    }

    @Test
    fun similarUsersViewModel_calculateAfterNextPage_replacesLoadedSimilarUsers() = runTest {
        mockDataWithInternet(firstPageNextCursor = nextCursor)

        initViewModelAndCalculate()

        viewModel.getNextSimilarUsers()
        advanceUntilIdle()

        viewModel.calculateSimilarUsers()
        advanceUntilIdle()

        assertEquals(similarUsers, similarUsersUiState.entities)

        coVerify(exactly = 2) { remoteUserCategoriesRepository.getSimilarUsers(any(), any(), null, any()) }
        coVerify(exactly = 1) { remoteUserCategoriesRepository.getSimilarUsers(any(), any(), nextCursor, any()) }
        confirmVerified(remoteUserCategoriesRepository)
    }

    @Test
    fun similarUsersViewModel_appLanguageChangeBeforeCalculation_notCalculated() = runTest {
        mockDataWithInternet()

        initViewModel()
        setupUiStateCollecting()
        advanceUntilIdle()

        languageChangesFlow.emit(AppLanguage.ENGLISH)
        advanceUntilIdle()

        assertFalse(similarUsersUiState.isCalculated())

        coVerify(exactly = 0) { remoteUserCategoriesRepository.getSimilarUsers(any(), any(), any(), any()) }
        confirmVerified(remoteUserCategoriesRepository)
    }

    @Test
    fun similarUsersViewModel_appLanguageChangeAfterCalculation_recalculatedSimilarUsers() = runTest {
        mockDataWithInternet()

        initViewModelAndCalculate()

        languageChangesFlow.emit(AppLanguage.ENGLISH)
        advanceUntilIdle()

        assertEquals(RequestStatus.SUCCESS, similarUsersUiState.dataRequestStatus)
        assertEquals(similarUsers, similarUsersUiState.entities)

        coVerify(exactly = 2) { remoteUserCategoriesRepository.getSimilarUsers(any(), any(), null, any()) }
    }

    @Test
    fun similarUsersViewModel_appLanguageChangeAfterFilterChange_recalculatedWithCalculatedFilter() = runTest {
        mockDataWithInternet()

        initViewModelAndCalculate()

        viewModel.updateUiState(changedFormData)
        languageChangesFlow.emit(AppLanguage.ENGLISH)
        advanceUntilIdle()

        assertEquals(changedFormData, similarUsersUiState.formData)
        assertEquals(savedFilterDetails, similarUsersUiState.calculatedFilterDetails)

        coVerify(exactly = 2) {
            remoteUserCategoriesRepository.getSimilarUsers(userId, savedFilterDetails, null, any())
        }
        confirmVerified(remoteUserCategoriesRepository)
    }

    @Test
    fun similarUsersViewModel_appLanguageChangeAfterFailedFirstCalculation_recalculatedWithFailedFilter() = runTest {
        mockDataWithoutInternet()

        initViewModel()
        setupUiStateCollecting()
        advanceUntilIdle()

        viewModel.updateUiState(changedFormData)
        viewModel.calculateSimilarUsers()
        advanceUntilIdle()

        assertEquals(changedFilterDetails, similarUsersUiState.calculatedFilterDetails)

        languageChangesFlow.emit(AppLanguage.ENGLISH)
        advanceUntilIdle()

        coVerify(exactly = 2) {
            remoteUserCategoriesRepository.getSimilarUsers(userId, changedFilterDetails, null, any())
        }
        confirmVerified(remoteUserCategoriesRepository)
    }

    @Test
    fun similarUsersViewModel_calculateInOfflineMode_filteredFakeSimilarUsersWithOfflineStatus() = runTest {
        isOfflineMode = true

        initViewModelAndCalculate()

        assertEquals(RequestStatus.ERROR(), similarUsersUiState.dataRequestStatus)
        assertEquals(FakeData.filterSimilarUsers(savedFilterDetails), similarUsersUiState.entities)
        assertTrue(similarUsersUiState.entities.isNotEmpty())
        assertTrue(similarUsersUiState.isLastPage)

        coVerify(exactly = 0) { remoteUserCategoriesRepository.getSimilarUsers(any(), any(), any(), any()) }
        confirmVerified(remoteUserCategoriesRepository)
    }

    @Test
    fun similarUsersViewModel_calculateInOfflineModeWithUnsuitableFilter_withoutSimilarUsers() = runTest {
        isOfflineMode = true

        initViewModel()
        setupUiStateCollecting()
        advanceUntilIdle()

        viewModel.updateUiState(SimilarUsersFilterFormData(genders = setOf(FEMALE)))
        viewModel.calculateSimilarUsers()
        advanceUntilIdle()

        assertEquals(RequestStatus.ERROR(), similarUsersUiState.dataRequestStatus)
        assertEquals(listOf<SimilarUserDto>(), similarUsersUiState.entities)
    }

    @Test
    fun similarUsersViewModel_calculateInOfflineModeAfterFilterChange_calculatedWithChangedFilter() = runTest {
        isOfflineMode = true

        initViewModelAndCalculate()

        viewModel.updateUiState(changedFormData)
        viewModel.calculateSimilarUsers()
        advanceUntilIdle()

        assertEquals(FakeData.filterSimilarUsers(changedFilterDetails), similarUsersUiState.entities)
        assertEquals(changedFilterDetails, similarUsersUiState.calculatedFilterDetails)
    }

    @Test
    fun similarUsersViewModel_calculationAndRefresh_dataRequestedByPullOnlyByRefresh() = runTest {
        mockDataWithInternet()

        initViewModelAndCalculate()

        assertFalse(similarUsersUiState.isDataRequestedByPull)

        viewModel.refreshSimilarUsers()
        advanceUntilIdle()

        assertTrue(similarUsersUiState.isDataRequestedByPull)
    }

    @Test
    fun similarUsersViewModel_appLanguageChangeAfterRefresh_dataNotRequestedByPull() = runTest {
        mockDataWithInternet()

        initViewModelAndCalculate()

        viewModel.refreshSimilarUsers()
        advanceUntilIdle()

        languageChangesFlow.emit(AppLanguage.ENGLISH)
        advanceUntilIdle()

        assertFalse(similarUsersUiState.isDataRequestedByPull)
    }

    private fun mockGeneralMethods() {
        every { savedStateHandle.get<Int>(SimilarUsersDestination.userId) } returns userId
        every { preferencesRepository.isOfflineMode } returns flowOf(isOfflineMode)
        every { preferencesRepository.languageChanges } returns languageChangesFlow
        every { preferencesRepository.similarUsersFilterDetails } returns flowOf(savedFilterDetails)
        coEvery { preferencesRepository.saveSimilarUsersFilterDetails(any()) } just runs
        every { localCategoriesRepository.getCategories() } returns flowOf(categories)
    }

    private fun mockDataWithInternet(firstPageNextCursor: String? = null) {
        coEvery { remoteUserCategoriesRepository.getSimilarUsers(any(), any(), null, any()) } returns
                Response.success(PageDto(similarUsers, firstPageNextCursor))
        coEvery {
            remoteUserCategoriesRepository.getSimilarUsers(any(), any(), firstPageNextCursor ?: "", any())
        } returns Response.success(PageDto(nextPageSimilarUsers))
    }

    private fun mockEmptySimilarUsers() {
        coEvery { remoteUserCategoriesRepository.getSimilarUsers(any(), any(), any(), any()) } returns
                Response.success(null)
    }

    private fun mockDataWithoutInternet() {
        coEvery { remoteUserCategoriesRepository.getSimilarUsers(any(), any(), any(), any()) } throws IOException()
    }
}
