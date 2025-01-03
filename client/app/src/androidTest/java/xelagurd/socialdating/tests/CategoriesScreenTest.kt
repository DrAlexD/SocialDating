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
import xelagurd.socialdating.onNodeWithTextId
import xelagurd.socialdating.ui.screen.CategoriesBody
import xelagurd.socialdating.ui.state.CategoriesUiState
import xelagurd.socialdating.ui.state.InternetStatus

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
    fun categoriesScreen_loadingStatusAndEmptyData_loadingText() {
        val categoriesUiState = CategoriesUiState()

        composeTestRule.activity.setContent {
            CategoriesBody(
                categoriesUiState = categoriesUiState,
                onCategoryClick = {}
            )
        }

        composeTestRule.onNodeWithTextId(R.string.loading).assertIsDisplayed()
    }

    @Test
    fun categoriesScreen_offlineStatusAndEmptyData_offlineText() {
        val categoriesUiState = CategoriesUiState(internetStatus = InternetStatus.OFFLINE)

        composeTestRule.activity.setContent {
            CategoriesBody(
                categoriesUiState = categoriesUiState,
                onCategoryClick = {}
            )
        }

        composeTestRule.onNodeWithTextId(R.string.no_internet_connection).assertIsDisplayed()
    }

    @Test
    fun categoriesScreen_onlineStatusAndEmptyData_onlineText() {
        val categoriesUiState = CategoriesUiState(internetStatus = InternetStatus.ONLINE)

        composeTestRule.activity.setContent {
            CategoriesBody(
                categoriesUiState = categoriesUiState,
                onCategoryClick = {}
            )
        }

        composeTestRule.onNodeWithTextId(R.string.no_data).assertIsDisplayed()
    }

    @Test
    fun categoriesScreen_loadingStatusAndData_displayedData() {
        val categoriesUiState = CategoriesUiState(
            categories = listOf(Category(1, "MyCategory")),
            internetStatus = InternetStatus.LOADING
        )

        composeTestRule.activity.setContent {
            CategoriesBody(
                categoriesUiState = categoriesUiState,
                onCategoryClick = {}
            )
        }

        composeTestRule.onNodeWithText("MyCategory").assertIsDisplayed()
    }

    @Test
    fun categoriesScreen_offlineStatusAndData_displayedData() {
        val categoriesUiState = CategoriesUiState(
            categories = listOf(Category(1, "MyCategory")),
            internetStatus = InternetStatus.OFFLINE
        )

        composeTestRule.activity.setContent {
            CategoriesBody(
                categoriesUiState = categoriesUiState,
                onCategoryClick = {}
            )
        }

        composeTestRule.onNodeWithText("MyCategory").assertIsDisplayed()
    }

    @Test
    fun categoriesScreen_onlineStatusAndData_displayedData() {
        val categoriesUiState = CategoriesUiState(
            categories = listOf(Category(1, "MyCategory")),
            internetStatus = InternetStatus.ONLINE
        )

        composeTestRule.activity.setContent {
            CategoriesBody(
                categoriesUiState = categoriesUiState,
                onCategoryClick = {}
            )
        }

        composeTestRule.onNodeWithText("MyCategory").assertIsDisplayed()
    }
}