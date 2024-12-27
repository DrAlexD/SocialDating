package xelagurd.socialdating.tests

import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
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
import xelagurd.socialdating.data.model.Statement
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
        val statementsUiState = StatementsUiState(listOf())

        composeTestRule.activity.setContent {
            StatementsBody(
                statementsUiState = statementsUiState,
                internetStatus = InternetStatus.LOADING,
                onStatementClick = {}
            )
        }

        composeTestRule.onNodeWithTextId(R.string.loading).assertIsDisplayed()
    }

    @Test
    fun statementsScreen_offlineStatusAndEmptyData_offlineText() {
        val statementsUiState = StatementsUiState(listOf())

        composeTestRule.activity.setContent {
            StatementsBody(
                statementsUiState = statementsUiState,
                internetStatus = InternetStatus.OFFLINE,
                onStatementClick = {}
            )
        }

        composeTestRule.onNodeWithTextId(R.string.no_internet_connection).assertIsDisplayed()
    }

    @Test
    fun statementsScreen_onlineStatusAndEmptyData_onlineText() {
        val statementsUiState = StatementsUiState(listOf())

        composeTestRule.activity.setContent {
            Scaffold { innerPadding ->
                StatementsBody(
                    statementsUiState = statementsUiState,
                    internetStatus = InternetStatus.ONLINE,
                    onStatementClick = {},
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = innerPadding
                )
            }
        }

        composeTestRule.onNodeWithTextId(R.string.no_data).assertIsDisplayed()
    }

    @Test
    fun statementsScreen_loadingStatusAndData_displayedData() {
        val statementsUiState = StatementsUiState(listOf(Statement(1, "MyStatement", 1)))

        composeTestRule.activity.setContent {
            StatementsBody(
                statementsUiState = statementsUiState,
                internetStatus = InternetStatus.LOADING,
                onStatementClick = {}
            )
        }

        composeTestRule.onNodeWithText("MyStatement").assertIsDisplayed()
    }

    @Test
    fun statementsScreen_offlineStatusAndData_displayedData() {
        val statementsUiState = StatementsUiState(listOf(Statement(1, "MyStatement", 1)))

        composeTestRule.activity.setContent {
            StatementsBody(
                statementsUiState = statementsUiState,
                internetStatus = InternetStatus.OFFLINE,
                onStatementClick = {}
            )
        }

        composeTestRule.onNodeWithText("MyStatement").assertIsDisplayed()
    }

    @Test
    fun statementsScreen_onlineStatusAndData_displayedData() {
        val statementsUiState = StatementsUiState(listOf(Statement(1, "MyStatement", 1)))

        composeTestRule.activity.setContent {
            StatementsBody(
                statementsUiState = statementsUiState,
                internetStatus = InternetStatus.ONLINE,
                onStatementClick = {}
            )
        }

        composeTestRule.onNodeWithText("MyStatement").assertIsDisplayed()
    }
}