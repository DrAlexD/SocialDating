package xelagurd.socialdating.client.androidTest

import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.navigation.testing.TestNavHostController
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import xelagurd.socialdating.client.AndroidNavigationTestUtils.navigateToCategoriesFromBottomNavBar
import xelagurd.socialdating.client.AndroidNavigationTestUtils.navigateToProfileFromBottomNavBar
import xelagurd.socialdating.client.AndroidNavigationTestUtils.navigateToSettingsFromBottomNavBar
import xelagurd.socialdating.client.AndroidNavigationTestUtils.navigateToSimilarUsersFromBottomNavBar
import xelagurd.socialdating.client.AndroidNavigationTestUtils.navigateToStatementAdding
import xelagurd.socialdating.client.AndroidNavigationTestUtils.navigateToStatements
import xelagurd.socialdating.client.AndroidNavigationTestUtils.performNavigateUp
import xelagurd.socialdating.client.AndroidNavigationTestUtils.setContentToAppNavHost
import xelagurd.socialdating.client.AndroidTestUtils.assertBackStackDepth
import xelagurd.socialdating.client.AndroidTestUtils.assertCurrentRouteName
import xelagurd.socialdating.client.AndroidTestUtils.assertRouteInBackStack
import xelagurd.socialdating.client.AndroidTestUtils.assertRouteNotInBackStack
import xelagurd.socialdating.client.AndroidTestUtils.getCurrentRoute
import xelagurd.socialdating.client.MainActivity
import xelagurd.socialdating.client.ui.navigation.CategoriesDestination
import xelagurd.socialdating.client.ui.navigation.StatementAddingDestination
import xelagurd.socialdating.client.ui.navigation.StatementsDestination

@HiltAndroidTest
class StatementsScreenNavigationTest {
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
    fun appNavHost_clickAddStatement_navigatesToStatementAddingScreenWithStatementsInBackStack() {
        composeTestRule.navigateToStatementAdding()

        navController.assertCurrentRouteName(StatementAddingDestination.routeWithArgs)
        navController.assertRouteInBackStack(StatementsDestination.routeWithArgs)
        //navController.assertBackStackDepth(5)
    }

    @Test
    fun appNavHost_clickBack_navigatesToCategoriesScreen() {
        composeTestRule.navigateToStatements()
        composeTestRule.performNavigateUp()

        navController.assertCurrentRouteName(CategoriesDestination.route)
        navController.assertRouteNotInBackStack(StatementsDestination.routeWithArgs)
        //navController.assertBackStackDepth(3)
    }

    @Test
    fun appNavHost_navigateToCategories_stayOnStatementsScreen() {
        composeTestRule.navigateToStatements()
        val previousRoute = navController.getCurrentRoute()
        composeTestRule.navigateToCategoriesFromBottomNavBar()
        val currentRoute = navController.getCurrentRoute()

        assertEquals(previousRoute, currentRoute)
        //navController.assertBackStackDepth(4)
    }

    @Test
    fun appNavHost_navigateToProfileAndBack_restoredStatementsScreenWithCategories() {
        composeTestRule.navigateToStatements()
        composeTestRule.navigateToProfileFromBottomNavBar()
        composeTestRule.navigateToCategoriesFromBottomNavBar()

        navController.assertCurrentRouteName(StatementsDestination.routeWithArgs)
        navController.assertRouteInBackStack(CategoriesDestination.route)
        //navController.assertBackStackDepth(4)
    }

    @Test
    fun appNavHost_navigateToSimilarUsersAndBack_restoredStatementsScreenWithCategories() {
        composeTestRule.navigateToStatements()
        composeTestRule.navigateToSimilarUsersFromBottomNavBar()
        composeTestRule.navigateToCategoriesFromBottomNavBar()

        navController.assertCurrentRouteName(StatementsDestination.routeWithArgs)
        navController.assertRouteInBackStack(CategoriesDestination.route)
        //navController.assertBackStackDepth(4)
    }

    @Test
    fun appNavHost_navigateToSettingsAndBack_restoredStatementsScreenWithCategories() {
        composeTestRule.navigateToStatements()
        composeTestRule.navigateToSettingsFromBottomNavBar()
        composeTestRule.navigateToCategoriesFromBottomNavBar()

        navController.assertCurrentRouteName(StatementsDestination.routeWithArgs)
        navController.assertRouteInBackStack(CategoriesDestination.route)
        //navController.assertBackStackDepth(4)
    }
}