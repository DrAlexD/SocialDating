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
import xelagurd.socialdating.onNodeWithTextId
import xelagurd.socialdating.ui.screen.CategoriesScreenComponent
import xelagurd.socialdating.ui.state.CategoriesUiState
import xelagurd.socialdating.ui.state.InternetStatus
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
    fun categoriesScreen_loadingStatusAndEmptyData_loadingIndicator() {
        val categoriesUiState = CategoriesUiState()

        setContentToCategoriesBody(categoriesUiState)

        composeTestRule.onNodeWithTagId(R.string.loading).assertIsDisplayed()
    }

    @Test
    fun categoriesScreen_offlineStatusAndEmptyData_offlineText() {
        val categoriesUiState = CategoriesUiState(internetStatus = InternetStatus.OFFLINE)

        setContentToCategoriesBody(categoriesUiState)

        composeTestRule.onNodeWithTextId(R.string.no_internet_connection).assertIsDisplayed()
    }

    @Test
    fun categoriesScreen_onlineStatusAndEmptyData_onlineText() {
        val categoriesUiState = CategoriesUiState(internetStatus = InternetStatus.ONLINE)

        setContentToCategoriesBody(categoriesUiState)

        composeTestRule.onNodeWithTextId(R.string.no_data).assertIsDisplayed()
    }

    @Test
    fun categoriesScreen_loadingStatusAndData_displayedData() {
        val categoriesUiState = CategoriesUiState(
            entities = listOf(Category(1, "Category1")),
            internetStatus = InternetStatus.LOADING
        )

        assertDataIsDisplayed(categoriesUiState)
    }

    @Test
    fun categoriesScreen_offlineStatusAndData_displayedData() {
        val categoriesUiState = CategoriesUiState(
            entities = listOf(Category(1, "Category1")),
            internetStatus = InternetStatus.OFFLINE
        )

        assertDataIsDisplayed(categoriesUiState)
    }

    @Test
    fun categoriesScreen_onlineStatusAndData_displayedData() {
        val categoriesUiState = CategoriesUiState(
            entities = listOf(Category(1, "Category1")),
            internetStatus = InternetStatus.ONLINE
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