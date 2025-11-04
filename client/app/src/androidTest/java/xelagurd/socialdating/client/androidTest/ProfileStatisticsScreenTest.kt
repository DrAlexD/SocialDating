package xelagurd.socialdating.client.androidTest

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
import xelagurd.socialdating.client.MainActivity
import xelagurd.socialdating.client.R
import xelagurd.socialdating.client.checkButtonAndClick
import xelagurd.socialdating.client.data.fake.FakeData
import xelagurd.socialdating.client.onNodeWithContentDescriptionId
import xelagurd.socialdating.client.onNodeWithTagId
import xelagurd.socialdating.client.ui.screen.ProfileStatisticsScreenComponent
import xelagurd.socialdating.client.ui.state.ProfileStatisticsUiState
import xelagurd.socialdating.client.ui.state.RequestStatus
import xelagurd.socialdating.client.ui.theme.AppTheme

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

    private val userCategoryWithData = FakeData.userCategoryWithData
    private val userDefiningThemeWithData = FakeData.userDefiningThemeWithData

    @Test
    fun profileStatisticsScreen_loadingStateAndEmptyData_loadingIndicator() {
        val profileStatisticsUiState = ProfileStatisticsUiState()

        setContentToProfileStatisticsBody(profileStatisticsUiState)

        composeTestRule.onNodeWithTagId(R.string.loading).assertIsDisplayed()
    }

    @Test
    fun profileStatisticsScreen_errorStateAndEmptyData_errorText() {
        val errorText = FakeData.ERROR_TEXT
        val profileStatisticsUiState = ProfileStatisticsUiState(
            dataRequestStatus = RequestStatus.ERROR(errorText)
        )

        setContentToProfileStatisticsBody(profileStatisticsUiState)

        composeTestRule.onNodeWithText(errorText).assertIsDisplayed()
    }

    @Test
    fun profileStatisticsScreen_failureStateAndEmptyData_failureText() {
        val failureText = FakeData.FAILURE_TEXT
        val profileStatisticsUiState = ProfileStatisticsUiState(
            dataRequestStatus = RequestStatus.FAILURE(failureText)
        )

        setContentToProfileStatisticsBody(profileStatisticsUiState)

        composeTestRule.onNodeWithText(failureText).assertIsDisplayed()
    }

    @Test
    fun profileStatisticsScreen_loadingStateAndData_displayedData() {
        val profileStatisticsUiState = ProfileStatisticsUiState(
            entities = listOf(userCategoryWithData),
            entityIdToData = mapOf(userCategoryWithData.categoryId to listOf(userDefiningThemeWithData)),
            dataRequestStatus = RequestStatus.LOADING
        )

        assertDataIsDisplayed(profileStatisticsUiState)
    }

    @Test
    fun profileStatisticsScreen_errorStateAndData_displayedData() {
        val profileStatisticsUiState = ProfileStatisticsUiState(
            entities = listOf(userCategoryWithData),
            entityIdToData = mapOf(userCategoryWithData.categoryId to listOf(userDefiningThemeWithData)),
            dataRequestStatus = RequestStatus.ERROR()
        )

        assertDataIsDisplayed(profileStatisticsUiState)
    }

    @Test
    fun profileStatisticsScreen_failureStateAndData_displayedData() {
        val profileStatisticsUiState = ProfileStatisticsUiState(
            entities = listOf(userCategoryWithData),
            entityIdToData = mapOf(userCategoryWithData.categoryId to listOf(userDefiningThemeWithData)),
            dataRequestStatus = RequestStatus.FAILURE()
        )

        assertDataIsDisplayed(profileStatisticsUiState)
    }

    @Test
    fun profileStatisticsScreen_successStateAndData_displayedData() {
        val profileStatisticsUiState = ProfileStatisticsUiState(
            entities = listOf(userCategoryWithData),
            entityIdToData = mapOf(userCategoryWithData.categoryId to listOf(userDefiningThemeWithData)),
            dataRequestStatus = RequestStatus.SUCCESS
        )

        assertDataIsDisplayed(profileStatisticsUiState)
    }

    private fun assertDataIsDisplayed(profileStatisticsUiState: ProfileStatisticsUiState) {
        setContentToProfileStatisticsBody(profileStatisticsUiState)

        composeTestRule.onNodeWithText(userCategoryWithData.categoryName).assertIsDisplayed()
        composeTestRule.onNodeWithText(userDefiningThemeWithData.definingThemeName).assertIsNotDisplayed()
        composeTestRule.onNodeWithText(userDefiningThemeWithData.definingThemeFromOpinion).assertIsNotDisplayed()
        composeTestRule.onNodeWithText(userDefiningThemeWithData.definingThemeToOpinion).assertIsNotDisplayed()
        composeTestRule.onNodeWithTagId(R.string.progress_indicator).assertIsNotDisplayed()

        composeTestRule.onNodeWithContentDescriptionId(R.string.expand_list).checkButtonAndClick()

        composeTestRule.onNodeWithText(userCategoryWithData.categoryName).assertIsDisplayed()
        composeTestRule.onNodeWithText(userDefiningThemeWithData.definingThemeName).assertIsDisplayed()
        composeTestRule.onNodeWithText(userDefiningThemeWithData.definingThemeFromOpinion).assertIsDisplayed()
        composeTestRule.onNodeWithText(userDefiningThemeWithData.definingThemeToOpinion).assertIsDisplayed()
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