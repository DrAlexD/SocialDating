package xelagurd.socialdating.client.androidTest

import java.time.LocalDate
import java.time.format.DateTimeFormatter
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.performScrollTo
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
import xelagurd.socialdating.client.AndroidTestUtils.onNodeWithContentDescriptionId
import xelagurd.socialdating.client.AndroidTestUtils.onNodeWithTagId
import xelagurd.socialdating.client.AndroidTestUtils.onNodeWithTextId
import xelagurd.socialdating.client.AndroidTestUtils.onNodeWithTextIdWithColon
import xelagurd.socialdating.client.AndroidTestUtils.setContentToScreen
import xelagurd.socialdating.client.AndroidTestUtils.setContentToScreenAndRecompose
import xelagurd.socialdating.client.HiltTestActivity
import xelagurd.socialdating.client.R
import xelagurd.socialdating.client.data.fake.FakeData
import xelagurd.socialdating.client.data.model.DefaultDataProperties.AGE_MAX
import xelagurd.socialdating.client.data.model.DefaultDataProperties.AGE_MIN
import xelagurd.socialdating.client.data.model.enums.Gender
import xelagurd.socialdating.client.data.model.enums.Purpose
import xelagurd.socialdating.client.ui.form.RegistrationFormData
import xelagurd.socialdating.client.ui.screen.RegistrationScreenComponent
import xelagurd.socialdating.client.ui.state.RegistrationUiState

