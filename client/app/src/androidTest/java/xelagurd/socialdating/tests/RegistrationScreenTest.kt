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
import xelagurd.socialdating.data.model.additional.RegistrationDetails
import xelagurd.socialdating.data.model.enums.Gender
import xelagurd.socialdating.data.model.enums.Purpose
import xelagurd.socialdating.onNodeWithTextId
import xelagurd.socialdating.ui.screen.RegistrationBody
import xelagurd.socialdating.ui.state.RegistrationUiState
import xelagurd.socialdating.ui.state.RequestStatus

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
    fun registrationScreen_failedStatus_failedText() {
        val registrationUiState = RegistrationUiState(requestStatus = RequestStatus.FAILED)

        setContentToRegistrationBody(registrationUiState)

        composeTestRule.onNodeWithTextId(R.string.failed_registration).assertIsDisplayed()
    }

    @Test
    fun registrationScreen_errorStatus_errorText() {
        val registrationUiState = RegistrationUiState(requestStatus = RequestStatus.ERROR)

        setContentToRegistrationBody(registrationUiState)

        composeTestRule.onNodeWithTextId(R.string.no_internet_connection).assertIsDisplayed()
    }

    @Test
    fun registrationScreen_emptyData_disabledButton() {
        val registrationUiState = RegistrationUiState(
            RegistrationDetails("", null, "", "", "", "", "", "", null),
            requestStatus = RequestStatus.UNDEFINED
        )

        assertRegisterButtonIsDisabled(registrationUiState)
    }

    @Test
    fun registrationScreen_allData_enabledButton() {
        val registrationUiState = RegistrationUiState(
            RegistrationDetails(
                "Alex", Gender.MALE, "qwe", "123", "123", "ssf", "20", "Mos", Purpose.ALL_AT_ONCE
            ),
            requestStatus = RequestStatus.UNDEFINED
        )

        assertRegisterButtonIsEnabled(registrationUiState)
    }

    @Test
    fun registrationScreen_emptyEmail_enabledButton() {
        val registrationUiState = RegistrationUiState(
            RegistrationDetails(
                "Alex", Gender.MALE, "qwe", "123", "123", "", "20", "Mos", Purpose.ALL_AT_ONCE
            ),
            requestStatus = RequestStatus.UNDEFINED
        )

        assertRegisterButtonIsEnabled(registrationUiState)
    }

    @Test
    fun registrationScreen_emptyName_disabledButton() {
        val registrationUiState = RegistrationUiState(
            RegistrationDetails(
                "", Gender.MALE, "qwe", "123", "123", "ssf", "20", "Mos", Purpose.ALL_AT_ONCE
            ),
            requestStatus = RequestStatus.UNDEFINED
        )

        assertRegisterButtonIsDisabled(registrationUiState)
    }

    @Test
    fun registrationScreen_emptyGender_disabledButton() {
        val registrationUiState = RegistrationUiState(
            RegistrationDetails(
                "Alex", null, "qwe", "123", "123", "ssf", "20", "Mos", Purpose.ALL_AT_ONCE
            ),
            requestStatus = RequestStatus.UNDEFINED
        )

        assertRegisterButtonIsDisabled(registrationUiState)
    }

    @Test
    fun registrationScreen_emptyUsername_disabledButton() {
        val registrationUiState = RegistrationUiState(
            RegistrationDetails(
                "Alex", Gender.MALE, "", "123", "123", "ssf", "20", "Mos", Purpose.ALL_AT_ONCE
            ),
            requestStatus = RequestStatus.UNDEFINED
        )

        assertRegisterButtonIsDisabled(registrationUiState)
    }

    @Test
    fun registrationScreen_emptyPassword_disabledButton() {
        val registrationUiState = RegistrationUiState(
            RegistrationDetails(
                "Alex", Gender.MALE, "qwe", "", "123", "ssf", "20", "Mos", Purpose.ALL_AT_ONCE
            ),
            requestStatus = RequestStatus.UNDEFINED
        )

        assertRegisterButtonIsDisabled(registrationUiState)
    }

    @Test
    fun registrationScreen_emptyRepeatedPassword_disabledButton() {
        val registrationUiState = RegistrationUiState(
            RegistrationDetails(
                "Alex", Gender.MALE, "qwe", "123", "", "ssf", "20", "Mos", Purpose.ALL_AT_ONCE
            ),
            requestStatus = RequestStatus.UNDEFINED
        )

        assertRegisterButtonIsDisabled(registrationUiState)
    }

    @Test
    fun registrationScreen_passwordIsNotEqualToRepeatedPassword_disabledButton() {
        val registrationUiState = RegistrationUiState(
            RegistrationDetails(
                "Alex", Gender.MALE, "qwe", "123", "456", "ssf", "20", "Mos", Purpose.ALL_AT_ONCE
            ),
            requestStatus = RequestStatus.UNDEFINED
        )

        assertRegisterButtonIsDisabled(registrationUiState)
    }

    @Test
    fun registrationScreen_emptyAge_disabledButton() {
        val registrationUiState = RegistrationUiState(
            RegistrationDetails(
                "Alex", Gender.MALE, "qwe", "123", "123", "ssf", "", "Mos", Purpose.ALL_AT_ONCE
            ),
            requestStatus = RequestStatus.UNDEFINED
        )

        assertRegisterButtonIsDisabled(registrationUiState)
    }

    @Test
    fun registrationScreen_emptyCity_disabledButton() {
        val registrationUiState = RegistrationUiState(
            RegistrationDetails(
                "Alex", Gender.MALE, "qwe", "123", "123", "ssf", "20", "", Purpose.ALL_AT_ONCE
            ),
            requestStatus = RequestStatus.UNDEFINED
        )

        assertRegisterButtonIsDisabled(registrationUiState)
    }

    @Test
    fun registrationScreen_emptyPurpose_disabledButton() {
        val registrationUiState = RegistrationUiState(
            RegistrationDetails(
                "Alex", Gender.MALE, "qwe", "123", "123", "ssf", "20", "Mos", null
            ),
            requestStatus = RequestStatus.UNDEFINED
        )

        assertRegisterButtonIsDisabled(registrationUiState)
    }

    private fun assertRegisterButtonIsEnabled(registrationUiState: RegistrationUiState) {
        setContentToRegistrationBody(registrationUiState)

        composeTestRule.onNodeWithTextId(R.string.register).assertIsEnabled()
    }

    private fun assertRegisterButtonIsDisabled(registrationUiState: RegistrationUiState) {
        setContentToRegistrationBody(registrationUiState)

        composeTestRule.onNodeWithTextId(R.string.register).assertIsNotEnabled()
    }

    private fun setContentToRegistrationBody(registrationUiState: RegistrationUiState) {
        composeTestRule.activity.setContent {
            RegistrationBody(
                registrationUiState = registrationUiState,
                onValueChange = {},
                onRegisterClick = {},
                onSuccessRegistration = {}
            )
        }
    }
}