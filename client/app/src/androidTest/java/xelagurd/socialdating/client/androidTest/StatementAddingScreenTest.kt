package xelagurd.socialdating.client.androidTest

import androidx.activity.compose.setContent
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsNotDisplayed
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
import xelagurd.socialdating.client.data.fake.FakeData
import xelagurd.socialdating.client.onNodeWithTagId
import xelagurd.socialdating.client.onNodeWithTextId
import xelagurd.socialdating.client.onNodeWithTextIdWithColon
import xelagurd.socialdating.client.ui.screen.StatementAddingScreenComponent
import xelagurd.socialdating.client.ui.state.RequestStatus
import xelagurd.socialdating.client.ui.state.StatementAddingUiState
import xelagurd.socialdating.client.ui.theme.AppTheme

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
    fun statementAddingScreen_loadingStateAndEmptyDefiningThemes_assertContentIsDisplayed() {
        val statementAddingUiState = StatementAddingUiState(
            dataRequestStatus = RequestStatus.LOADING
        )

        assertContentIsDisplayed(statementAddingUiState)

        composeTestRule.onNodeWithTagId(R.string.loading).assertIsDisplayed()
    }

    @Test
    fun statementAddingScreen_failureStateAndEmptyDefiningThemes_assertContentIsDisplayed() {
        val failureText = FakeData.FAILURE_TEXT
        val statementAddingUiState = StatementAddingUiState(
            dataRequestStatus = RequestStatus.FAILURE(failureText)
        )

        assertContentIsDisplayed(statementAddingUiState)

        composeTestRule.onNodeWithText(failureText).assertIsDisplayed()
    }

    @Test
    fun statementAddingScreen_allDefiningThemes_assertContentIsDisplayed() {
        val statementAddingUiState = StatementAddingUiState(
            dataRequestStatus = RequestStatus.SUCCESS,
            entities = definingThemes,
            formData = statementFormData.copy(definingThemeId = null)
        )

        assertContentIsDisplayed(statementAddingUiState)
        composeTestRule.onNodeWithText(definingThemes[0].name).assertIsDisplayed()
        composeTestRule.onNodeWithText(definingThemes[1].name).assertIsDisplayed()
    }

    @Test
    fun statementAddingScreen_chosenDefiningTheme_assertContentIsDisplayed() {
        val statementAddingUiState = StatementAddingUiState(
            dataRequestStatus = RequestStatus.SUCCESS,
            entities = definingThemes,
            formData = statementFormData
        )

        assertContentIsDisplayed(statementAddingUiState)
        composeTestRule.onNodeWithText(definingThemes[0].name).assertIsDisplayed()
        composeTestRule.onNodeWithText(definingThemes[1].name).assertIsNotDisplayed()
    }

    @Test
    fun statementAddingScreen_loadingState_loadingIndicator() {
        val statementAddingUiState = StatementAddingUiState(
            dataRequestStatus = RequestStatus.SUCCESS,
            actionRequestStatus = RequestStatus.LOADING
        )

        setContentToStatementAddingBody(statementAddingUiState)

        composeTestRule.onNodeWithTagId(R.string.loading).assertIsDisplayed()
    }

    @Test
    fun statementAddingScreen_failureState_failureText() {
        val failureText = FakeData.FAILURE_TEXT
        val statementAddingUiState = StatementAddingUiState(
            dataRequestStatus = RequestStatus.SUCCESS,
            actionRequestStatus = RequestStatus.FAILURE(failureText)
        )

        setContentToStatementAddingBody(statementAddingUiState)

        composeTestRule.onNodeWithText(failureText).assertIsDisplayed()
    }

    @Test
    fun statementAddingScreen_errorState_errorText() {
        val errorText = FakeData.ERROR_TEXT
        val statementAddingUiState = StatementAddingUiState(
            dataRequestStatus = RequestStatus.SUCCESS,
            actionRequestStatus = RequestStatus.ERROR(errorText)
        )

        setContentToStatementAddingBody(statementAddingUiState)

        composeTestRule.onNodeWithText(errorText).assertIsDisplayed()
    }

    @Test
    fun statementAddingScreen_emptyData_disabledButton() {
        val statementAddingUiState = StatementAddingUiState(
            dataRequestStatus = RequestStatus.SUCCESS,
            entities = definingThemes,
            formData = statementFormData.copy(text = "", isSupportDefiningTheme = null, definingThemeId = null,
                creatorUserId = null)
        )

        assertAddStatementButtonIsDisabled(statementAddingUiState)
    }

    @Test
    fun statementAddingScreen_allData_enabledButton() {
        val statementAddingUiState = StatementAddingUiState(
            dataRequestStatus = RequestStatus.SUCCESS,
            entities = definingThemes,
            formData = statementFormData
        )

        assertAddStatementButtonIsEnabled(statementAddingUiState)
    }

    @Test
    fun statementAddingScreen_emptyStatementText_disabledButton() {
        val statementAddingUiState = StatementAddingUiState(
            dataRequestStatus = RequestStatus.SUCCESS,
            entities = definingThemes,
            formData = statementFormData.copy(text = "")
        )

        assertAddStatementButtonIsDisabled(statementAddingUiState)
    }

    @Test
    fun statementAddingScreen_emptySupportDefiningTheme_disabledButton() {
        val statementAddingUiState = StatementAddingUiState(
            dataRequestStatus = RequestStatus.SUCCESS,
            entities = definingThemes,
            formData = statementFormData.copy(isSupportDefiningTheme = null)
        )

        assertAddStatementButtonIsDisabled(statementAddingUiState)
    }

    @Test
    fun statementAddingScreen_emptyDefiningTheme_disabledButton() {
        val statementAddingUiState = StatementAddingUiState(
            dataRequestStatus = RequestStatus.SUCCESS,
            entities = definingThemes,
            formData = statementFormData.copy(definingThemeId = null)
        )

        assertAddStatementButtonIsDisabled(statementAddingUiState)
    }

    private fun assertAddStatementButtonIsEnabled(statementAddingUiState: StatementAddingUiState) {
        setContentToStatementAddingBody(statementAddingUiState)

        composeTestRule.onNodeWithTextId(R.string.add_statement).checkEnabledButton()
    }

    private fun assertAddStatementButtonIsDisabled(statementAddingUiState: StatementAddingUiState) {
        setContentToStatementAddingBody(statementAddingUiState)

        composeTestRule.onNodeWithTextId(R.string.add_statement).checkDisabledButton()
    }

    private fun assertContentIsDisplayed(statementAddingUiState: StatementAddingUiState) {
        setContentToStatementAddingBody(statementAddingUiState)

        composeTestRule.onNodeWithTextId(R.string.statement_text).checkTextField()

        composeTestRule.onNodeWithTextIdWithColon(R.string.defining_theme).assertIsDisplayed()

        composeTestRule.onNodeWithTextId(R.string.is_support_defining_theme).assertIsDisplayed()
        composeTestRule.onNodeWithTextId(R.string.yes).assertIsDisplayed()
        composeTestRule.onNodeWithTextId(R.string.no).assertIsDisplayed()
    }

    private fun setContentToStatementAddingBody(statementAddingUiState: StatementAddingUiState) {
        composeTestRule.activity.setContent {
            AppTheme {
                StatementAddingScreenComponent(
                    statementAddingUiState = statementAddingUiState
                )
            }
        }
    }
}