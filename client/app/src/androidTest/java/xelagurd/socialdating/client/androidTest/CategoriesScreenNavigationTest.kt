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
import xelagurd.socialdating.client.AndroidNavigationTestUtils.navigateToCategoriesFromBottomNavBar
import xelagurd.socialdating.client.AndroidNavigationTestUtils.navigateToProfileFromBottomNavBar
import xelagurd.socialdating.client.AndroidNavigationTestUtils.navigateToSettingsFromBottomNavBar
import xelagurd.socialdating.client.AndroidNavigationTestUtils.navigateToSimilarUsersFromBottomNavBar
import xelagurd.socialdating.client.AndroidNavigationTestUtils.navigateToStatements
import xelagurd.socialdating.client.AndroidNavigationTestUtils.setContentToAppNavHost
import xelagurd.socialdating.client.AndroidTestUtils.assertBackStackDepth
import xelagurd.socialdating.client.AndroidTestUtils.assertCurrentRouteName
import xelagurd.socialdating.client.AndroidTestUtils.assertRouteInBackStack
import xelagurd.socialdating.client.AndroidTestUtils.getCurrentRoute
import xelagurd.socialdating.client.MainActivity
import xelagurd.socialdating.client.ui.navigation.CategoriesDestination
import xelagurd.socialdating.client.ui.navigation.ProfileDestination
import xelagurd.socialdating.client.ui.navigation.SettingsDestination
import xelagurd.socialdating.client.ui.navigation.SimilarUsersDestination
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

        navController = composeTestRule.setContentToAppNavHost()
    }

    @Test
    fun appNavHost_clickCategory_navigatesToStatementsScreenWithCategoriesInBackStack() {
        composeTestRule.navigateToStatements()

        navController.assertCurrentRouteName(StatementsDestination.routeWithArgs)
        navController.assertRouteInBackStack(CategoriesDestination.route)
        //navController.assertBackStackDepth(4)
    }

    @Test
    fun appNavHost_navigateToCategories_stayOnCategoriesScreen() {
        composeTestRule.loginAndNavigateToCategories()
        val previousRoute = navController.getCurrentRoute()
        composeTestRule.navigateToCategoriesFromBottomNavBar()
        val currentRoute = navController.getCurrentRoute()

        assertEquals(previousRoute, currentRoute)
        //navController.assertBackStackDepth(3)
    }

    @Test
    fun appNavHost_navigateToProfile_navigatesToProfileScreenWithCategoriesInBackStack() {
        composeTestRule.loginAndNavigateToCategories()
        composeTestRule.navigateToProfileFromBottomNavBar()

        navController.assertCurrentRouteName(ProfileDestination.routeWithArgs)
        navController.assertRouteInBackStack(CategoriesDestination.route)
        //navController.assertBackStackDepth(4)
    }

    @Test
    fun appNavHost_navigateToSimilarUsers_navigatesToSimilarUsersScreenWithCategoriesInBackStack() {
        composeTestRule.loginAndNavigateToCategories()
        composeTestRule.navigateToSimilarUsersFromBottomNavBar()

        navController.assertCurrentRouteName(SimilarUsersDestination.routeWithArgs)
        navController.assertRouteInBackStack(CategoriesDestination.route)
        //navController.assertBackStackDepth(4)
    }

    @Test
    fun appNavHost_navigateToSettings_navigatesToSettingsScreenWithCategoriesInBackStack() {
        composeTestRule.loginAndNavigateToCategories()
        composeTestRule.navigateToSettingsFromBottomNavBar()

        navController.assertCurrentRouteName(SettingsDestination.route)
        navController.assertRouteInBackStack(CategoriesDestination.route)
        //navController.assertBackStackDepth(4)
    }
}