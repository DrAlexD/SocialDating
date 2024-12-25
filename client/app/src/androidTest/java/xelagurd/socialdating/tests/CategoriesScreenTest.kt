package xelagurd.socialdating.tests

import androidx.activity.compose.setContent
import androidx.compose.ui.test.assertIsSelected
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
import xelagurd.socialdating.onNodeWithContentDescriptionId
import xelagurd.socialdating.onNodeWithTagId
import xelagurd.socialdating.onNodeWithTextId
import xelagurd.socialdating.ui.screen.CategoriesBody
import xelagurd.socialdating.ui.screen.CategoriesScreen
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
    fun categoriesScreen_checkTopAndBottomBars() {
        composeTestRule.activity.setContent {
            CategoriesScreen(onCategoryClick = {})
        }

        composeTestRule.onNodeWithContentDescriptionId(R.string.back_button).assertDoesNotExist()
        composeTestRule.onNodeWithTagId(R.string.refresh).assertExists()

        composeTestRule.onNodeWithTagId(R.string.nav_categories).assertExists()
        composeTestRule.onNodeWithTagId(R.string.nav_categories).assertIsSelected()
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

        composeTestRule.onNodeWithTextId(R.string.loading).assertExists()
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

        composeTestRule.onNodeWithTextId(R.string.offline).assertExists()
    }

    @Test
    fun categoriesScreen_onlineStatusAndEmptyData_onlineText() {
        val categoriesUiState = CategoriesUiState(listOf())

        composeTestRule.activity.setContent {
            CategoriesBody(
                categoriesUiState = categoriesUiState,
                internetStatus = InternetStatus.ONLINE,
                onCategoryClick = {}
            )
        }

        composeTestRule.onNodeWithTextId(R.string.online).assertExists()
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

        composeTestRule.onNodeWithText("MyCategory").assertExists()
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

        composeTestRule.onNodeWithText("MyCategory").assertExists()
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

        composeTestRule.onNodeWithText("MyCategory").assertExists()
    }
}