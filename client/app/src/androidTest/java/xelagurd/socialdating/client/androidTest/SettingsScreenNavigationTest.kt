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
import xelagurd.socialdating.client.logoutAndNavigateToLoginScreen
import xelagurd.socialdating.client.navigateToCategoriesFromBottomNavBar
import xelagurd.socialdating.client.navigateToProfileFromBottomNavBar
import xelagurd.socialdating.client.navigateToSettingsFromBottomNavBar
import xelagurd.socialdating.client.ui.navigation.AppNavHost
import xelagurd.socialdating.client.ui.navigation.CategoriesDestination
import xelagurd.socialdating.client.ui.navigation.LoginDestination
import xelagurd.socialdating.client.ui.navigation.ProfileDestination

@HiltAndroidTest
class SettingsScreenNavigationTest {
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
    fun appNavHost_performLogout_navigatesToLoginScreen() {
        composeTestRule.loginAndNavigateToCategories()
        composeTestRule.navigateToSettingsFromBottomNavBar()
        composeTestRule.logoutAndNavigateToLoginScreen()

        navController.assertCurrentRouteName(LoginDestination.route)
        //navController.assertBackStackDepth(?)
    }

    @Test
    fun appNavHost_navigateToSettings_stayOnSettingsScreen() {
        composeTestRule.loginAndNavigateToCategories()
        composeTestRule.navigateToSettingsFromBottomNavBar()
        val previousRoute = navController.getCurrentRoute()
        composeTestRule.navigateToSettingsFromBottomNavBar()
        val currentRoute = navController.getCurrentRoute()

        assertEquals(previousRoute, currentRoute)
        //navController.assertBackStackDepth(?)
    }

    @Test
    fun appNavHost_navigateToProfile_navigatesToProfileScreen() {
        composeTestRule.loginAndNavigateToCategories()
        composeTestRule.navigateToSettingsFromBottomNavBar()
        composeTestRule.navigateToProfileFromBottomNavBar()

        navController.assertCurrentRouteName(ProfileDestination.routeWithArgs)
        //navController.assertBackStackDepth(?)
    }

    @Test
    fun appNavHost_navigateToCategories_navigatesToCategoriesScreen() {
        composeTestRule.loginAndNavigateToCategories()
        composeTestRule.navigateToSettingsFromBottomNavBar()
        composeTestRule.navigateToCategoriesFromBottomNavBar()

        navController.assertCurrentRouteName(CategoriesDestination.route)
        //navController.assertBackStackDepth(?)
    }
}