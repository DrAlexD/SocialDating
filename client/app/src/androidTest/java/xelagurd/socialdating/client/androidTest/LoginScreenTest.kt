package xelagurd.socialdating.client.androidTest

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import xelagurd.socialdating.client.AndroidTestUtils.INPUT_TEXT
import xelagurd.socialdating.client.AndroidTestUtils.checkButtonAndClick
import xelagurd.socialdating.client.AndroidTestUtils.checkDisabledButton
import xelagurd.socialdating.client.AndroidTestUtils.checkEnabledButton
import xelagurd.socialdating.client.AndroidTestUtils.checkTextField
import xelagurd.socialdating.client.AndroidTestUtils.checkTextFieldAndInput
import xelagurd.socialdating.client.AndroidTestUtils.onNodeWithTextId
import xelagurd.socialdating.client.AndroidTestUtils.setContentToScreen
import xelagurd.socialdating.client.AndroidTestUtils.setContentToScreenAndRecompose
import xelagurd.socialdating.client.MainActivity
import xelagurd.socialdating.client.R
import xelagurd.socialdating.client.data.fake.FakeData
import xelagurd.socialdating.client.ui.form.LoginFormData
import xelagurd.socialdating.client.ui.screen.LoginScreenComponent
import xelagurd.socialdating.client.ui.state.LoginUiState

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

    private val loginFormData = FakeData.loginFormData

    @Test
    fun loginScreen_defaultParameters_displayedContentWithDisabledButton() {
        composeTestRule.setContentToScreen {
            LoginScreenComponent()
        }

        assertContentIsDisplayed()
        composeTestRule.onNodeWithTextId(R.string.login).checkDisabledButton()
    }

    @Test
    fun loginScreen_recomposition_displayedContentWithDisabledButton() {
        composeTestRule.setContentToScreenAndRecompose {
            LoginScreenComponent(loginUiState = LoginUiState())
        }

        assertContentIsDisplayed()
        composeTestRule.onNodeWithTextId(R.string.login).checkDisabledButton()
    }

    @Test
    fun loginScreen_validFormData_displayedContentWithEnabledButton() {
        composeTestRule.setContentToScreen {
            LoginScreenComponent(loginUiState = LoginUiState(formData = loginFormData))
        }

        assertContentIsDisplayed()
        composeTestRule.onNodeWithTextId(R.string.login).checkEnabledButton()
    }

    @Test
    fun loginScreen_allActions_calledAllActions() {
        var changedFormData: LoginFormData? = null
        var isLoginClicked = false
        var isRegistrationClicked = false
        var isOfflineModeClicked = false

        composeTestRule.setContentToScreen {
            LoginScreenComponent(
                loginUiState = LoginUiState(formData = loginFormData),
                onSuccessLogin = {},
                onRegistrationClick = { isRegistrationClicked = true },
                onValueChange = { changedFormData = it },
                onLoginClick = { isLoginClicked = true },
                onOfflineModeClick = { isOfflineModeClicked = true }
            )
        }

        composeTestRule.onNodeWithTextId(R.string.username).checkTextFieldAndInput(INPUT_TEXT)
        assertEquals(true, changedFormData?.username?.contains(INPUT_TEXT))

        composeTestRule.onNodeWithTextId(R.string.password).checkTextFieldAndInput(INPUT_TEXT)
        assertEquals(true, changedFormData?.password?.contains(INPUT_TEXT))

        composeTestRule.onNodeWithTextId(R.string.login).checkButtonAndClick()
        assertTrue(isLoginClicked)

        composeTestRule.onNodeWithTextId(R.string.register).checkButtonAndClick()
        assertTrue(isRegistrationClicked)

        composeTestRule.onNodeWithTextId(R.string.offline_mode).checkButtonAndClick()
        assertTrue(isOfflineModeClicked)
    }

    private fun assertContentIsDisplayed() {
        composeTestRule.onNodeWithTextId(R.string.username).checkTextField()
        composeTestRule.onNodeWithTextId(R.string.password).checkTextField()

        composeTestRule.onNodeWithTextId(R.string.or).assertIsDisplayed()
        composeTestRule.onNodeWithTextId(R.string.register).checkEnabledButton()
        composeTestRule.onNodeWithTextId(R.string.offline_mode).checkEnabledButton()
    }
}