package xelagurd.socialdating.client.androidTest

import androidx.activity.compose.setContent
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.navigation.compose.ComposeNavigator
import androidx.navigation.testing.TestNavHostController
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import xelagurd.socialdating.client.MainActivity
import xelagurd.socialdating.client.assertCurrentRouteName
import xelagurd.socialdating.client.getCurrentRoute
import xelagurd.socialdating.client.loginAndNavigateToCategories
import xelagurd.socialdating.client.navigateToCategoriesFromBottomNavBar
import xelagurd.socialdating.client.navigateToProfileFromBottomNavBar
import xelagurd.socialdating.client.navigateToSettingsFromBottomNavBar
import xelagurd.socialdating.client.navigateToStatements
import xelagurd.socialdating.client.ui.navigation.AppNavHost
import xelagurd.socialdating.client.ui.navigation.ProfileDestination
import xelagurd.socialdating.client.ui.navigation.SettingsDestination
import xelagurd.socialdating.client.ui.navigation.StatementsDestination

@HiltAndroidTest
class CategoriesScreenNavigationTest {
    @get:Rule(order = 0)
    val hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 1)
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    private lateinit var navController: TestNavHostController

    @Before
    fun setup() {
        hiltRule.inject()

        composeTestRule.activity.setContent {
            navController = TestNavHostController(composeTestRule.activity).apply {
                navigatorProvider.addNavigator(ComposeNavigator())
            }
            AppNavHost(navController)
        }

        composeTestRule.runOnIdle {
            assert(::navController.isInitialized)
        }
    }

    @Test
    fun appNavHost_clickCategory_navigatesToStatements() {
        composeTestRule.navigateToStatements()

        navController.assertCurrentRouteName(StatementsDestination.routeWithArgs)
        //navController.assertBackStackDepth(?)
    }

    @Test
    fun appNavHost_navigateToCategories_stayOnCategoriesScreen() {
        composeTestRule.loginAndNavigateToCategories()
        val previousRoute = navController.getCurrentRoute()
        composeTestRule.navigateToCategoriesFromBottomNavBar()
        val currentRoute = navController.getCurrentRoute()

        assertEquals(previousRoute, currentRoute)
        //navController.assertBackStackDepth(?)
    }

    @Test
    fun appNavHost_navigateToProfile_navigatesToProfile() {
        composeTestRule.loginAndNavigateToCategories()
        composeTestRule.navigateToProfileFromBottomNavBar()

        navController.assertCurrentRouteName(ProfileDestination.routeWithArgs)
        //navController.assertBackStackDepth(?)
    }

    @Test
    fun appNavHost_navigateToSettings_navigatesToSettings() {
        composeTestRule.loginAndNavigateToCategories()
        composeTestRule.navigateToSettingsFromBottomNavBar()

        navController.assertCurrentRouteName(SettingsDestination.route)
        //navController.assertBackStackDepth(?)
    }
}