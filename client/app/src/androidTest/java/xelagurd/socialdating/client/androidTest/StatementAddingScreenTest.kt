package xelagurd.socialdating.client.androidTest

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithText
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
    fun statementAddingScreen_validFormData_displayedContentWithEnabledButton() {
        composeTestRule.setContentToScreen {
            StatementAddingScreenComponent(
                statementAddingUiState = StatementAddingUiState(
                    dataRequestStatus = RequestStatus.SUCCESS,
                    entities = definingThemes,
                    formData = statementFormData
                )
            )
        }

        assertContentIsDisplayed()
        composeTestRule.onNodeWithTextId(R.string.add_statement).checkEnabledButton()
    }

    @Test
    fun statementAddingScreen_allActionsWithoutAddStatement_calledAllActions() {
        var changedFormData: StatementFormData? = null
        var isNavigateUpClicked = false

        composeTestRule.setContentToScreen {
            StatementAddingScreenComponent(
                statementAddingUiState = StatementAddingUiState(
                    dataRequestStatus = RequestStatus.SUCCESS,
                    entities = definingThemes,
                    formData = statementFormData.copy(definingThemeId = null)
                ),
                onSuccessStatementAdding = {},
                onNavigateUp = { isNavigateUpClicked = true },
                onValueChange = { changedFormData = it },
                onStatementAddingClick = {}
            )
        }

        composeTestRule.onNodeWithTextId(R.string.statement_text).checkTextFieldAndInput(INPUT_TEXT)
        assertEquals(true, changedFormData?.text?.contains(INPUT_TEXT))

        composeTestRule.onNodeWithText(definingThemes[0].name).checkButtonAndClick()
        assertEquals(definingThemes[0].id, changedFormData?.definingThemeId)

        composeTestRule.onNodeWithTagId(R.string.yes).checkButtonAndClick()
        assertEquals(true, changedFormData?.isSupportDefiningTheme)

        composeTestRule.onNodeWithTagId(R.string.no).checkButtonAndClick()
        assertEquals(false, changedFormData?.isSupportDefiningTheme)

        composeTestRule.onNodeWithContentDescriptionId(R.string.back_button).checkButtonAndClick()
        assertTrue(isNavigateUpClicked)
    }

    @Test
    fun statementAddingScreen_chosenDefiningThemeClick_clearedDefiningThemeAndCalledAddStatement() {
        var changedFormData: StatementFormData? = null
        var isStatementAddingClicked = false

        composeTestRule.setContentToScreen {
            StatementAddingScreenComponent(
                statementAddingUiState = StatementAddingUiState(
                    dataRequestStatus = RequestStatus.SUCCESS,
                    entities = definingThemes,
                    formData = statementFormData
                ),
                onSuccessStatementAdding = {},
                onNavigateUp = {},
                onValueChange = { changedFormData = it },
                onStatementAddingClick = { isStatementAddingClicked = true }
            )
        }

        composeTestRule.onNodeWithText(definingThemes[0].name).checkButtonAndClick()
        assertEquals(null, changedFormData?.definingThemeId)

        composeTestRule.onNodeWithTextId(R.string.add_statement).checkButtonAndClick()
        assertTrue(isStatementAddingClicked)
    }

    private fun assertContentIsDisplayed() {
        composeTestRule.onNodeWithTextId(R.string.statement_text).checkTextField()

        composeTestRule.onNodeWithTextIdWithColon(R.string.defining_theme).assertIsDisplayed()

        composeTestRule.onNodeWithTextId(R.string.is_support_defining_theme).assertIsDisplayed()
        composeTestRule.onNodeWithTextId(R.string.yes).assertIsDisplayed()
        composeTestRule.onNodeWithTextId(R.string.no).assertIsDisplayed()
    }
}