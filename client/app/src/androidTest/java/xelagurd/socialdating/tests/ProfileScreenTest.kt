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
import xelagurd.socialdating.data.model.User
import xelagurd.socialdating.data.model.enums.Gender
import xelagurd.socialdating.data.model.enums.Purpose
import xelagurd.socialdating.onNodeWithTagId
import xelagurd.socialdating.onNodeWithTextId
import xelagurd.socialdating.onNodeWithTextIdWithColon
import xelagurd.socialdating.ui.screen.ProfileScreenComponent
import xelagurd.socialdating.ui.state.ProfileUiState
import xelagurd.socialdating.ui.state.RequestStatus
import xelagurd.socialdating.ui.theme.AppTheme

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
    fun profileScreen_loadingStateAndEmptyData_loadingIndicator() {
        val profileUiState = ProfileUiState()

        setContentToProfileBody(profileUiState)

        composeTestRule.onNodeWithTagId(R.string.loading).assertIsDisplayed()
    }

    @Test
    fun profileScreen_errorStateAndEmptyData_errorText() {
        val errorText = "Error Text"
        val profileUiState = ProfileUiState(dataRequestStatus = RequestStatus.ERROR(errorText))

        setContentToProfileBody(profileUiState)

        composeTestRule.onNodeWithText(errorText).assertIsDisplayed()
    }

    @Test
    fun profileScreen_failureStateAndEmptyData_failureText() {
        val failureText = "Failure Text"
        val profileUiState = ProfileUiState(
            dataRequestStatus = RequestStatus.FAILURE(failureText)
        )

        setContentToProfileBody(profileUiState)

        composeTestRule.onNodeWithText(failureText).assertIsDisplayed()
    }

    @Test
    fun profileScreen_loadingStateAndData_displayedData() {
        val profileUiState = ProfileUiState(
            entity = User(
                1, "User1", Gender.MALE, "username1", "password1",
                "email1@gmail.com", 30, "Moscow", Purpose.ALL_AT_ONCE, 50
            ),
            dataRequestStatus = RequestStatus.LOADING
        )

        assertDataIsDisplayed(profileUiState)
    }

    @Test
    fun profileScreen_errorStateAndData_displayedData() {
        val profileUiState = ProfileUiState(
            entity = User(
                1, "User1", Gender.MALE, "username1", "password1",
                "email1@gmail.com", 30, "Moscow", Purpose.ALL_AT_ONCE, 50
            ),
            dataRequestStatus = RequestStatus.ERROR()
        )

        assertDataIsDisplayed(profileUiState)
    }

    @Test
    fun profileScreen_failureStateAndData_displayedData() {
        val profileUiState = ProfileUiState(
            entity = User(
                1, "User1", Gender.MALE, "username1", "password1",
                "email1@gmail.com", 30, "Moscow", Purpose.ALL_AT_ONCE, 50
            ),
            dataRequestStatus = RequestStatus.FAILURE()
        )

        assertDataIsDisplayed(profileUiState)
    }

    @Test
    fun profileScreen_successStateAndData_displayedData() {
        val profileUiState = ProfileUiState(
            entity = User(
                1, "User1", Gender.MALE, "username1", "password1",
                "email1@gmail.com", 30, "Moscow", Purpose.ALL_AT_ONCE, 50
            ),
            dataRequestStatus = RequestStatus.SUCCESS
        )

        assertDataIsDisplayed(profileUiState)
    }

    private fun assertDataIsDisplayed(profileUiState: ProfileUiState) {
        setContentToProfileBody(profileUiState)

        composeTestRule.onNodeWithTextIdWithColon(R.string.username).assertIsDisplayed()
        composeTestRule.onNodeWithTextIdWithColon(R.string.name).assertIsDisplayed()
        composeTestRule.onNodeWithTextIdWithColon(R.string.age).assertIsDisplayed()
        composeTestRule.onNodeWithTextIdWithColon(R.string.city).assertIsDisplayed()
        composeTestRule.onNodeWithTextIdWithColon(R.string.purpose).assertIsDisplayed()

        composeTestRule.onNodeWithText("username1").assertIsDisplayed()
        composeTestRule.onNodeWithText("User1").assertIsDisplayed()
        composeTestRule.onNodeWithText("30").assertIsDisplayed()
        composeTestRule.onNodeWithText("Moscow").assertIsDisplayed()
        composeTestRule.onNodeWithTextId(R.string.all_at_once).assertIsDisplayed()

        composeTestRule.onNodeWithTextId(R.string.open_profile_statistics).checkEnabledButton()
    }

    private fun setContentToProfileBody(profileUiState: ProfileUiState) {
        composeTestRule.activity.setContent {
            AppTheme {
                ProfileScreenComponent(
                    profileUiState = profileUiState
                )
            }
        }
    }
}