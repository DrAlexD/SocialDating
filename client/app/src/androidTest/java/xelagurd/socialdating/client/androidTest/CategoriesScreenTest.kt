package xelagurd.socialdating.client.androidTest

import androidx.compose.ui.test.assertIsDisplayed
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
import xelagurd.socialdating.client.AndroidTestUtils.onNodeWithTagId
import xelagurd.socialdating.client.AndroidTestUtils.onNodeWithTextId
import xelagurd.socialdating.client.AndroidTestUtils.setContentToScreen
import xelagurd.socialdating.client.AndroidTestUtils.setContentToScreenAndRecompose
import xelagurd.socialdating.client.MainActivity
import xelagurd.socialdating.client.R
import xelagurd.socialdating.client.data.fake.FakeData
import xelagurd.socialdating.client.ui.screen.CategoriesScreenComponent
import xelagurd.socialdating.client.ui.state.CategoriesUiState
import xelagurd.socialdating.client.ui.state.RequestStatus

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
    fun categoriesScreen_defaultParameters_loadingIndicator() {
        composeTestRule.setContentToScreen {
            CategoriesScreenComponent()
        }

        composeTestRule.onNodeWithTagId(R.string.loading).assertIsDisplayed()
    }

    @Test
    fun categoriesScreen_recomposition_loadingIndicator() {
        composeTestRule.setContentToScreenAndRecompose {
            CategoriesScreenComponent(categoriesUiState = CategoriesUiState())
        }

        composeTestRule.onNodeWithTagId(R.string.loading).assertIsDisplayed()
    }

    @Test
    fun categoriesScreen_data_displayedData() {
        val categoriesUiState = CategoriesUiState(
            entities = categories,
            dataRequestStatus = RequestStatus.SUCCESS
        )

        setContentToCategoriesBody(categoriesUiState)

        composeTestRule.onNodeWithText(categories[0].name).assertIsDisplayed()
        composeTestRule.onNodeWithText(categories[1].name).assertIsDisplayed()
    }

    @Test
    fun categoriesScreen_allActions_calledAllActions() {
        var clickedCategoryId = -1
        var isRefreshClicked = false
        val categoriesUiState = CategoriesUiState(
            entities = categories,
            dataRequestStatus = RequestStatus.SUCCESS
        )

        composeTestRule.setContentToScreen {
            CategoriesScreenComponent(
                categoriesUiState = categoriesUiState,
                onCategoryClick = { clickedCategoryId = it },
                refreshAction = { isRefreshClicked = true }
            )
        }

        composeTestRule.onNodeWithText(categories[0].name).checkButtonAndClick()
        assertEquals(categories[0].id, clickedCategoryId)

        composeTestRule.onNodeWithTextId(R.string.online).checkButtonAndClick()
        assertTrue(isRefreshClicked)
    }

    private fun setContentToCategoriesBody(categoriesUiState: CategoriesUiState) {
        composeTestRule.setContentToScreen {
            CategoriesScreenComponent(
                categoriesUiState = categoriesUiState
            )
        }
    }
}