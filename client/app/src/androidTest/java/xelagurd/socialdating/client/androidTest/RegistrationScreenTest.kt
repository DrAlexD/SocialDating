package xelagurd.socialdating.client.androidTest

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
import xelagurd.socialdating.client.MainActivity
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
    val composeTestRule = createAndroidComposeRule<MainActivity>()

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
            age = (AGE_MIN - 1).toString()
        )

        composeTestRule.setContentToScreen {
            RegistrationScreenComponent(
                registrationUiState = RegistrationUiState(formData = invalidFormData)
            )
        }

        composeTestRule.onNodeWithTextId(R.string.error_username_format)
            .performScrollTo().assertIsDisplayed()
        composeTestRule.onNodeWithTextId(R.string.error_email_format)
            .performScrollTo().assertIsDisplayed()
        composeTestRule.onNodeWithTextId(R.string.error_age, AGE_MIN, AGE_MAX)
            .performScrollTo().assertIsDisplayed()
        composeTestRule.onNodeWithTextId(R.string.error_repeated_password)
            .performScrollTo().assertIsDisplayed()
        composeTestRule.onNodeWithTextId(R.string.register).performScrollTo().checkDisabledButton()
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

        composeTestRule.onNodeWithTextId(R.string.age).checkTextFieldAndInput("1")
        assertEquals(true, changedFormData?.age?.contains("1"))

        composeTestRule.onNodeWithTextId(R.string.city).checkTextFieldAndInput(INPUT_TEXT)
        assertEquals(true, changedFormData?.city?.contains(INPUT_TEXT))

        composeTestRule.onNodeWithTagId(R.string.female).checkButtonAndClick()
        assertEquals(Gender.FEMALE, changedFormData?.gender)

        composeTestRule.onNodeWithTagId(R.string.friends).checkButtonAndClick()
        assertEquals(Purpose.FRIENDS, changedFormData?.purpose)

        composeTestRule.onNodeWithTextId(R.string.register).checkButtonAndClick()
        assertTrue(isRegisterClicked)

        composeTestRule.onNodeWithContentDescriptionId(R.string.back_button).checkButtonAndClick()
        assertTrue(isNavigateUpClicked)
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

        composeTestRule.onNodeWithTextId(R.string.password).checkTextFieldAndInput(INPUT_TEXT)
        assertEquals(true, changedFormData?.password?.contains(INPUT_TEXT))

        composeTestRule.onNodeWithTextId(R.string.repeat_password).checkTextFieldAndInput(INPUT_TEXT)
        assertEquals(true, changedFormData?.repeatedPassword?.contains(INPUT_TEXT))
    }

    private fun assertContentIsDisplayed() {
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
}