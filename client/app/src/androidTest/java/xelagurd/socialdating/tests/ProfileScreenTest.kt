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
import xelagurd.socialdating.data.model.User
import xelagurd.socialdating.onNodeWithTextId
import xelagurd.socialdating.ui.screen.ProfileBody
import xelagurd.socialdating.ui.state.InternetStatus
import xelagurd.socialdating.ui.state.ProfileUiState

@HiltAndroidTest
class ProfileScreenTest {
    @get:Rule(order = 0)
    val hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 1)
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    @Before
    fun setup() {
        hiltRule.inject()
    }

    @Test
    fun profileScreen_loadingStatusAndEmptyData_loadingText() {
        val profileUiState = ProfileUiState()

        composeTestRule.activity.setContent {
            ProfileBody(
                profileUiState = profileUiState,
                onProfileStatisticsClick = {}
            )
        }

        composeTestRule.onNodeWithTextId(R.string.loading).assertIsDisplayed()
    }

    @Test
    fun profileScreen_offlineStatusAndEmptyData_offlineText() {
        val profileUiState = ProfileUiState(internetStatus = InternetStatus.OFFLINE)

        composeTestRule.activity.setContent {
            ProfileBody(
                profileUiState = profileUiState,
                onProfileStatisticsClick = {}
            )
        }

        composeTestRule.onNodeWithTextId(R.string.no_internet_connection).assertIsDisplayed()
    }

    @Test
    fun profileScreen_onlineStatusAndEmptyData_onlineText() {
        val profileUiState = ProfileUiState(internetStatus = InternetStatus.ONLINE)

        composeTestRule.activity.setContent {
            ProfileBody(
                profileUiState = profileUiState,
                onProfileStatisticsClick = {}
            )
        }

        composeTestRule.onNodeWithTextId(R.string.no_data).assertIsDisplayed()
    }

    @Test
    fun profileScreen_loadingStatusAndData_displayedData() {
        val profileUiState = ProfileUiState(
            user = User(1, "MyName", "", "", "", "", 30, "", "", 50),
            internetStatus = InternetStatus.LOADING
        )

        composeTestRule.activity.setContent {
            ProfileBody(
                profileUiState = profileUiState,
                onProfileStatisticsClick = {}
            )
        }

        composeTestRule.onNodeWithText("MyName").assertIsDisplayed()
    }

    @Test
    fun profileScreen_offlineStatusAndData_displayedData() {
        val profileUiState = ProfileUiState(
            user = User(1, "MyName", "", "", "", "", 30, "", "", 50),
            internetStatus = InternetStatus.OFFLINE
        )

        composeTestRule.activity.setContent {
            ProfileBody(
                profileUiState = profileUiState,
                onProfileStatisticsClick = {}
            )
        }

        composeTestRule.onNodeWithText("MyName").assertIsDisplayed()
    }

    @Test
    fun profileScreen_onlineStatusAndData_displayedData() {
        val profileUiState = ProfileUiState(
            user = User(1, "MyName", "", "", "", "", 30, "", "", 50),
            internetStatus = InternetStatus.ONLINE
        )

        composeTestRule.activity.setContent {
            ProfileBody(
                profileUiState = profileUiState,
                onProfileStatisticsClick = {}
            )
        }

        composeTestRule.onNodeWithText("MyName").assertIsDisplayed()
    }
}