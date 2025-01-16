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
    fun statementAddingScreen_emptyData_assertContentIsDisplayed() {
        val statementAddingUiState = StatementAddingUiState()

        assertContentIsDisplayed(statementAddingUiState)
        composeTestRule.onNodeWithTextId(R.string.no_data).assertIsDisplayed()
    }

    @Test
    fun statementAddingScreen_chosenDefiningThemeData_assertContentIsDisplayed() {
        val statementAddingUiState = StatementAddingUiState(
            entities = FakeDataSource.definingThemes,
            formDetails = StatementDetails("StatementText1", true, 1, 1),
            actionRequestStatus = RequestStatus.UNDEFINED
        )

        assertContentIsDisplayed(statementAddingUiState)
        composeTestRule.onNodeWithText(FakeDataSource.definingThemes[0].name).assertIsDisplayed()
        composeTestRule.onNodeWithText(FakeDataSource.definingThemes[1].name).assertIsNotDisplayed()
    }

    @Test
    fun statementAddingScreen_definingThemesData_assertContentIsDisplayed() {
        val statementAddingUiState = StatementAddingUiState(
            entities = FakeDataSource.definingThemes,
            formDetails = StatementDetails("StatementText1", true, null, 1),
            actionRequestStatus = RequestStatus.UNDEFINED
        )

        assertContentIsDisplayed(statementAddingUiState)
        composeTestRule.onNodeWithText(FakeDataSource.definingThemes[0].name).assertIsDisplayed()
        composeTestRule.onNodeWithText(FakeDataSource.definingThemes[1].name).assertIsDisplayed()
    }

    @Test
    fun statementAddingScreen_loadingStatus_loadingIndicator() {
        val statementAddingUiState = StatementAddingUiState(actionRequestStatus = RequestStatus.LOADING)

        setContentToStatementAddingBody(statementAddingUiState)

        composeTestRule.onNodeWithTagId(R.string.loading).assertIsDisplayed()
    }

    @Test
    fun statementAddingScreen_failedStatus_failedText() {
        val statementAddingUiState = StatementAddingUiState(actionRequestStatus = RequestStatus.FAILED)

        setContentToStatementAddingBody(statementAddingUiState)

        composeTestRule.onNodeWithTextId(R.string.failed_add_statement).assertIsDisplayed()
    }

    @Test
    fun statementAddingScreen_errorStatus_errorText() {
        val statementAddingUiState = StatementAddingUiState(actionRequestStatus = RequestStatus.ERROR)

        setContentToStatementAddingBody(statementAddingUiState)

        composeTestRule.onNodeWithTextId(R.string.no_internet_connection).assertIsDisplayed()
    }

    @Test
    fun statementAddingScreen_emptyData_disabledButton() {
        val statementAddingUiState = StatementAddingUiState(
            entities = FakeDataSource.definingThemes,
            formDetails = StatementDetails("", null, null, 1),
            actionRequestStatus = RequestStatus.UNDEFINED
        )

        assertAddStatementButtonIsDisabled(statementAddingUiState)
    }

    @Test
    fun statementAddingScreen_allData_enabledButton() {
        val statementAddingUiState = StatementAddingUiState(
            entities = FakeDataSource.definingThemes,
            formDetails = StatementDetails("StatementText1", true, 1, 1),
            actionRequestStatus = RequestStatus.UNDEFINED
        )

        assertAddStatementButtonIsEnabled(statementAddingUiState)
    }

    @Test
    fun statementAddingScreen_emptyStatementText_disabledButton() {
        val statementAddingUiState = StatementAddingUiState(
            entities = FakeDataSource.definingThemes,
            formDetails = StatementDetails("", true, 1, 1),
            actionRequestStatus = RequestStatus.UNDEFINED
        )

        assertAddStatementButtonIsDisabled(statementAddingUiState)
    }

    @Test
    fun statementAddingScreen_emptySupportDefiningTheme_disabledButton() {
        val statementAddingUiState = StatementAddingUiState(
            entities = FakeDataSource.definingThemes,
            formDetails = StatementDetails("StatementText1", null, 1, 1),
            actionRequestStatus = RequestStatus.UNDEFINED
        )

        assertAddStatementButtonIsDisabled(statementAddingUiState)
    }

    @Test
    fun statementAddingScreen_emptyDefiningTheme_disabledButton() {
        val statementAddingUiState = StatementAddingUiState(
            entities = FakeDataSource.definingThemes,
            formDetails = StatementDetails("StatementText1", true, null, 1),
            actionRequestStatus = RequestStatus.UNDEFINED
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