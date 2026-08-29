package xelagurd.socialdating.client.androidTest

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsNotDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.unit.dp
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import xelagurd.socialdating.client.AndroidTestUtils.onNodeWithTagId
import xelagurd.socialdating.client.AndroidTestUtils.onNodeWithTextId
import xelagurd.socialdating.client.AndroidTestUtils.setContentToScreen
import xelagurd.socialdating.client.MainActivity
import xelagurd.socialdating.client.R
import xelagurd.socialdating.client.data.fake.FakeData
import xelagurd.socialdating.client.data.model.Category
import xelagurd.socialdating.client.data.model.DefiningTheme
import xelagurd.socialdating.client.data.model.User
import xelagurd.socialdating.client.ui.screen.AppLargeTitleText
import xelagurd.socialdating.client.ui.screen.AppMediumTextCard
import xelagurd.socialdating.client.ui.screen.ComponentWithActionRequestStatus
import xelagurd.socialdating.client.ui.screen.DataChoosingListComponent
import xelagurd.socialdating.client.ui.screen.DataMultiChoosingListComponent
import xelagurd.socialdating.client.ui.screen.DataEntityComponent
import xelagurd.socialdating.client.ui.screen.DataListComponent
import xelagurd.socialdating.client.ui.state.CategoriesUiState
import xelagurd.socialdating.client.ui.state.ProfileUiState
import xelagurd.socialdating.client.ui.state.RequestStatus
import xelagurd.socialdating.client.ui.state.StatementAddingUiState

