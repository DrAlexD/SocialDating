package xelagurd.socialdating.tests

import androidx.activity.compose.setContent
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import xelagurd.socialdating.MainActivity
import xelagurd.socialdating.R
import xelagurd.socialdating.checkEnabledButton
import xelagurd.socialdating.onNodeWithTagId
import xelagurd.socialdating.onNodeWithTextId
import xelagurd.socialdating.ui.screen.SettingsScreenComponent
import xelagurd.socialdating.ui.state.RequestStatus
import xelagurd.socialdating.ui.state.SettingsUiState
import xelagurd.socialdating.ui.theme.AppTheme

@HiltAndroidTest
class SettingsScreenTest {
    @get:Rule(order = 0)
    val hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 1)
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    @Before
    fun setup() {
        hiltRule.inject()
    }

    @Test
    fun registrationScreen_assertContentIsDisplayed() {
        val settingsUiState = SettingsUiState()

        assertContentIsDisplayed(settingsUiState)
    }

    @Test
    fun settingsScreen_loadingState_loadingIndicator() {
        val settingsUiState = SettingsUiState(actionRequestStatus = RequestStatus.LOADING)

        setContentToSettingsBody(settingsUiState)

        composeTestRule.onNodeWithTagId(R.string.loading).assertIsDisplayed()
    }

    private fun assertContentIsDisplayed(settingsUiState: SettingsUiState) {
        setContentToSettingsBody(settingsUiState)

        composeTestRule.onNodeWithTextId(R.string.logout).checkEnabledButton()
    }

    private fun setContentToSettingsBody(settingsUiState: SettingsUiState) {
        composeTestRule.activity.setContent {
            AppTheme {
                SettingsScreenComponent(
                    settingsUiState = settingsUiState
                )
            }
        }
    }
}