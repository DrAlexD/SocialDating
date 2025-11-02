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
import xelagurd.socialdating.client.MainActivity
import xelagurd.socialdating.client.R
import xelagurd.socialdating.client.checkDisabledButton
import xelagurd.socialdating.client.checkEnabledButton
import xelagurd.socialdating.client.checkTextField
import xelagurd.socialdating.client.onNodeWithTagId
import xelagurd.socialdating.client.onNodeWithTextId
import xelagurd.socialdating.client.ui.form.LoginFormData
import xelagurd.socialdating.client.ui.screen.LoginScreenComponent
import xelagurd.socialdating.client.ui.state.LoginUiState
import xelagurd.socialdating.client.ui.state.RequestStatus
import xelagurd.socialdating.client.ui.theme.AppTheme

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
    fun loginScreen_loadingState_loadingIndicator() {
        val loginUiState = LoginUiState(actionRequestStatus = RequestStatus.LOADING)

        setContentToLoginBody(loginUiState)

        composeTestRule.onNodeWithTagId(R.string.loading).assertIsDisplayed()
    }

    @Test
    fun loginScreen_failureState_failureText() {
        val failureText = "Failure Text"
        val loginUiState = LoginUiState(actionRequestStatus = RequestStatus.FAILURE(failureText))

        setContentToLoginBody(loginUiState)

        composeTestRule.onNodeWithText(failureText).assertIsDisplayed()
    }

    @Test
    fun loginScreen_errorState_errorText() {
        val errorText = "Error Text"
        val loginUiState = LoginUiState(actionRequestStatus = RequestStatus.ERROR(errorText))

        setContentToLoginBody(loginUiState)

        composeTestRule.onNodeWithText(errorText).assertIsDisplayed()
    }

    @Test
    fun loginScreen_emptyData_disabledButton() {
        val loginUiState = LoginUiState(
            formData = LoginFormData("", "")
        )

        assertLoginButtonIsDisabled(loginUiState)
    }

    @Test
    fun loginScreen_allData_enabledButton() {
        val loginUiState = LoginUiState(
            formData = LoginFormData("login", "password")
        )

        assertLoginButtonIsEnabled(loginUiState)
    }

    @Test
    fun loginScreen_emptyUsername_disabledButton() {
        val loginUiState = LoginUiState(
            formData = LoginFormData("", "password")
        )

        assertLoginButtonIsDisabled(loginUiState)
    }

    @Test
    fun loginScreen_emptyPassword_disabledButton() {
        val loginUiState = LoginUiState(
            formData = LoginFormData("login", "")
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