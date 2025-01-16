package xelagurd.socialdating.tests

import androidx.activity.compose.setContent
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithText
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import xelagurd.socialdating.MainActivity
import xelagurd.socialdating.R
import xelagurd.socialdating.data.model.Category
import xelagurd.socialdating.onNodeWithTagId
import xelagurd.socialdating.ui.screen.CategoriesScreenComponent
import xelagurd.socialdating.ui.state.CategoriesUiState
import xelagurd.socialdating.ui.state.RequestStatus
import xelagurd.socialdating.ui.theme.AppTheme

@HiltAndroidTest
class CategoriesScreenTest {
    @get:Rule(order = 0)
    val hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 1)
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    @Before
    fun setup() {
        hiltRule.inject()
    }

    @Test
    fun categoriesScreen_loadingStateAndEmptyData_loadingIndicator() {
        val categoriesUiState = CategoriesUiState()

        setContentToCategoriesBody(categoriesUiState)

        composeTestRule.onNodeWithTagId(R.string.loading).assertIsDisplayed()
    }

    @Test
    fun categoriesScreen_errorStateAndEmptyData_errorText() {
        val errorText = "Error Text"
        val categoriesUiState = CategoriesUiState(
            dataRequestStatus = RequestStatus.ERROR(errorText)
        )

        setContentToCategoriesBody(categoriesUiState)

        composeTestRule.onNodeWithText(errorText).assertIsDisplayed()
    }

    @Test
    fun categoriesScreen_failureStateAndEmptyData_failureText() {
        val failureText = "Failure Text"
        val categoriesUiState = CategoriesUiState(
            dataRequestStatus = RequestStatus.FAILURE(failureText)
        )

        setContentToCategoriesBody(categoriesUiState)

        composeTestRule.onNodeWithText(failureText).assertIsDisplayed()
    }

    @Test
    fun categoriesScreen_loadingStateAndData_displayedData() {
        val categoriesUiState = CategoriesUiState(
            entities = listOf(Category(1, "Category1")),
            dataRequestStatus = RequestStatus.LOADING
        )

        assertDataIsDisplayed(categoriesUiState)
    }

    @Test
    fun categoriesScreen_errorStateAndData_displayedData() {
        val categoriesUiState = CategoriesUiState(
            entities = listOf(Category(1, "Category1")),
            dataRequestStatus = RequestStatus.ERROR()
        )

        assertDataIsDisplayed(categoriesUiState)
    }

    @Test
    fun categoriesScreen_failureStateAndData_displayedData() {
        val categoriesUiState = CategoriesUiState(
            entities = listOf(Category(1, "Category1")),
            dataRequestStatus = RequestStatus.FAILURE()
        )

        assertDataIsDisplayed(categoriesUiState)
    }

    @Test
    fun categoriesScreen_successStateAndData_displayedData() {
        val categoriesUiState = CategoriesUiState(
            entities = listOf(Category(1, "Category1")),
            dataRequestStatus = RequestStatus.SUCCESS
        )

        assertDataIsDisplayed(categoriesUiState)
    }

    private fun assertDataIsDisplayed(categoriesUiState: CategoriesUiState) {
        setContentToCategoriesBody(categoriesUiState)

        composeTestRule.onNodeWithText("Category1").assertIsDisplayed()
    }

    private fun setContentToCategoriesBody(categoriesUiState: CategoriesUiState) {
        composeTestRule.activity.setContent {
            AppTheme {
                CategoriesScreenComponent(
                    categoriesUiState = categoriesUiState
                )
            }
        }
    }
}