package xelagurd.socialdating.client.androidTest

import androidx.compose.ui.Modifier
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsNotSelected
import androidx.compose.ui.test.assertIsSelected
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import xelagurd.socialdating.client.AndroidTestUtils.onNodeWithTagId
import xelagurd.socialdating.client.AndroidTestUtils.setContentToScreen
import xelagurd.socialdating.client.MainActivity
import xelagurd.socialdating.client.R
import xelagurd.socialdating.client.ui.AppBottomNavigationBar
import xelagurd.socialdating.client.ui.navigation.CategoriesDestination
import xelagurd.socialdating.client.ui.navigation.SettingsDestination

@HiltAndroidTest
class BottomBarTest {
    @get:Rule(order = 0)
    val hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 1)
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    @Before
    fun setup() {
        hiltRule.inject()
    }

    @Test
    fun bottomBar_categoriesTopLevelRoute_selectedCategoriesItem() {
        composeTestRule.setContentToScreen {
            AppBottomNavigationBar(
                currentTopLevelRoute = CategoriesDestination.topLevelRoute,
                modifier = Modifier
            )
        }

        composeTestRule.onNodeWithTagId(R.string.nav_categories).assertIsSelected()
        composeTestRule.onNodeWithTagId(R.string.nav_profile).assertIsNotSelected()
        composeTestRule.onNodeWithTagId(R.string.nav_similar_users).assertIsNotSelected()
        composeTestRule.onNodeWithTagId(R.string.nav_settings).assertIsNotSelected()
    }

    @Test
    fun bottomBar_settingsTopLevelRoute_selectedSettingsItem() {
        composeTestRule.setContentToScreen {
            AppBottomNavigationBar(
                currentTopLevelRoute = SettingsDestination.topLevelRoute
            )
        }

        composeTestRule.onNodeWithTagId(R.string.nav_settings).assertIsSelected()
        composeTestRule.onNodeWithTagId(R.string.nav_categories).assertIsNotSelected()
    }

    @Test
    fun bottomBar_anotherUser_withoutSelectedItem() {
        composeTestRule.setContentToScreen {
            AppBottomNavigationBar(
                currentTopLevelRoute = CategoriesDestination.topLevelRoute,
                isCurrentUser = false
            )
        }

        composeTestRule.onNodeWithTagId(R.string.nav_categories).assertIsDisplayed()
        composeTestRule.onNodeWithTagId(R.string.nav_categories).assertIsNotSelected()
    }
}