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
import xelagurd.socialdating.ui.screen.ProfileStatisticsScreenComponent
import xelagurd.socialdating.ui.state.ProfileStatisticsUiState
import xelagurd.socialdating.ui.state.RequestStatus
import xelagurd.socialdating.ui.theme.AppTheme

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
    fun profileStatisticsScreen_loadingStatusAndEmptyData_loadingIndicator() {
        val profileStatisticsUiState = ProfileStatisticsUiState()

        setContentToProfileStatisticsBody(profileStatisticsUiState)

        composeTestRule.onNodeWithTagId(R.string.loading).assertIsDisplayed()
    }

    @Test
    fun profileStatisticsScreen_offlineStatusAndEmptyData_offlineText() {
        val profileStatisticsUiState =
            ProfileStatisticsUiState(dataRequestStatus = RequestStatus.ERROR)

        setContentToProfileStatisticsBody(profileStatisticsUiState)

        composeTestRule.onNodeWithTextId(R.string.no_internet_connection).assertIsDisplayed()
    }

    @Test
    fun profileStatisticsScreen_onlineStatusAndEmptyData_onlineText() {
        val profileStatisticsUiState =
            ProfileStatisticsUiState(dataRequestStatus = RequestStatus.SUCCESS)

        setContentToProfileStatisticsBody(profileStatisticsUiState)

        composeTestRule.onNodeWithTextId(R.string.no_data).assertIsDisplayed()
    }

    @Test
    fun profileStatisticsScreen_loadingStatusAndData_displayedData() {
        val profileStatisticsUiState = ProfileStatisticsUiState(
            entities = listOf(UserCategoryWithData(1, 50, 50, 1, "Category1")),
            entityIdToData = mapOf(
                1 to listOf(UserDefiningThemeWithData(1, 50, 50, 1, 1, "Theme1", "No", "Yes"))
            ),
            dataRequestStatus = RequestStatus.LOADING
        )

        assertDataIsDisplayed(profileStatisticsUiState)
    }

    @Test
    fun profileStatisticsScreen_offlineStatusAndData_displayedData() {
        val profileStatisticsUiState = ProfileStatisticsUiState(
            entities = listOf(UserCategoryWithData(1, 50, 50, 1, "Category1")),
            entityIdToData = mapOf(
                1 to listOf(UserDefiningThemeWithData(1, 50, 50, 1, 1, "Theme1", "No", "Yes"))
            ),
            dataRequestStatus = RequestStatus.ERROR
        )

        assertDataIsDisplayed(profileStatisticsUiState)
    }

    @Test
    fun profileStatisticsScreen_onlineStatusAndData_displayedData() {
        val profileStatisticsUiState = ProfileStatisticsUiState(
            entities = listOf(UserCategoryWithData(1, 50, 50, 1, "Category1")),
            entityIdToData = mapOf(
                1 to listOf(UserDefiningThemeWithData(1, 50, 50, 1, 1, "Theme1", "No", "Yes"))
            ),
            dataRequestStatus = RequestStatus.SUCCESS
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
            AppTheme {
                ProfileStatisticsScreenComponent(
                    profileStatisticsUiState = profileStatisticsUiState
                )
            }
        }
    }
}