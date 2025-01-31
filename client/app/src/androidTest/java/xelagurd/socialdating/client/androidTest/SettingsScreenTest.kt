package xelagurd.socialdating.client.androidTest

import androidx.activity.compose.setContent
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import xelagurd.socialdating.client.MainActivity
import xelagurd.socialdating.client.R
import xelagurd.socialdating.client.checkEnabledButton
import xelagurd.socialdating.client.onNodeWithTagId
import xelagurd.socialdating.client.onNodeWithTextId
import xelagurd.socialdating.client.ui.screen.SettingsScreenComponent
import xelagurd.socialdating.client.ui.state.RequestStatus
import xelagurd.socialdating.client.ui.state.SettingsUiState
import xelagurd.socialdating.client.ui.theme.AppTheme

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