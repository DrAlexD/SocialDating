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
import xelagurd.socialdating.data.fake.FakeDataSource
import xelagurd.socialdating.data.model.additional.StatementDetails
import xelagurd.socialdating.onNodeWithTextId
import xelagurd.socialdating.ui.screen.StatementAddingBody
import xelagurd.socialdating.ui.state.RequestStatus
import xelagurd.socialdating.ui.state.StatementAddingUiState

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
    fun statementAddingScreen_failedStatus_failedText() {
        val statementAddingUiState = StatementAddingUiState(requestStatus = RequestStatus.FAILED)

        setContentToStatementAddingBody(statementAddingUiState)

        composeTestRule.onNodeWithTextId(R.string.failed_add_statement).assertIsDisplayed()
    }

    @Test
    fun statementAddingScreen_errorStatus_errorText() {
        val statementAddingUiState = StatementAddingUiState(requestStatus = RequestStatus.ERROR)

        setContentToStatementAddingBody(statementAddingUiState)

        composeTestRule.onNodeWithTextId(R.string.no_internet_connection).assertIsDisplayed()
    }

    @Test
    fun statementAddingScreen_emptyData_disabledButton() {
        val statementAddingUiState = StatementAddingUiState(
            definingThemes = FakeDataSource.definingThemes,
            statementDetails = StatementDetails("", null, null, 1),
            requestStatus = RequestStatus.UNDEFINED
        )

        assertAddStatementButtonIsDisabled(statementAddingUiState)
    }

    @Test
    fun statementAddingScreen_allData_enabledButton() {
        val statementAddingUiState = StatementAddingUiState(
            definingThemes = FakeDataSource.definingThemes,
            statementDetails = StatementDetails("fdfsd", true, 1, 1),
            requestStatus = RequestStatus.UNDEFINED
        )

        assertAddStatementButtonIsEnabled(statementAddingUiState)
    }

    @Test
    fun statementAddingScreen_emptyStatementText_disabledButton() {
        val statementAddingUiState = StatementAddingUiState(
            definingThemes = FakeDataSource.definingThemes,
            statementDetails = StatementDetails("", true, 1, 1),
            requestStatus = RequestStatus.UNDEFINED
        )

        assertAddStatementButtonIsDisabled(statementAddingUiState)
    }

    @Test
    fun statementAddingScreen_emptySupportDefiningTheme_disabledButton() {
        val statementAddingUiState = StatementAddingUiState(
            definingThemes = FakeDataSource.definingThemes,
            statementDetails = StatementDetails("fdfsd", null, 1, 1),
            requestStatus = RequestStatus.UNDEFINED
        )

        assertAddStatementButtonIsDisabled(statementAddingUiState)
    }

    @Test
    fun statementAddingScreen_emptyDefiningTheme_disabledButton() {
        val statementAddingUiState = StatementAddingUiState(
            definingThemes = FakeDataSource.definingThemes,
            statementDetails = StatementDetails("fdfsd", true, null, 1),
            requestStatus = RequestStatus.UNDEFINED
        )

        assertAddStatementButtonIsDisabled(statementAddingUiState)
    }

    private fun assertAddStatementButtonIsEnabled(statementAddingUiState: StatementAddingUiState) {
        setContentToStatementAddingBody(statementAddingUiState)

        composeTestRule.onNodeWithTextId(R.string.add_statement).assertIsEnabled()
    }

    private fun assertAddStatementButtonIsDisabled(statementAddingUiState: StatementAddingUiState) {
        setContentToStatementAddingBody(statementAddingUiState)

        composeTestRule.onNodeWithTextId(R.string.add_statement).assertIsNotEnabled()
    }

    private fun setContentToStatementAddingBody(statementAddingUiState: StatementAddingUiState) {
        composeTestRule.activity.setContent {
            StatementAddingBody(
                statementAddingUiState = statementAddingUiState,
                onValueChange = { },
                onStatementAddingClick = { },
                onSuccessStatementAdding = { },
            )
        }
    }
}