package xelagurd.socialdating.tests

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
import xelagurd.socialdating.MainActivity
import xelagurd.socialdating.R
import xelagurd.socialdating.checkDisabledButton
import xelagurd.socialdating.checkEnabledButton
import xelagurd.socialdating.checkTextField
import xelagurd.socialdating.data.fake.FakeDataSource
import xelagurd.socialdating.data.model.details.StatementDetails
import xelagurd.socialdating.onNodeWithTagId
import xelagurd.socialdating.onNodeWithTextId
import xelagurd.socialdating.onNodeWithTextIdWithColon
import xelagurd.socialdating.ui.screen.StatementAddingScreenComponent
import xelagurd.socialdating.ui.state.RequestStatus
import xelagurd.socialdating.ui.state.StatementAddingUiState
import xelagurd.socialdating.ui.theme.AppTheme

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

    @Test
    fun statementAddingScree_loadingStateAndEmptyDefiningThemes_assertContentIsDisplayed() {
        val statementAddingUiState = StatementAddingUiState(
            dataRequestStatus = RequestStatus.LOADING
        )

        assertContentIsDisplayed(statementAddingUiState)

        composeTestRule.onNodeWithTagId(R.string.loading).assertIsDisplayed()
    }

    @Test
    fun statementAddingScree_failureStateAndEmptyDefiningThemes_assertContentIsDisplayed() {
        val failureText = "Failure Text"
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
            entities = FakeDataSource.definingThemes,
            formDetails = StatementDetails("StatementText1", true, null, 1)
        )

        assertContentIsDisplayed(statementAddingUiState)
        composeTestRule.onNodeWithText(FakeDataSource.definingThemes[0].name).assertIsDisplayed()
        composeTestRule.onNodeWithText(FakeDataSource.definingThemes[1].name).assertIsDisplayed()
    }

    @Test
    fun statementAddingScreen_chosenDefiningTheme_assertContentIsDisplayed() {
        val statementAddingUiState = StatementAddingUiState(
            dataRequestStatus = RequestStatus.SUCCESS,
            entities = FakeDataSource.definingThemes,
            formDetails = StatementDetails("StatementText1", true, 1, 1)
        )

        assertContentIsDisplayed(statementAddingUiState)
        composeTestRule.onNodeWithText(FakeDataSource.definingThemes[0].name).assertIsDisplayed()
        composeTestRule.onNodeWithText(FakeDataSource.definingThemes[1].name).assertIsNotDisplayed()
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
        val failureText = "Failure Text"
        val statementAddingUiState = StatementAddingUiState(
            dataRequestStatus = RequestStatus.SUCCESS,
            actionRequestStatus = RequestStatus.FAILURE(failureText)
        )

        setContentToStatementAddingBody(statementAddingUiState)

        composeTestRule.onNodeWithText(failureText).assertIsDisplayed()
    }

    @Test
    fun statementAddingScreen_errorState_errorText() {
        val errorText = "Error Text"
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
            entities = FakeDataSource.definingThemes,
            formDetails = StatementDetails("", null, null, 1)
        )

        assertAddStatementButtonIsDisabled(statementAddingUiState)
    }

    @Test
    fun statementAddingScreen_allData_enabledButton() {
        val statementAddingUiState = StatementAddingUiState(
            dataRequestStatus = RequestStatus.SUCCESS,
            entities = FakeDataSource.definingThemes,
            formDetails = StatementDetails("StatementText1", true, 1, 1)
        )

        assertAddStatementButtonIsEnabled(statementAddingUiState)
    }

    @Test
    fun statementAddingScreen_emptyStatementText_disabledButton() {
        val statementAddingUiState = StatementAddingUiState(
            dataRequestStatus = RequestStatus.SUCCESS,
            entities = FakeDataSource.definingThemes,
            formDetails = StatementDetails("", true, 1, 1)
        )

        assertAddStatementButtonIsDisabled(statementAddingUiState)
    }

    @Test
    fun statementAddingScreen_emptySupportDefiningTheme_disabledButton() {
        val statementAddingUiState = StatementAddingUiState(
            dataRequestStatus = RequestStatus.SUCCESS,
            entities = FakeDataSource.definingThemes,
            formDetails = StatementDetails("StatementText1", null, 1, 1)
        )

        assertAddStatementButtonIsDisabled(statementAddingUiState)
    }

    @Test
    fun statementAddingScreen_emptyDefiningTheme_disabledButton() {
        val statementAddingUiState = StatementAddingUiState(
            dataRequestStatus = RequestStatus.SUCCESS,
            entities = FakeDataSource.definingThemes,
            formDetails = StatementDetails("StatementText1", true, null, 1)
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

        composeTestRule.onNodeWithTextId(R.string.is_support_defining_theme)
            .assertIsDisplayed()
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