package xelagurd.socialdating.client.androidTest

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performScrollTo
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
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
import xelagurd.socialdating.client.data.model.DefaultDataProperties.STATEMENT_TEXT_LENGTH_MAX
import xelagurd.socialdating.client.data.model.DefaultDataProperties.STATEMENT_TEXT_LENGTH_MIN
import xelagurd.socialdating.client.data.model.DefiningTheme
import xelagurd.socialdating.client.ui.form.StatementFormData
import xelagurd.socialdating.client.ui.screen.StatementAddingScreenComponent
import xelagurd.socialdating.client.ui.state.RequestStatus
import xelagurd.socialdating.client.ui.state.StatementAddingUiState

@HiltAndroidTest
class StatementAddingScreenTest {
    @get:Rule(order = 0)
    val hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 1)
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    @Before
    fun setup() {
        hiltRule.inject()
    }

    private val definingThemes = FakeData.definingThemes
    private val statementFormData = FakeData.statementFormData

    private val chosenDefiningTheme = definingThemes[0]
    private val anotherChosenDefiningTheme = definingThemes[1]
    private val notChosenDefiningTheme = definingThemes[2]

    @Test
    fun statementAddingScreen_defaultParameters_displayedContentWithDisabledButton() {
        composeTestRule.setContentToScreen {
            StatementAddingScreenComponent()
        }

        assertContentIsDisplayed()
        composeTestRule.onNodeWithTextId(R.string.add_statement).checkDisabledButton()
    }

    @Test
    fun statementAddingScreen_recomposition_displayedContentWithDisabledButton() {
        composeTestRule.setContentToScreenAndRecompose {
            StatementAddingScreenComponent(statementAddingUiState = StatementAddingUiState())
        }

        assertContentIsDisplayed()
        composeTestRule.onNodeWithTextId(R.string.add_statement).checkDisabledButton()
    }

    @Test
    fun statementAddingScreen_validFormData_displayedOpinionOfEveryChosenDefiningThemeWithEnabledButton() {
        composeTestRule.setContentToScreen {
            StatementAddingScreenComponent(
                statementAddingUiState = statementAddingUiState(statementFormData)
            )
        }

        assertContentIsDisplayed()
        assertOpinionIsDisplayed(chosenDefiningTheme)
        assertOpinionIsDisplayed(anotherChosenDefiningTheme)
        assertOpinionIsNotDisplayed(notChosenDefiningTheme)

        composeTestRule.onNodeWithTextId(R.string.add_statement).checkEnabledButton()
    }

    @Test
    fun statementAddingScreen_invalidFormData_displayedErrorWithDisabledButton() {
        val invalidFormData = statementFormData.copy(text = "T")

        composeTestRule.setContentToScreen {
            StatementAddingScreenComponent(
                statementAddingUiState = statementAddingUiState(invalidFormData)
            )
        }

        composeTestRule
            .onNodeWithTextId(
                R.string.error_length,
                STATEMENT_TEXT_LENGTH_MIN,
                STATEMENT_TEXT_LENGTH_MAX
            )
            .performScrollTo().assertIsDisplayed()
        composeTestRule.onNodeWithTextId(R.string.add_statement)
            .performScrollTo().checkDisabledButton()
    }

    @Test
    fun statementAddingScreen_notChosenDefiningThemeClick_choseItWithoutOpinion() {
        var changedFormData: StatementFormData? = null
        var isNavigateUpClicked = false

        composeTestRule.setContentToScreen {
            StatementAddingScreenComponent(
                statementAddingUiState = statementAddingUiState(
                    statementFormData.copy(definingThemes = mapOf())
                ),
                onNavigateUp = { isNavigateUpClicked = true },
                onValueChange = { changedFormData = it }
            )
        }

        composeTestRule.onNodeWithTextId(R.string.add_statement).checkDisabledButton()

        composeTestRule.onNodeWithTextId(R.string.statement_text).checkTextFieldAndInput(INPUT_TEXT)
        assertEquals(true, changedFormData?.text?.contains(INPUT_TEXT))

        composeTestRule.onNodeWithText(notChosenDefiningTheme.name).checkButtonAndClick()
        assertEquals(mapOf(notChosenDefiningTheme.id to null), changedFormData?.definingThemes)

        composeTestRule.onNodeWithContentDescriptionId(R.string.back_button).checkButtonAndClick()
        assertTrue(isNavigateUpClicked)
    }

    @Test
    fun statementAddingScreen_chosenDefiningThemeOpinionClick_updatedOnlyItsOpinion() {
        var changedFormData: StatementFormData? = null
        var isStatementAddingClicked = false

        composeTestRule.setContentToScreen {
            StatementAddingScreenComponent(
                statementAddingUiState = statementAddingUiState(statementFormData),
                onValueChange = { changedFormData = it },
                onStatementAddingClick = { isStatementAddingClicked = true }
            )
        }

        composeTestRule
            .onNodeWithTagId(R.string.no, chosenDefiningTheme.id.toString())
            .checkButtonAndClick()
        assertEquals(false, changedFormData?.definingThemes?.get(chosenDefiningTheme.id))
        assertEquals(
            statementFormData.definingThemes[anotherChosenDefiningTheme.id],
            changedFormData?.definingThemes?.get(anotherChosenDefiningTheme.id)
        )

        composeTestRule
            .onNodeWithTagId(R.string.yes, chosenDefiningTheme.id.toString())
            .checkButtonAndClick()
        assertEquals(true, changedFormData?.definingThemes?.get(chosenDefiningTheme.id))

        composeTestRule.onNodeWithTextId(R.string.add_statement).checkButtonAndClick()
        assertTrue(isStatementAddingClicked)
    }

    @Test
    fun statementAddingScreen_chosenDefiningThemeClick_removedOnlyIt() {
        var changedFormData: StatementFormData? = null

        composeTestRule.setContentToScreen {
            StatementAddingScreenComponent(
                statementAddingUiState = statementAddingUiState(statementFormData),
                onValueChange = { changedFormData = it }
            )
        }

        composeTestRule.onNodeWithText(chosenDefiningTheme.name).checkButtonAndClick()

        assertFalse(changedFormData?.definingThemes?.containsKey(chosenDefiningTheme.id) ?: true)
        assertTrue(changedFormData?.definingThemes?.containsKey(anotherChosenDefiningTheme.id) ?: false)
    }

    private fun statementAddingUiState(formData: StatementFormData) =
        StatementAddingUiState(
            entities = definingThemes,
            dataRequestStatus = RequestStatus.SUCCESS,
            formData = formData
        )

    private fun assertContentIsDisplayed() {
        composeTestRule.onNodeWithTextId(R.string.statement_text).checkTextField()

        composeTestRule.onNodeWithTextIdWithColon(R.string.defining_themes).assertIsDisplayed()
        composeTestRule.onNodeWithTextId(R.string.is_support_defining_theme).assertIsDisplayed()
    }

    private fun assertOpinionIsDisplayed(definingTheme: DefiningTheme) {
        composeTestRule.onNodeWithTagId(R.string.yes, definingTheme.id.toString()).assertIsDisplayed()
        composeTestRule.onNodeWithTagId(R.string.no, definingTheme.id.toString()).assertIsDisplayed()
    }

    private fun assertOpinionIsNotDisplayed(definingTheme: DefiningTheme) {
        composeTestRule.onNodeWithTagId(R.string.yes, definingTheme.id.toString()).assertDoesNotExist()
        composeTestRule.onNodeWithTagId(R.string.no, definingTheme.id.toString()).assertDoesNotExist()
    }
}
