package xelagurd.socialdating.client.androidTest

import androidx.activity.compose.setContent
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithText
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import xelagurd.socialdating.client.MainActivity
import xelagurd.socialdating.client.R
import xelagurd.socialdating.client.checkEnabledButton
import xelagurd.socialdating.client.data.model.Statement
import xelagurd.socialdating.client.onNodeWithContentDescriptionId
import xelagurd.socialdating.client.onNodeWithTagId
import xelagurd.socialdating.client.ui.screen.StatementsScreenComponent
import xelagurd.socialdating.client.ui.state.RequestStatus
import xelagurd.socialdating.client.ui.state.StatementsUiState
import xelagurd.socialdating.client.ui.theme.AppTheme

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
    fun statementsScreen_loadingStateAndEmptyData_loadingIndicator() {
        val statementsUiState = StatementsUiState()

        setContentToStatementsBody(statementsUiState)

        composeTestRule.onNodeWithTagId(R.string.loading).assertIsDisplayed()
    }

    @Test
    fun statementsScreen_errorStateAndEmptyData_errorText() {
        val errorText = "Error Text"
        val statementsUiState = StatementsUiState(
            dataRequestStatus = RequestStatus.ERROR(errorText)
        )

        setContentToStatementsBody(statementsUiState)

        composeTestRule.onNodeWithText(errorText).assertIsDisplayed()
    }

    @Test
    fun statementsScreen_failureStateAndEmptyData_failureText() {
        val failureText = "Failure Text"
        val statementsUiState = StatementsUiState(
            dataRequestStatus = RequestStatus.FAILURE(failureText)
        )

        setContentToStatementsBody(statementsUiState)

        composeTestRule.onNodeWithText(failureText).assertIsDisplayed()
    }

    @Test
    fun statementsScreen_loadingStateAndData_displayedData() {
        val statementsUiState = StatementsUiState(
            entities = listOf(Statement(1, "Statement1", true, 1, 1)),
            dataRequestStatus = RequestStatus.LOADING
        )

        assertDataIsDisplayed(statementsUiState)
    }

    @Test
    fun statementsScreen_errorStateAndData_displayedData() {
        val statementsUiState = StatementsUiState(
            entities = listOf(Statement(1, "Statement1", true, 1, 1)),
            dataRequestStatus = RequestStatus.ERROR()
        )

        assertDataIsDisplayed(statementsUiState)
    }

    @Test
    fun statementsScreen_failureStateAndData_displayedData() {
        val statementsUiState = StatementsUiState(
            entities = listOf(Statement(1, "Statement1", true, 1, 1)),
            dataRequestStatus = RequestStatus.FAILURE()
        )

        assertDataIsDisplayed(statementsUiState)
    }

    @Test
    fun statementsScreen_successStateAndData_displayedData() {
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