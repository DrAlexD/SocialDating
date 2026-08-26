package xelagurd.socialdating.client.androidTest

import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.navigation.testing.TestNavHostController
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import xelagurd.socialdating.client.AndroidNavigationTestUtils.loginAndNavigateToCategories
import xelagurd.socialdating.client.AndroidNavigationTestUtils.logoutAndNavigateToLoginScreen
import xelagurd.socialdating.client.AndroidNavigationTestUtils.navigateToCategoriesFromBottomNavBar
import xelagurd.socialdating.client.AndroidNavigationTestUtils.navigateToProfileFromBottomNavBar
import xelagurd.socialdating.client.AndroidNavigationTestUtils.navigateToSettingsFromBottomNavBar
import xelagurd.socialdating.client.AndroidNavigationTestUtils.navigateToSimilarUsersFromBottomNavBar
import xelagurd.socialdating.client.AndroidNavigationTestUtils.setContentToAppNavHost
import xelagurd.socialdating.client.AndroidTestUtils.assertBackStackDepth
import xelagurd.socialdating.client.AndroidTestUtils.assertCurrentRouteName
import xelagurd.socialdating.client.AndroidTestUtils.assertRouteNotInBackStack
import xelagurd.socialdating.client.AndroidTestUtils.getCurrentRoute
import xelagurd.socialdating.client.MainActivity
import xelagurd.socialdating.client.ui.navigation.CategoriesDestination
import xelagurd.socialdating.client.ui.navigation.LoginDestination
import xelagurd.socialdating.client.ui.navigation.ProfileDestination
import xelagurd.socialdating.client.ui.navigation.SettingsDestination
import xelagurd.socialdating.client.ui.navigation.SimilarUsersDestination

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

        navController = composeTestRule.setContentToAppNavHost()
    }

    @Test
    fun appNavHost_performLogout_navigatesToLoginScreenWithoutSettings() {
        composeTestRule.loginAndNavigateToCategories()
        composeTestRule.navigateToSettingsFromBottomNavBar()
        composeTestRule.logoutAndNavigateToLoginScreen()

        navController.assertCurrentRouteName(LoginDestination.route)
        navController.assertRouteNotInBackStack(SettingsDestination.route)
        //navController.assertBackStackDepth(2)
    }

    @Test
    fun appNavHost_navigateToSettings_stayOnSettingsScreen() {
        composeTestRule.loginAndNavigateToCategories()
        composeTestRule.navigateToSettingsFromBottomNavBar()
        val previousRoute = navController.getCurrentRoute()
        composeTestRule.navigateToSettingsFromBottomNavBar()
        val currentRoute = navController.getCurrentRoute()

        assertEquals(previousRoute, currentRoute)
        //navController.assertBackStackDepth(4)
    }

    @Test
    fun appNavHost_navigateToProfile_navigatesToProfileScreenWithoutSettings() {
        composeTestRule.loginAndNavigateToCategories()
        composeTestRule.navigateToSettingsFromBottomNavBar()
        composeTestRule.navigateToProfileFromBottomNavBar()

        navController.assertCurrentRouteName(ProfileDestination.routeWithArgs)
        navController.assertRouteNotInBackStack(SettingsDestination.route)
        //navController.assertBackStackDepth(4)
    }

    @Test
    fun appNavHost_navigateToSimilarUsers_navigatesToSimilarUsersScreenWithoutSettings() {
        composeTestRule.loginAndNavigateToCategories()
        composeTestRule.navigateToSettingsFromBottomNavBar()
        composeTestRule.navigateToSimilarUsersFromBottomNavBar()

        navController.assertCurrentRouteName(SimilarUsersDestination.routeWithArgs)
        navController.assertRouteNotInBackStack(SettingsDestination.route)
        //navController.assertBackStackDepth(4)
    }

    @Test
    fun appNavHost_navigateToCategories_navigatesToCategoriesScreenWithoutSettings() {
        composeTestRule.loginAndNavigateToCategories()
        composeTestRule.navigateToSettingsFromBottomNavBar()
        composeTestRule.navigateToCategoriesFromBottomNavBar()

        navController.assertCurrentRouteName(CategoriesDestination.route)
        navController.assertRouteNotInBackStack(SettingsDestination.route)
        //navController.assertBackStackDepth(3)
    }
}