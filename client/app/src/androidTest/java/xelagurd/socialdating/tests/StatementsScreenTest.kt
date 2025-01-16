package xelagurd.socialdating.tests

import androidx.activity.compose.setContent
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithText
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import xelagurd.socialdating.MainActivity
import xelagurd.socialdating.R
import xelagurd.socialdating.checkEnabledButton
import xelagurd.socialdating.data.model.Statement
import xelagurd.socialdating.onNodeWithContentDescriptionId
import xelagurd.socialdating.onNodeWithTagId
import xelagurd.socialdating.onNodeWithTextId
import xelagurd.socialdating.ui.screen.StatementsScreenComponent
import xelagurd.socialdating.ui.state.RequestStatus
import xelagurd.socialdating.ui.state.StatementsUiState
import xelagurd.socialdating.ui.theme.AppTheme

@HiltAndroidTest
class StatementsScreenTest {
    @get:Rule(order = 0)
    val hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 1)
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    @Before
    fun setup() {
        hiltRule.inject()
    }

    @Test
    fun statementsScreen_loadingStatusAndEmptyData_loadingIndicator() {
        val statementsUiState = StatementsUiState()

        setContentToStatementsBody(statementsUiState)

        composeTestRule.onNodeWithTagId(R.string.loading).assertIsDisplayed()
    }

    @Test
    fun statementsScreen_offlineStatusAndEmptyData_offlineText() {
        val statementsUiState = StatementsUiState(dataRequestStatus = RequestStatus.ERROR)

        setContentToStatementsBody(statementsUiState)

        composeTestRule.onNodeWithTextId(R.string.no_internet_connection).assertIsDisplayed()
    }

    @Test
    fun statementsScreen_onlineStatusAndEmptyData_onlineText() {
        val statementsUiState = StatementsUiState(dataRequestStatus = RequestStatus.SUCCESS)

        setContentToStatementsBody(statementsUiState)

        composeTestRule.onNodeWithTextId(R.string.no_data).assertIsDisplayed()
    }

    @Test
    fun statementsScreen_loadingStatusAndData_displayedData() {
        val statementsUiState = StatementsUiState(
            entities = listOf(Statement(1, "Statement1", true, 1, 1)),
            dataRequestStatus = RequestStatus.LOADING
        )

        assertDataIsDisplayed(statementsUiState)
    }

    @Test
    fun statementsScreen_offlineStatusAndData_displayedData() {
        val statementsUiState = StatementsUiState(
            entities = listOf(Statement(1, "Statement1", true, 1, 1)),
            dataRequestStatus = RequestStatus.ERROR
        )

        assertDataIsDisplayed(statementsUiState)
    }

    @Test
    fun statementsScreen_onlineStatusAndData_displayedData() {
        val statementsUiState = StatementsUiState(
            entities = listOf(Statement(1, "Statement1", true, 1, 1)),
            dataRequestStatus = RequestStatus.SUCCESS
        )

        assertDataIsDisplayed(statementsUiState)
    }

    private fun assertDataIsDisplayed(statementsUiState: StatementsUiState) {
        setContentToStatementsBody(statementsUiState)

        composeTestRule.onNodeWithText("Statement1").assertIsDisplayed()
        composeTestRule.onNodeWithContentDescriptionId(R.string.full_maintain).checkEnabledButton()
        composeTestRule.onNodeWithContentDescriptionId(R.string.part_maintain).checkEnabledButton()
        composeTestRule.onNodeWithContentDescriptionId(R.string.not_sure).checkEnabledButton()
        composeTestRule.onNodeWithContentDescriptionId(R.string.part_no_maintain)
            .checkEnabledButton()
        composeTestRule.onNodeWithContentDescriptionId(R.string.full_no_maintain)
            .checkEnabledButton()
    }

    private fun setContentToStatementsBody(statementsUiState: StatementsUiState) {
        composeTestRule.activity.setContent {
            AppTheme {
                StatementsScreenComponent(
                    statementsUiState = statementsUiState
                )
            }
        }
    }
}