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
import xelagurd.socialdating.client.AndroidTestUtils.checkEnabledButton
import xelagurd.socialdating.client.AndroidTestUtils.onNodeWithTagId
import xelagurd.socialdating.client.AndroidTestUtils.onNodeWithTextId
import xelagurd.socialdating.client.AndroidTestUtils.onNodeWithTextIdWithColon
import xelagurd.socialdating.client.MainActivity
import xelagurd.socialdating.client.R
import xelagurd.socialdating.client.data.fake.FakeData
import xelagurd.socialdating.client.ui.screen.ProfileScreenComponent
import xelagurd.socialdating.client.ui.state.ProfileUiState
import xelagurd.socialdating.client.ui.state.RequestStatus
import xelagurd.socialdating.client.ui.theme.AppTheme

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

    private val user = FakeData.users[0]

    @Test
    fun profileScreen_loadingStateAndEmptyData_loadingIndicator() {
        val profileUiState = ProfileUiState()

        setContentToProfileBody(profileUiState)

        composeTestRule.onNodeWithTagId(R.string.loading).assertIsDisplayed()
    }

    @Test
    fun profileScreen_errorStateAndEmptyData_errorText() {
        val errorText = FakeData.ERROR_TEXT
        val profileUiState = ProfileUiState(
            dataRequestStatus = RequestStatus.ERROR(errorText)
        )

        setContentToProfileBody(profileUiState)

        composeTestRule.onNodeWithText(errorText).assertIsDisplayed()
    }

    @Test
    fun profileScreen_failureStateAndEmptyData_failureText() {
        val failureText = FakeData.FAILURE_TEXT
        val profileUiState = ProfileUiState(
            dataRequestStatus = RequestStatus.FAILURE(failureText)
        )

        setContentToProfileBody(profileUiState)

        composeTestRule.onNodeWithText(failureText).assertIsDisplayed()
    }

    @Test
    fun profileScreen_loadingStateAndData_displayedData() {
        val profileUiState = ProfileUiState(
            entity = user,
            dataRequestStatus = RequestStatus.LOADING
        )

        assertDataIsDisplayed(profileUiState)
    }

    @Test
    fun profileScreen_errorStateAndData_displayedData() {
        val profileUiState = ProfileUiState(
            entity = user,
            dataRequestStatus = RequestStatus.ERROR()
        )

        assertDataIsDisplayed(profileUiState)
    }

    @Test
    fun profileScreen_failureStateAndData_displayedData() {
        val profileUiState = ProfileUiState(
            entity = user,
            dataRequestStatus = RequestStatus.FAILURE()
        )

        assertDataIsDisplayed(profileUiState)
    }

    @Test
    fun profileScreen_successStateAndData_displayedData() {
        val profileUiState = ProfileUiState(
            entity = user,
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

        composeTestRule.onNodeWithText(user.username).assertIsDisplayed()
        composeTestRule.onNodeWithText(user.name).assertIsDisplayed()
        composeTestRule.onNodeWithText(user.age.toString()).assertIsDisplayed()
        composeTestRule.onNodeWithText(user.city).assertIsDisplayed()
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