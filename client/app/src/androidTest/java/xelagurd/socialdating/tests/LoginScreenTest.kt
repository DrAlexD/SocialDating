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
import xelagurd.socialdating.checkDisabledButton
import xelagurd.socialdating.checkEnabledButton
import xelagurd.socialdating.checkTextField
import xelagurd.socialdating.data.model.details.LoginDetails
import xelagurd.socialdating.onNodeWithTagId
import xelagurd.socialdating.onNodeWithTextId
import xelagurd.socialdating.ui.screen.LoginScreenComponent
import xelagurd.socialdating.ui.state.LoginUiState
import xelagurd.socialdating.ui.state.RequestStatus
import xelagurd.socialdating.ui.theme.AppTheme

@HiltAndroidTest
class LoginScreenTest {
    @get:Rule(order = 0)
    val hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 1)
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    @Before
    fun setup() {
        hiltRule.inject()
    }

    @Test
    fun loginScreen_assertContentIsDisplayed() {
        val loginUiState = LoginUiState()

        assertContentIsDisplayed(loginUiState)
    }

    @Test
    fun loginScreen_loadingStatus_loadingIndicator() {
        val loginUiState = LoginUiState(actionRequestStatus = RequestStatus.LOADING)

        setContentToLoginBody(loginUiState)

        composeTestRule.onNodeWithTagId(R.string.loading).assertIsDisplayed()
    }

    @Test
    fun loginScreen_failedStatus_failedText() {
        val loginUiState = LoginUiState(actionRequestStatus = RequestStatus.FAILED)

        setContentToLoginBody(loginUiState)

        composeTestRule.onNodeWithTextId(R.string.failed_login).assertIsDisplayed()
    }

    @Test
    fun loginScreen_errorStatus_errorText() {
        val loginUiState = LoginUiState(actionRequestStatus = RequestStatus.ERROR)

        setContentToLoginBody(loginUiState)

        composeTestRule.onNodeWithTextId(R.string.no_internet_connection).assertIsDisplayed()
    }

    @Test
    fun loginScreen_emptyData_disabledButton() {
        val loginUiState = LoginUiState(
            formDetails = LoginDetails("", "")
        )

        assertLoginButtonIsDisabled(loginUiState)
    }

    @Test
    fun loginScreen_allData_enabledButton() {
        val loginUiState = LoginUiState(
            formDetails = LoginDetails("login", "password")
        )

        assertLoginButtonIsEnabled(loginUiState)
    }

    @Test
    fun loginScreen_emptyUsername_disabledButton() {
        val loginUiState = LoginUiState(
            formDetails = LoginDetails("", "password")
        )

        assertLoginButtonIsDisabled(loginUiState)
    }

    @Test
    fun loginScreen_emptyPassword_disabledButton() {
        val loginUiState = LoginUiState(
            formDetails = LoginDetails("login", "")
        )

        assertLoginButtonIsDisabled(loginUiState)
    }

    private fun assertLoginButtonIsEnabled(loginUiState: LoginUiState) {
        setContentToLoginBody(loginUiState)

        composeTestRule.onNodeWithTextId(R.string.login).checkEnabledButton()
    }

    private fun assertLoginButtonIsDisabled(loginUiState: LoginUiState) {
        setContentToLoginBody(loginUiState)

        composeTestRule.onNodeWithTextId(R.string.login).checkDisabledButton()
    }

    private fun assertContentIsDisplayed(loginUiState: LoginUiState) {
        setContentToLoginBody(loginUiState)

        composeTestRule.onNodeWithTextId(R.string.username).checkTextField()
        composeTestRule.onNodeWithTextId(R.string.password).checkTextField()

        composeTestRule.onNodeWithTextId(R.string.register).checkEnabledButton()
    }

    private fun setContentToLoginBody(loginUiState: LoginUiState) {
        composeTestRule.activity.setContent {
            AppTheme {
                LoginScreenComponent(loginUiState = loginUiState)
            }
        }
    }
}