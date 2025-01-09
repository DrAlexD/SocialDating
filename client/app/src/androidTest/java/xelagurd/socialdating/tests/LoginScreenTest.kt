package xelagurd.socialdating.tests

import androidx.activity.compose.setContent
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsEnabled
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import xelagurd.socialdating.MainActivity
import xelagurd.socialdating.R
import xelagurd.socialdating.data.model.additional.LoginDetails
import xelagurd.socialdating.onNodeWithTextId
import xelagurd.socialdating.ui.screen.LoginBody
import xelagurd.socialdating.ui.state.LoginUiState
import xelagurd.socialdating.ui.state.RequestStatus

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
    fun loginScreen_failedStatus_failedText() {
        val loginUiState = LoginUiState(requestStatus = RequestStatus.FAILED)

        composeTestRule.activity.setContent {
            LoginBody(
                loginUiState = loginUiState,
                onValueChange = {},
                onLoginClick = {},
                onSuccessLogin = {},
                onRegistrationClick = {}
            )
        }

        composeTestRule.onNodeWithTextId(R.string.failed_login).assertIsDisplayed()
    }

    @Test
    fun loginScreen_errorStatus_errorText() {
        val loginUiState = LoginUiState(requestStatus = RequestStatus.ERROR)

        composeTestRule.activity.setContent {
            LoginBody(
                loginUiState = loginUiState,
                onValueChange = {},
                onLoginClick = {},
                onSuccessLogin = {},
                onRegistrationClick = {}
            )
        }

        composeTestRule.onNodeWithTextId(R.string.no_internet_connection).assertIsDisplayed()
    }

    @Test
    fun loginScreen_emptyData_disabledButton() {
        val loginUiState = LoginUiState(
            LoginDetails("", ""),
            requestStatus = RequestStatus.UNDEFINED
        )

        composeTestRule.activity.setContent {
            LoginBody(
                loginUiState = loginUiState,
                onValueChange = {},
                onLoginClick = {},
                onSuccessLogin = {},
                onRegistrationClick = {}
            )
        }

        composeTestRule.onNodeWithTextId(R.string.login).assertIsNotEnabled()
        composeTestRule.onNodeWithTextId(R.string.register).assertIsEnabled()
    }

    @Test
    fun loginScreen_allData_enabledButton() {
        val loginUiState = LoginUiState(
            LoginDetails("abc", "123"),
            requestStatus = RequestStatus.UNDEFINED
        )

        composeTestRule.activity.setContent {
            LoginBody(
                loginUiState = loginUiState,
                onValueChange = {},
                onLoginClick = {},
                onSuccessLogin = {},
                onRegistrationClick = {}
            )
        }

        composeTestRule.onNodeWithTextId(R.string.login).assertIsEnabled()
        composeTestRule.onNodeWithTextId(R.string.register).assertIsEnabled()
    }

    @Test
    fun loginScreen_emptyUsername_disabledButton() {
        val loginUiState = LoginUiState(
            LoginDetails("", "123"),
            requestStatus = RequestStatus.UNDEFINED
        )

        composeTestRule.activity.setContent {
            LoginBody(
                loginUiState = loginUiState,
                onValueChange = {},
                onLoginClick = {},
                onSuccessLogin = {},
                onRegistrationClick = {}
            )
        }

        composeTestRule.onNodeWithTextId(R.string.login).assertIsNotEnabled()
        composeTestRule.onNodeWithTextId(R.string.register).assertIsEnabled()
    }

    @Test
    fun loginScreen_emptyPassword_disabledButton() {
        val loginUiState = LoginUiState(
            LoginDetails("abc", ""),
            requestStatus = RequestStatus.UNDEFINED
        )

        composeTestRule.activity.setContent {
            LoginBody(
                loginUiState = loginUiState,
                onValueChange = {},
                onLoginClick = {},
                onSuccessLogin = {},
                onRegistrationClick = {}
            )
        }

        composeTestRule.onNodeWithTextId(R.string.login).assertIsNotEnabled()
        composeTestRule.onNodeWithTextId(R.string.register).assertIsEnabled()
    }
}