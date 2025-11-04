package xelagurd.socialdating.client.androidTest

import androidx.activity.compose.setContent
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithText
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import xelagurd.socialdating.client.MainActivity
import xelagurd.socialdating.client.R
import xelagurd.socialdating.client.data.fake.FakeData
import xelagurd.socialdating.client.onNodeWithTagId
import xelagurd.socialdating.client.ui.screen.CategoriesScreenComponent
import xelagurd.socialdating.client.ui.state.CategoriesUiState
import xelagurd.socialdating.client.ui.state.RequestStatus
import xelagurd.socialdating.client.ui.theme.AppTheme

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

    private val categories = FakeData.categories

    @Test
    fun categoriesScreen_loadingStateAndEmptyData_loadingIndicator() {
        val categoriesUiState = CategoriesUiState()

        setContentToCategoriesBody(categoriesUiState)

        composeTestRule.onNodeWithTagId(R.string.loading).assertIsDisplayed()
    }

    @Test
    fun categoriesScreen_errorStateAndEmptyData_errorText() {
        val errorText = FakeData.ERROR_TEXT
        val categoriesUiState = CategoriesUiState(
            dataRequestStatus = RequestStatus.ERROR(errorText)
        )

        setContentToCategoriesBody(categoriesUiState)

        composeTestRule.onNodeWithText(errorText).assertIsDisplayed()
    }

    @Test
    fun categoriesScreen_failureStateAndEmptyData_failureText() {
        val failureText = FakeData.FAILURE_TEXT
        val categoriesUiState = CategoriesUiState(
            dataRequestStatus = RequestStatus.FAILURE(failureText)
        )

        setContentToCategoriesBody(categoriesUiState)

        composeTestRule.onNodeWithText(failureText).assertIsDisplayed()
    }

    @Test
    fun categoriesScreen_loadingStateAndData_displayedData() {
        val categoriesUiState = CategoriesUiState(
            entities = categories,
            dataRequestStatus = RequestStatus.LOADING
        )

        assertDataIsDisplayed(categoriesUiState)
    }

    @Test
    fun categoriesScreen_errorStateAndData_displayedData() {
        val categoriesUiState = CategoriesUiState(
            entities = categories,
            dataRequestStatus = RequestStatus.ERROR()
        )

        assertDataIsDisplayed(categoriesUiState)
    }

    @Test
    fun categoriesScreen_failureStateAndData_displayedData() {
        val categoriesUiState = CategoriesUiState(
            entities = categories,
            dataRequestStatus = RequestStatus.FAILURE()
        )

        assertDataIsDisplayed(categoriesUiState)
    }

    @Test
    fun categoriesScreen_successStateAndData_displayedData() {
        val categoriesUiState = CategoriesUiState(
            entities = categories,
            dataRequestStatus = RequestStatus.SUCCESS
        )

        assertDataIsDisplayed(categoriesUiState)
    }

    private fun assertDataIsDisplayed(categoriesUiState: CategoriesUiState) {
        setContentToCategoriesBody(categoriesUiState)

        composeTestRule.onNodeWithText(categories[0].name).assertIsDisplayed()
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