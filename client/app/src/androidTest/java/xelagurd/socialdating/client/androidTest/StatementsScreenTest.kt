package xelagurd.socialdating.client.androidTest

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsNotDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithText
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import xelagurd.socialdating.client.AndroidTestUtils.checkButtonAndClick
import xelagurd.socialdating.client.AndroidTestUtils.checkEnabledButton
import xelagurd.socialdating.client.AndroidTestUtils.onNodeWithContentDescriptionId
import xelagurd.socialdating.client.AndroidTestUtils.onNodeWithTagId
import xelagurd.socialdating.client.AndroidTestUtils.onNodeWithTextId
import xelagurd.socialdating.client.AndroidTestUtils.setContentToScreen
import xelagurd.socialdating.client.AndroidTestUtils.setContentToScreenAndRecompose
import xelagurd.socialdating.client.MainActivity
import xelagurd.socialdating.client.R
import xelagurd.socialdating.client.data.fake.FakeData
import xelagurd.socialdating.client.data.model.Statement
import xelagurd.socialdating.client.data.model.enums.StatementReactionType
import xelagurd.socialdating.client.ui.screen.StatementsScreenComponent
import xelagurd.socialdating.client.ui.state.RequestStatus
import xelagurd.socialdating.client.ui.state.StatementsUiState

@HiltAndroidTest
class StatementsScreenTest {
    @get:Rule(order = 0)
    val hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 1)
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    @Before
    fun setup() {
        hiltRule.inject()
    }

    private val statements = listOf(FakeData.statements[0])

    @Test
    fun statementsScreen_defaultParameters_loadingIndicator() {
        composeTestRule.setContentToScreen {
            StatementsScreenComponent()
        }

        composeTestRule.onNodeWithTagId(R.string.loading).assertIsDisplayed()
    }

    @Test
    fun statementsScreen_recomposition_loadingIndicator() {
        composeTestRule.setContentToScreenAndRecompose {
            StatementsScreenComponent(statementsUiState = StatementsUiState())
        }

        composeTestRule.onNodeWithTagId(R.string.loading).assertIsDisplayed()
    }

    @Test
    fun statementsScreen_data_displayedData() {
        val statementsUiState = StatementsUiState(
            entities = statements,
            dataRequestStatus = RequestStatus.SUCCESS
        )

        setContentToStatementsBody(statementsUiState)

        composeTestRule.onNodeWithText(statements[0].text).assertIsDisplayed()
        composeTestRule.onNodeWithContentDescriptionId(R.string.full_maintain).checkEnabledButton()
        composeTestRule.onNodeWithContentDescriptionId(R.string.part_maintain).checkEnabledButton()
        composeTestRule.onNodeWithContentDescriptionId(R.string.not_sure).checkEnabledButton()
        composeTestRule.onNodeWithContentDescriptionId(R.string.part_no_maintain).checkEnabledButton()
        composeTestRule.onNodeWithContentDescriptionId(R.string.full_no_maintain).checkEnabledButton()

        composeTestRule.onNodeWithContentDescriptionId(R.string.add_statement).checkEnabledButton()
    }

    @Test
    fun statementsScreen_emptyData_withoutAddStatementButton() {
        val statementsUiState = StatementsUiState(
            dataRequestStatus = RequestStatus.SUCCESS
        )

        setContentToStatementsBody(statementsUiState)

        composeTestRule.onNodeWithContentDescriptionId(R.string.add_statement).assertIsNotDisplayed()
    }

    @Test
    fun statementsScreen_allActions_calledAllActions() {
        var reaction: Pair<Statement, StatementReactionType>? = null
        var addingCategoryId = -1
        var isNavigateUpClicked = false
        var isRefreshClicked = false
        val statementsUiState = StatementsUiState(
            categoryId = FakeData.mainCategory.id,
            entities = statements,
            dataRequestStatus = RequestStatus.SUCCESS
        )

        composeTestRule.setContentToScreen {
            StatementsScreenComponent(
                statementsUiState = statementsUiState,
                onStatementClick = {},
                onStatementAddingClick = { addingCategoryId = it },
                onNavigateUp = { isNavigateUpClicked = true },
                refreshAction = { isRefreshClicked = true },
                onStatementReactionClick = { statement, reactionType ->
                    reaction = statement to reactionType
                }
            )
        }

        composeTestRule.onNodeWithText(statements[0].text).checkButtonAndClick()

        composeTestRule.onNodeWithContentDescriptionId(R.string.full_maintain).checkButtonAndClick()
        assertEquals(statements[0] to StatementReactionType.FULL_MAINTAIN, reaction)

        composeTestRule.onNodeWithContentDescriptionId(R.string.add_statement).checkButtonAndClick()
        assertEquals(FakeData.mainCategory.id, addingCategoryId)

        composeTestRule.onNodeWithContentDescriptionId(R.string.back_button).checkButtonAndClick()
        assertTrue(isNavigateUpClicked)

        composeTestRule.onNodeWithTextId(R.string.online).checkButtonAndClick()
        assertTrue(isRefreshClicked)
    }

    private fun setContentToStatementsBody(statementsUiState: StatementsUiState) {
        composeTestRule.setContentToScreen {
            StatementsScreenComponent(
                statementsUiState = statementsUiState
            )
        }
    }
}