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
import xelagurd.socialdating.onNodeWithTextId
import xelagurd.socialdating.ui.screen.StatementsBody
import xelagurd.socialdating.ui.state.InternetStatus
import xelagurd.socialdating.ui.state.StatementsUiState

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
    fun statementsScreen_loadingStatusAndEmptyData_loadingText() {
        val statementsUiState = StatementsUiState()

        setContentToStatementsBody(statementsUiState)

        composeTestRule.onNodeWithTextId(R.string.loading).assertIsDisplayed()
    }

    @Test
    fun statementsScreen_offlineStatusAndEmptyData_offlineText() {
        val statementsUiState = StatementsUiState(internetStatus = InternetStatus.OFFLINE)

        setContentToStatementsBody(statementsUiState)

        composeTestRule.onNodeWithTextId(R.string.no_internet_connection).assertIsDisplayed()
    }

    @Test
    fun statementsScreen_onlineStatusAndEmptyData_onlineText() {
        val statementsUiState = StatementsUiState(internetStatus = InternetStatus.ONLINE)

        setContentToStatementsBody(statementsUiState)

        composeTestRule.onNodeWithTextId(R.string.no_data).assertIsDisplayed()
    }

    @Test
    fun statementsScreen_loadingStatusAndData_displayedData() {
        val statementsUiState = StatementsUiState(
            statements = listOf(Statement(1, "Statement1", true, 1, 1)),
            internetStatus = InternetStatus.LOADING
        )

        assertDataIsDisplayed(statementsUiState)
    }

    @Test
    fun statementsScreen_offlineStatusAndData_displayedData() {
        val statementsUiState = StatementsUiState(
            statements = listOf(Statement(1, "Statement1", true, 1, 1)),
            internetStatus = InternetStatus.OFFLINE
        )

        assertDataIsDisplayed(statementsUiState)
    }

    @Test
    fun statementsScreen_onlineStatusAndData_displayedData() {
        val statementsUiState = StatementsUiState(
            statements = listOf(Statement(1, "Statement1", true, 1, 1)),
            internetStatus = InternetStatus.ONLINE
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
            StatementsBody(
                statementsUiState = statementsUiState,
                onStatementClick = {},
                onStatementReactionClick = { _, _ -> null }
            )
        }
    }
}