@HiltAndroidTest
class RegistrationScreenTest {
    @get:Rule(order = 0)
    val hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 1)
    val composeTestRule = createAndroidComposeRule<HiltTestActivity>()

    @Before
    fun setup() {
        hiltRule.inject()
    }

    private val registrationFormData = FakeData.registrationFormData

    @Test
    fun registrationScreen_defaultParameters_displayedContentWithDisabledButton() {
        composeTestRule.setContentToScreen {
            RegistrationScreenComponent()
        }

        assertContentIsDisplayed()
        composeTestRule.onNodeWithTextId(R.string.register).checkDisabledButton()
    }

    @Test
    fun registrationScreen_recomposition_displayedContentWithDisabledButton() {
        composeTestRule.setContentToScreenAndRecompose {
            RegistrationScreenComponent(registrationUiState = RegistrationUiState())
        }

        assertContentIsDisplayed()
        composeTestRule.onNodeWithTextId(R.string.register).checkDisabledButton()
    }

    @Test
    fun registrationScreen_validFormData_displayedContentWithEnabledButton() {
        composeTestRule.setContentToScreen {
            RegistrationScreenComponent(
                registrationUiState = RegistrationUiState(formData = registrationFormData)
            )
        }

        assertContentIsDisplayed()
        composeTestRule.onNodeWithTextId(R.string.register).checkEnabledButton()
    }

    @Test
    fun registrationScreen_invalidFormData_displayedErrorsWithDisabledButton() {
        val invalidFormData = registrationFormData.copy(
            username = "user name",
            repeatedPassword = "otherPassword",
            email = "email1gmail.com",
            birthDate = LocalDate.now().minusYears(AGE_MIN - 1L).format(DateTimeFormatter.ofPattern("ddMMuuuu"))
        )

        composeTestRule.setContentToScreen {
            RegistrationScreenComponent(
                registrationUiState = RegistrationUiState(formData = invalidFormData)
            )
        }

        composeTestRule.onNodeWithTextId(R.string.error_username_format).assertIsDisplayed()
        composeTestRule.onNodeWithTextId(R.string.error_email_format).assertIsDisplayed()
        composeTestRule.onNodeWithTextId(R.string.error_age, AGE_MIN, AGE_MAX).assertIsDisplayed()

        scrollToFormEnd()
        composeTestRule.onNodeWithTextId(R.string.error_repeated_password).assertIsDisplayed()
        composeTestRule.onNodeWithTextId(R.string.register).checkDisabledButton()
    }

    @Test
    fun registrationScreen_allActionsWithoutPasswords_calledAllActions() {
        var changedFormData: RegistrationFormData? = null
        var isRegisterClicked = false
        var isNavigateUpClicked = false

        composeTestRule.setContentToScreen {
            RegistrationScreenComponent(
                registrationUiState = RegistrationUiState(formData = registrationFormData),
                onSuccessRegistration = {},
                onNavigateUp = { isNavigateUpClicked = true },
                onValueChange = { changedFormData = it },
                onRegisterClick = { isRegisterClicked = true }
            )
        }

        composeTestRule.onNodeWithTextId(R.string.username).checkTextFieldAndInput(INPUT_TEXT)
        assertEquals(true, changedFormData?.username?.contains(INPUT_TEXT))

        composeTestRule.onNodeWithTextId(R.string.name).checkTextFieldAndInput(INPUT_TEXT)
        assertEquals(true, changedFormData?.name?.contains(INPUT_TEXT))

        composeTestRule.onNodeWithTextId(R.string.email_optional).checkTextFieldAndInput(INPUT_TEXT)
        assertEquals(true, changedFormData?.email?.contains(INPUT_TEXT))

        composeTestRule.onNodeWithTextId(R.string.city).checkTextFieldAndInput(INPUT_TEXT)
        assertEquals(true, changedFormData?.city?.contains(INPUT_TEXT))

        composeTestRule.onNodeWithTagId(R.string.female).checkButtonAndClick()
        assertEquals(Gender.FEMALE, changedFormData?.gender)

        scrollToFormEnd()
        composeTestRule.onNodeWithTagId(R.string.friends).checkButtonAndClick()
        assertEquals(Purpose.FRIENDS, changedFormData?.purpose)

        composeTestRule.onNodeWithTextId(R.string.register).checkButtonAndClick()
        assertTrue(isRegisterClicked)

        composeTestRule.onNodeWithContentDescriptionId(R.string.back_button).checkButtonAndClick()
        assertTrue(isNavigateUpClicked)
    }

    @Test
    fun registrationScreen_birthDateInput_calledValueChangeActionWithDigitsOnly() {
        var changedFormData: RegistrationFormData? = null

        composeTestRule.setContentToScreen {
            RegistrationScreenComponent(
                registrationUiState = RegistrationUiState(formData = RegistrationFormData()),
                onValueChange = { changedFormData = it }
            )
        }

        composeTestRule.onNodeWithTextId(R.string.birth_date).checkTextFieldAndInput("15.03")
        assertEquals("1503", changedFormData?.birthDate)
    }

    @Test
    fun registrationScreen_passwordsInput_calledValueChangeAction() {
        var changedFormData: RegistrationFormData? = null

        composeTestRule.setContentToScreen {
            RegistrationScreenComponent(
                registrationUiState = RegistrationUiState(formData = registrationFormData),
                onValueChange = { changedFormData = it }
            )
        }

        scrollToFormEnd()
        composeTestRule.onNodeWithTextId(R.string.password).checkTextFieldAndInput(INPUT_TEXT)
        assertEquals(true, changedFormData?.password?.contains(INPUT_TEXT))

        composeTestRule.onNodeWithTextId(R.string.repeat_password).checkTextFieldAndInput(INPUT_TEXT)
        assertEquals(true, changedFormData?.repeatedPassword?.contains(INPUT_TEXT))
    }

    // the form stays scrolled to its end, so the register button is checked after it without the scroll
    private fun assertContentIsDisplayed() {
        composeTestRule.onNodeWithTextId(R.string.username).checkTextField()
        composeTestRule.onNodeWithTextId(R.string.name).checkTextField()

        composeTestRule.onNodeWithTextIdWithColon(R.string.gender).assertIsDisplayed()
        composeTestRule.onNodeWithTextId(R.string.male).assertIsDisplayed()
        composeTestRule.onNodeWithTextId(R.string.female).assertIsDisplayed()

        composeTestRule.onNodeWithTextId(R.string.email_optional).checkTextField()
        composeTestRule.onNodeWithTextId(R.string.birth_date).checkTextField()
        composeTestRule.onNodeWithTextId(R.string.city).checkTextField()

        scrollToFormEnd()
        composeTestRule.onNodeWithTextIdWithColon(R.string.purpose).assertIsDisplayed()
        composeTestRule.onNodeWithTextId(R.string.friends).assertIsDisplayed()
        composeTestRule.onNodeWithTextId(R.string.relationships).assertIsDisplayed()
        composeTestRule.onNodeWithTextId(R.string.all_at_once).assertIsDisplayed()

        composeTestRule.onNodeWithTextId(R.string.password).checkTextField()
        composeTestRule.onNodeWithTextId(R.string.repeat_password).checkTextField()
    }

    // the form is longer than the small screens, but shorter than two of them, so its top is checked
    // without the scroll, and the rest of it is checked after the scroll to its end
    private fun scrollToFormEnd() {
        composeTestRule.onNodeWithTextId(R.string.register).performScrollTo()
    }
}