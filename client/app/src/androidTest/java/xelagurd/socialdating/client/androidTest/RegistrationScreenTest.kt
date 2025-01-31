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
import xelagurd.socialdating.client.data.model.details.RegistrationDetails
import xelagurd.socialdating.client.data.model.enums.Gender
import xelagurd.socialdating.client.data.model.enums.Purpose
import xelagurd.socialdating.client.onNodeWithTagId
import xelagurd.socialdating.client.onNodeWithTextId
import xelagurd.socialdating.client.onNodeWithTextIdWithColon
import xelagurd.socialdating.client.ui.screen.RegistrationScreenComponent
import xelagurd.socialdating.client.ui.state.RegistrationUiState
import xelagurd.socialdating.client.ui.state.RequestStatus
import xelagurd.socialdating.client.ui.theme.AppTheme

@HiltAndroidTest
class RegistrationScreenTest {
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
        val registrationUiState = RegistrationUiState()

        assertContentIsDisplayed(registrationUiState)
    }

    @Test
    fun registrationScreen_loadingState_loadingIndicator() {
        val registrationUiState = RegistrationUiState(actionRequestStatus = RequestStatus.LOADING)

        setContentToRegistrationBody(registrationUiState)

        composeTestRule.onNodeWithTagId(R.string.loading).assertIsDisplayed()
    }

    @Test
    fun registrationScreen_failureState_failureText() {
        val failureText = "Failure Text"
        val registrationUiState = RegistrationUiState(
            actionRequestStatus = RequestStatus.FAILURE(failureText)
        )

        setContentToRegistrationBody(registrationUiState)

        composeTestRule.onNodeWithText(failureText).assertIsDisplayed()
    }

    @Test
    fun registrationScreen_errorState_errorText() {
        val errorText = "Error Text"
        val registrationUiState = RegistrationUiState(
            actionRequestStatus = RequestStatus.ERROR(errorText)
        )

        setContentToRegistrationBody(registrationUiState)

        composeTestRule.onNodeWithText(errorText).assertIsDisplayed()
    }

    @Test
    fun registrationScreen_emptyData_disabledButton() {
        val registrationUiState = RegistrationUiState(
            formDetails = RegistrationDetails("", null, "", "", "", "", "", "", null)
        )

        assertRegisterButtonIsDisabled(registrationUiState)
    }

    @Test
    fun registrationScreen_allData_enabledButton() {
        val registrationUiState = RegistrationUiState(
            formDetails = RegistrationDetails(
                "Alex", Gender.MALE, "log", "123", "123", "email", "20", "Mos", Purpose.ALL_AT_ONCE
            )
        )

        assertRegisterButtonIsEnabled(registrationUiState)
    }

    @Test
    fun registrationScreen_emptyEmail_enabledButton() {
        val registrationUiState = RegistrationUiState(
            formDetails = RegistrationDetails(
                "Alex", Gender.MALE, "log", "123", "123", "", "20", "Mos", Purpose.ALL_AT_ONCE
            )
        )

        assertRegisterButtonIsEnabled(registrationUiState)
    }

    @Test
    fun registrationScreen_emptyName_disabledButton() {
        val registrationUiState = RegistrationUiState(
            formDetails = RegistrationDetails(
                "", Gender.MALE, "log", "123", "123", "email", "20", "Mos", Purpose.ALL_AT_ONCE
            )
        )

        assertRegisterButtonIsDisabled(registrationUiState)
    }

    @Test
    fun registrationScreen_emptyGender_disabledButton() {
        val registrationUiState = RegistrationUiState(
            formDetails = RegistrationDetails(
                "Alex", null, "log", "123", "123", "email", "20", "Mos", Purpose.ALL_AT_ONCE
            )
        )

        assertRegisterButtonIsDisabled(registrationUiState)
    }

    @Test
    fun registrationScreen_emptyUsername_disabledButton() {
        val registrationUiState = RegistrationUiState(
            formDetails = RegistrationDetails(
                "Alex", Gender.MALE, "", "123", "123", "email", "20", "Mos", Purpose.ALL_AT_ONCE
            )
        )

        assertRegisterButtonIsDisabled(registrationUiState)
    }

    @Test
    fun registrationScreen_emptyPassword_disabledButton() {
        val registrationUiState = RegistrationUiState(
            formDetails = RegistrationDetails(
                "Alex", Gender.MALE, "log", "", "123", "email", "20", "Mos", Purpose.ALL_AT_ONCE
            )
        )

        assertRegisterButtonIsDisabled(registrationUiState)
    }

    @Test
    fun registrationScreen_emptyRepeatedPassword_disabledButton() {
        val registrationUiState = RegistrationUiState(
            formDetails = RegistrationDetails(
                "Alex", Gender.MALE, "log", "123", "", "email", "20", "Mos", Purpose.ALL_AT_ONCE
            )
        )

        assertRegisterButtonIsDisabled(registrationUiState)
    }

    @Test
    fun registrationScreen_passwordIsNotEqualToRepeatedPassword_disabledButton() {
        val registrationUiState = RegistrationUiState(
            formDetails = RegistrationDetails(
                "Alex", Gender.MALE, "log", "123", "456", "email", "20", "Mos", Purpose.ALL_AT_ONCE
            )
        )

        assertRegisterButtonIsDisabled(registrationUiState)
    }

    @Test
    fun registrationScreen_emptyAge_disabledButton() {
        val registrationUiState = RegistrationUiState(
            formDetails = RegistrationDetails(
                "Alex", Gender.MALE, "log", "123", "123", "email", "", "Mos", Purpose.ALL_AT_ONCE
            )
        )

        assertRegisterButtonIsDisabled(registrationUiState)
    }

    @Test
    fun registrationScreen_emptyCity_disabledButton() {
        val registrationUiState = RegistrationUiState(
            formDetails = RegistrationDetails(
                "Alex", Gender.MALE, "log", "123", "123", "email", "20", "", Purpose.ALL_AT_ONCE
            )
        )

        assertRegisterButtonIsDisabled(registrationUiState)
    }

    @Test
    fun registrationScreen_emptyPurpose_disabledButton() {
        val registrationUiState = RegistrationUiState(
            formDetails = RegistrationDetails(
                "Alex", Gender.MALE, "log", "123", "123", "email", "20", "Mos", null
            )
        )

        assertRegisterButtonIsDisabled(registrationUiState)
    }

    private fun assertRegisterButtonIsEnabled(registrationUiState: RegistrationUiState) {
        setContentToRegistrationBody(registrationUiState)

        composeTestRule.onNodeWithTextId(R.string.register).checkEnabledButton()
    }

    private fun assertRegisterButtonIsDisabled(registrationUiState: RegistrationUiState) {
        setContentToRegistrationBody(registrationUiState)

        composeTestRule.onNodeWithTextId(R.string.register).checkDisabledButton()
    }

    private fun assertContentIsDisplayed(registrationUiState: RegistrationUiState) {
        setContentToRegistrationBody(registrationUiState)

        composeTestRule.onNodeWithTextId(R.string.username).checkTextField()
        composeTestRule.onNodeWithTextId(R.string.name).checkTextField()

        composeTestRule.onNodeWithTextIdWithColon(R.string.gender).assertIsDisplayed()
        composeTestRule.onNodeWithTextId(R.string.male).assertIsDisplayed()
        composeTestRule.onNodeWithTextId(R.string.female).assertIsDisplayed()

        composeTestRule.onNodeWithTextId(R.string.email_optional).checkTextField()
        composeTestRule.onNodeWithTextId(R.string.age).checkTextField()
        composeTestRule.onNodeWithTextId(R.string.city).checkTextField()

        composeTestRule.onNodeWithTextIdWithColon(R.string.purpose).assertIsDisplayed()
        composeTestRule.onNodeWithTextId(R.string.friends).assertIsDisplayed()
        composeTestRule.onNodeWithTextId(R.string.relationships).assertIsDisplayed()
        composeTestRule.onNodeWithTextId(R.string.all_at_once).assertIsDisplayed()

        composeTestRule.onNodeWithTextId(R.string.password).checkTextField()
        composeTestRule.onNodeWithTextId(R.string.repeat_password).checkTextField()
    }

    private fun setContentToRegistrationBody(registrationUiState: RegistrationUiState) {
        composeTestRule.activity.setContent {
            AppTheme {
                RegistrationScreenComponent(
                    registrationUiState = registrationUiState
                )
            }
        }
    }
}