@HiltAndroidTest
class AppComponentsTest {
    @get:Rule(order = 0)
    val hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 1)
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    @Before
    fun setup() {
        hiltRule.inject()
    }

    private val categories = FakeData.categories
    private val definingThemes = FakeData.definingThemes
    private val user = FakeData.mainUser

    private val dataRequestStatusesWithoutSuccess = listOf(
        RequestStatus.UNDEFINED,
        RequestStatus.LOADING,
        RequestStatus.FAILURE(FakeData.FAILURE_TEXT),
        RequestStatus.ERROR(FakeData.ERROR_TEXT)
    )

    @Test
    fun dataListComponent_undefinedStateAndEmptyData_loadingIndicator() {
        setContentToDataListComponent(CategoriesUiState(dataRequestStatus = RequestStatus.UNDEFINED))

        composeTestRule.onNodeWithTagId(R.string.loading).assertIsDisplayed()
    }

    @Test
    fun dataListComponent_loadingStateAndEmptyData_loadingIndicator() {
        setContentToDataListComponent(CategoriesUiState(dataRequestStatus = RequestStatus.LOADING))

        composeTestRule.onNodeWithTagId(R.string.loading).assertIsDisplayed()
    }

    @Test
    fun dataListComponent_failureStateAndEmptyData_failureText() {
        setContentToDataListComponent(
            CategoriesUiState(dataRequestStatus = RequestStatus.FAILURE(FakeData.FAILURE_TEXT))
        )

        composeTestRule.onNodeWithText(FakeData.FAILURE_TEXT).assertIsDisplayed()
    }

    @Test
    fun dataListComponent_errorStateAndEmptyData_errorText() {
        setContentToDataListComponent(
            CategoriesUiState(dataRequestStatus = RequestStatus.ERROR(FakeData.ERROR_TEXT))
        )

        composeTestRule.onNodeWithText(FakeData.ERROR_TEXT).assertIsDisplayed()
    }

    @Test
    fun dataListComponent_successStateAndEmptyData_noDataText() {
        setContentToDataListComponent(CategoriesUiState(dataRequestStatus = RequestStatus.SUCCESS))

        composeTestRule.onNodeWithTextId(R.string.no_data).assertIsDisplayed()
    }

    @Test
    fun dataListComponent_anyStateAndData_displayedData() {
        (dataRequestStatusesWithoutSuccess + RequestStatus.SUCCESS).forEach { dataRequestStatus ->
            setContentToDataListComponent(
                CategoriesUiState(entities = categories, dataRequestStatus = dataRequestStatus)
            )

            composeTestRule.onNodeWithText(categories[0].name).assertIsDisplayed()
            composeTestRule.onNodeWithTagId(R.string.loading).assertIsNotDisplayed()
            composeTestRule.onNodeWithTextId(R.string.no_data).assertIsNotDisplayed()
        }
    }

    @Test
    fun dataEntityComponent_successStateAndEmptyData_noDataText() {
        setContentToDataEntityComponent(ProfileUiState(dataRequestStatus = RequestStatus.SUCCESS))

        composeTestRule.onNodeWithTextId(R.string.no_data).assertIsDisplayed()
    }

    @Test
    fun dataEntityComponent_anyStateAndData_displayedData() {
        (dataRequestStatusesWithoutSuccess + RequestStatus.SUCCESS).forEach { dataRequestStatus ->
            setContentToDataEntityComponent(
                ProfileUiState(entity = user, dataRequestStatus = dataRequestStatus)
            )

            composeTestRule.onNodeWithText(user.username).assertIsDisplayed()
            composeTestRule.onNodeWithTagId(R.string.loading).assertIsNotDisplayed()
        }
    }

    @Test
    fun dataChoosingListComponent_loadingStateAndEmptyData_loadingIndicator() {
        setContentToDataChoosingListComponent(
            StatementAddingUiState(dataRequestStatus = RequestStatus.LOADING),
            chosenEntityId = null
        )

        composeTestRule.onNodeWithTagId(R.string.loading).assertIsDisplayed()
    }

    @Test
    fun dataChoosingListComponent_withoutChosenEntity_displayedAllData() {
        setContentToDataChoosingListComponent(
            StatementAddingUiState(entities = definingThemes, dataRequestStatus = RequestStatus.SUCCESS),
            chosenEntityId = null
        )

        composeTestRule.onNodeWithText(definingThemes[0].name).assertIsDisplayed()
        composeTestRule.onNodeWithText(definingThemes[1].name).assertIsDisplayed()
    }

    @Test
    fun dataChoosingListComponent_chosenEntity_displayedOnlyChosenData() {
        setContentToDataChoosingListComponent(
            StatementAddingUiState(entities = definingThemes, dataRequestStatus = RequestStatus.SUCCESS),
            chosenEntityId = definingThemes[0].id
        )

        composeTestRule.onNodeWithText(definingThemes[0].name).assertIsDisplayed()
        composeTestRule.onNodeWithText(definingThemes[1].name).assertIsNotDisplayed()
    }

    @Test
    fun dataMultiChoosingListComponent_loadingStateAndEmptyData_loadingIndicator() {
        setContentToDataMultiChoosingListComponent(
            StatementAddingUiState(dataRequestStatus = RequestStatus.LOADING),
            chosenEntityIds = setOf()
        )

        composeTestRule.onNodeWithTagId(R.string.loading).assertIsDisplayed()
    }

    @Test
    fun dataMultiChoosingListComponent_chosenEntities_displayedAllData() {
        setContentToDataMultiChoosingListComponent(
            StatementAddingUiState(entities = definingThemes, dataRequestStatus = RequestStatus.SUCCESS),
            chosenEntityIds = setOf(definingThemes[0].id)
        )

        composeTestRule.onNodeWithText(definingThemes[0].name).assertIsDisplayed()
        composeTestRule.onNodeWithText(definingThemes[1].name).assertIsDisplayed()
    }

    @Test
    fun componentWithActionRequestStatus_undefinedState_onlyContent() {
        val isSuccess = setContentToComponentWithActionRequestStatus(RequestStatus.UNDEFINED)

        composeTestRule.onNodeWithText(CONTENT_TEXT).assertIsDisplayed()
        composeTestRule.onNodeWithTagId(R.string.loading).assertIsNotDisplayed()
        assertFalse(isSuccess())
    }

    @Test
    fun componentWithActionRequestStatus_loadingState_loadingIndicator() {
        val isSuccess = setContentToComponentWithActionRequestStatus(RequestStatus.LOADING)

        composeTestRule.onNodeWithText(CONTENT_TEXT).assertIsDisplayed()
        composeTestRule.onNodeWithTagId(R.string.loading).assertIsDisplayed()
        assertFalse(isSuccess())
    }

    @Test
    fun componentWithActionRequestStatus_failureState_failureText() {
        val isSuccess =
            setContentToComponentWithActionRequestStatus(RequestStatus.FAILURE(FakeData.FAILURE_TEXT))

        composeTestRule.onNodeWithText(FakeData.FAILURE_TEXT).assertIsDisplayed()
        assertFalse(isSuccess())
    }

    @Test
    fun componentWithActionRequestStatus_errorState_errorText() {
        val isSuccess =
            setContentToComponentWithActionRequestStatus(RequestStatus.ERROR(FakeData.ERROR_TEXT))

        composeTestRule.onNodeWithText(FakeData.ERROR_TEXT).assertIsDisplayed()
        assertFalse(isSuccess())
    }

    @Test
    fun componentWithActionRequestStatus_successState_calledSuccessAction() {
        val isSuccess = setContentToComponentWithActionRequestStatus(RequestStatus.SUCCESS)
        composeTestRule.waitForIdle()

        assertTrue(isSuccess())
    }

    private fun setContentToDataListComponent(categoriesUiState: CategoriesUiState) {
        composeTestRule.setContentToScreen {
            DataListComponent(dataListUiState = categoriesUiState) {
                AppLargeTitleText(text = (it as Category).name)
            }
        }
    }

    private fun setContentToDataEntityComponent(profileUiState: ProfileUiState) {
        composeTestRule.setContentToScreen {
            DataEntityComponent(dataEntityUiState = profileUiState) {
                AppLargeTitleText(text = (it as User).username)
            }
        }
    }

    private fun setContentToDataChoosingListComponent(
        statementAddingUiState: StatementAddingUiState,
        chosenEntityId: Int?
    ) {
        composeTestRule.setContentToScreen {
            DataChoosingListComponent(
                dataListUiState = statementAddingUiState,
                chosenEntityId = chosenEntityId,
                maxHeight = MAX_HEIGHT_DP.dp
            ) { entity, isHasBorder ->
                AppMediumTextCard(
                    text = (entity as DefiningTheme).name,
                    onClick = {},
                    isHasBorder = isHasBorder
                )
            }
        }
    }

    private fun setContentToDataMultiChoosingListComponent(
        statementAddingUiState: StatementAddingUiState,
        chosenEntityIds: Set<Int>
    ) {
        composeTestRule.setContentToScreen {
            DataMultiChoosingListComponent(
                dataListUiState = statementAddingUiState,
                chosenEntityIds = chosenEntityIds,
                maxHeight = MAX_HEIGHT_DP.dp
            ) { entity, isHasBorder ->
                AppMediumTextCard(
                    text = (entity as DefiningTheme).name,
                    onClick = {},
                    isHasBorder = isHasBorder
                )
            }
        }
    }

    private fun setContentToComponentWithActionRequestStatus(actionRequestStatus: RequestStatus): () -> Boolean {
        var isSuccess = false

        composeTestRule.setContentToScreen {
            ComponentWithActionRequestStatus(
                actionRequestStatus = actionRequestStatus,
                onSuccess = { isSuccess = true }
            ) {
                AppLargeTitleText(text = CONTENT_TEXT)
            }
        }

        return { isSuccess }
    }

    private companion object {
        const val CONTENT_TEXT = "Content"
        const val MAX_HEIGHT_DP = 200
    }
}