package xelagurd.socialdating.tests

import androidx.activity.compose.setContent
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsNotDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithText
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import xelagurd.socialdating.MainActivity
import xelagurd.socialdating.R
import xelagurd.socialdating.checkButtonAndClick
import xelagurd.socialdating.data.model.additional.UserCategoryWithData
import xelagurd.socialdating.data.model.additional.UserDefiningThemeWithData
import xelagurd.socialdating.onNodeWithContentDescriptionId
import xelagurd.socialdating.onNodeWithTagId
import xelagurd.socialdating.onNodeWithTextId
import xelagurd.socialdating.ui.screen.ProfileStatisticsBody
import xelagurd.socialdating.ui.state.InternetStatus
import xelagurd.socialdating.ui.state.ProfileStatisticsUiState

@HiltAndroidTest
class ProfileStatisticsScreenTest {
    @get:Rule(order = 0)
    val hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 1)
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    @Before
    fun setup() {
        hiltRule.inject()
    }

    @Test
    fun profileStatisticsScreen_loadingStatusAndEmptyData_loadingText() {
        val profileStatisticsUiState = ProfileStatisticsUiState()

        setContentToProfileStatisticsBody(profileStatisticsUiState)

        composeTestRule.onNodeWithTextId(R.string.loading).assertIsDisplayed()
    }

    @Test
    fun profileStatisticsScreen_offlineStatusAndEmptyData_offlineText() {
        val profileStatisticsUiState =
            ProfileStatisticsUiState(internetStatus = InternetStatus.OFFLINE)

        setContentToProfileStatisticsBody(profileStatisticsUiState)

        composeTestRule.onNodeWithTextId(R.string.no_internet_connection).assertIsDisplayed()
    }

    @Test
    fun profileStatisticsScreen_onlineStatusAndEmptyData_onlineText() {
        val profileStatisticsUiState =
            ProfileStatisticsUiState(internetStatus = InternetStatus.ONLINE)

        setContentToProfileStatisticsBody(profileStatisticsUiState)

        composeTestRule.onNodeWithTextId(R.string.no_data).assertIsDisplayed()
    }

    @Test
    fun profileStatisticsScreen_loadingStatusAndData_displayedData() {
        val profileStatisticsUiState = ProfileStatisticsUiState(
            userCategories = listOf(UserCategoryWithData(1, 50, 50, 1, "Category1")),
            userCategoryToDefiningThemes = mapOf(
                1 to listOf(UserDefiningThemeWithData(1, 50, 50, 1, 1, "Theme1", "No", "Yes"))
            ),
            internetStatus = InternetStatus.LOADING
        )

        assertDataIsDisplayed(profileStatisticsUiState)
    }

    @Test
    fun profileStatisticsScreen_offlineStatusAndData_displayedData() {
        val profileStatisticsUiState = ProfileStatisticsUiState(
            userCategories = listOf(UserCategoryWithData(1, 50, 50, 1, "Category1")),
            userCategoryToDefiningThemes = mapOf(
                1 to listOf(UserDefiningThemeWithData(1, 50, 50, 1, 1, "Theme1", "No", "Yes"))
            ),
            internetStatus = InternetStatus.OFFLINE
        )

        assertDataIsDisplayed(profileStatisticsUiState)
    }

    @Test
    fun profileStatisticsScreen_onlineStatusAndData_displayedData() {
        val profileStatisticsUiState = ProfileStatisticsUiState(
            userCategories = listOf(UserCategoryWithData(1, 50, 50, 1, "Category1")),
            userCategoryToDefiningThemes = mapOf(
                1 to listOf(UserDefiningThemeWithData(1, 50, 50, 1, 1, "Theme1", "No", "Yes"))
            ),
            internetStatus = InternetStatus.ONLINE
        )

        assertDataIsDisplayed(profileStatisticsUiState)
    }

    private fun assertDataIsDisplayed(profileStatisticsUiState: ProfileStatisticsUiState) {
        setContentToProfileStatisticsBody(profileStatisticsUiState)

        composeTestRule.onNodeWithText("Category1").assertIsDisplayed()
        composeTestRule.onNodeWithText("Theme1").assertIsNotDisplayed()
        composeTestRule.onNodeWithText("No").assertIsNotDisplayed()
        composeTestRule.onNodeWithText("Yes").assertIsNotDisplayed()
        composeTestRule.onNodeWithTagId(R.string.progress_indicator).assertIsNotDisplayed()

        composeTestRule.onNodeWithContentDescriptionId(R.string.expand_list)
            .checkButtonAndClick()

        composeTestRule.onNodeWithText("Category1").assertIsDisplayed()
        composeTestRule.onNodeWithText("Theme1").assertIsDisplayed()
        composeTestRule.onNodeWithText("No").assertIsDisplayed()
        composeTestRule.onNodeWithText("Yes").assertIsDisplayed()
        composeTestRule.onNodeWithTagId(R.string.progress_indicator).assertIsDisplayed()
    }

    private fun setContentToProfileStatisticsBody(profileStatisticsUiState: ProfileStatisticsUiState) {
        composeTestRule.activity.setContent {
            ProfileStatisticsBody(
                profileStatisticsUiState = profileStatisticsUiState
            )
        }
    }
}