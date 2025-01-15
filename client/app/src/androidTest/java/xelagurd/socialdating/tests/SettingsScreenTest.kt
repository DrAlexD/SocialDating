package xelagurd.socialdating.tests

import androidx.activity.compose.setContent
import androidx.compose.ui.res.stringResource
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
import xelagurd.socialdating.ui.screen.ComponentWithRequestStatus
import xelagurd.socialdating.ui.screen.SettingsDetailsBody
import xelagurd.socialdating.ui.state.RegistrationUiState
import xelagurd.socialdating.ui.state.RequestStatus
import xelagurd.socialdating.ui.state.SettingsUiState

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
    fun settingsScreen_loadingStatus_loadingIndicator() {
        val settingsUiState = SettingsUiState(requestStatus = RequestStatus.LOADING)

        setContentToSettingsBody(settingsUiState)

        composeTestRule.onNodeWithTagId(R.string.loading).assertIsDisplayed()
    }

    @Test
    fun settingsScreen_errorStatus_errorText() {
        val settingsUiState = SettingsUiState(requestStatus = RequestStatus.ERROR)

        setContentToSettingsBody(settingsUiState)

        composeTestRule.onNodeWithTextId(R.string.unknown_error).assertIsDisplayed()
    }

    private fun assertContentIsDisplayed(settingsUiState: SettingsUiState) {
        setContentToSettingsBody(settingsUiState)

        composeTestRule.onNodeWithTextId(R.string.logout).checkEnabledButton()
    }

    private fun setContentToSettingsBody(settingsUiState: SettingsUiState) {
        composeTestRule.activity.setContent {
            ComponentWithRequestStatus(
                requestStatus = settingsUiState.requestStatus,
                onSuccess = { },
                failedText = "",
                errorText = stringResource(R.string.unknown_error)
            ) {
                SettingsDetailsBody(
                    onLogoutClick = { }
                )
            }
        }
    }
}