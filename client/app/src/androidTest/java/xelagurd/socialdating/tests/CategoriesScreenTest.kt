package xelagurd.socialdating.tests

import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
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
        val categoriesUiState = CategoriesUiState(listOf())

        composeTestRule.activity.setContent {
            CategoriesBody(
                categoriesUiState = categoriesUiState,
                internetStatus = InternetStatus.LOADING,
                onCategoryClick = {}
            )
        }

        composeTestRule.onNodeWithTextId(R.string.loading).assertIsDisplayed()
    }

    @Test
    fun categoriesScreen_offlineStatusAndEmptyData_offlineText() {
        val categoriesUiState = CategoriesUiState(listOf())

        composeTestRule.activity.setContent {
            CategoriesBody(
                categoriesUiState = categoriesUiState,
                internetStatus = InternetStatus.OFFLINE,
                onCategoryClick = {}
            )
        }

        composeTestRule.onNodeWithTextId(R.string.no_internet_connection).assertIsDisplayed()
    }

    @Test
    fun categoriesScreen_onlineStatusAndEmptyData_onlineText() {
        val categoriesUiState = CategoriesUiState(listOf())

        composeTestRule.activity.setContent {
            Scaffold { innerPadding ->
                CategoriesBody(
                    categoriesUiState = categoriesUiState,
                    internetStatus = InternetStatus.ONLINE,
                    onCategoryClick = {},
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = innerPadding
                )
            }
        }

        composeTestRule.onNodeWithTextId(R.string.no_data).assertIsDisplayed()
    }

    @Test
    fun categoriesScreen_loadingStatusAndData_displayedData() {
        val categoriesUiState = CategoriesUiState(listOf(Category(1, "MyCategory")))

        composeTestRule.activity.setContent {
            CategoriesBody(
                categoriesUiState = categoriesUiState,
                internetStatus = InternetStatus.LOADING,
                onCategoryClick = {}
            )
        }

        composeTestRule.onNodeWithText("MyCategory").assertIsDisplayed()
    }

    @Test
    fun categoriesScreen_offlineStatusAndData_displayedData() {
        val categoriesUiState = CategoriesUiState(listOf(Category(1, "MyCategory")))

        composeTestRule.activity.setContent {
            CategoriesBody(
                categoriesUiState = categoriesUiState,
                internetStatus = InternetStatus.OFFLINE,
                onCategoryClick = {}
            )
        }

        composeTestRule.onNodeWithText("MyCategory").assertIsDisplayed()
    }

    @Test
    fun categoriesScreen_onlineStatusAndData_displayedData() {
        val categoriesUiState = CategoriesUiState(listOf(Category(1, "MyCategory")))

        composeTestRule.activity.setContent {
            CategoriesBody(
                categoriesUiState = categoriesUiState,
                internetStatus = InternetStatus.ONLINE,
                onCategoryClick = {}
            )
        }

        composeTestRule.onNodeWithText("MyCategory").assertIsDisplayed()
    }
}