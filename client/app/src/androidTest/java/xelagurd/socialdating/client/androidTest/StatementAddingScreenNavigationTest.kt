package xelagurd.socialdating.client.androidTest

import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.navigation.testing.TestNavHostController
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import xelagurd.socialdating.client.AndroidNavigationTestUtils.addStatementAndNavigateToStatementsScreen
import xelagurd.socialdating.client.AndroidNavigationTestUtils.navigateToCategoriesFromBottomNavBar
import xelagurd.socialdating.client.AndroidNavigationTestUtils.navigateToProfileFromBottomNavBar
import xelagurd.socialdating.client.AndroidNavigationTestUtils.navigateToSettingsFromBottomNavBar
import xelagurd.socialdating.client.AndroidNavigationTestUtils.navigateToSimilarUsersFromBottomNavBar
import xelagurd.socialdating.client.AndroidNavigationTestUtils.navigateToStatementAdding
import xelagurd.socialdating.client.AndroidNavigationTestUtils.performNavigateUp
import xelagurd.socialdating.client.AndroidNavigationTestUtils.setContentToAppNavHost
import xelagurd.socialdating.client.AndroidTestUtils.assertBackStackDepth
import xelagurd.socialdating.client.AndroidTestUtils.assertCurrentRouteName
import xelagurd.socialdating.client.AndroidTestUtils.assertRouteInBackStack
import xelagurd.socialdating.client.AndroidTestUtils.assertRouteNotInBackStack
import xelagurd.socialdating.client.AndroidTestUtils.getCurrentRoute
import xelagurd.socialdating.client.MainActivity
import xelagurd.socialdating.client.ui.navigation.StatementAddingDestination
import xelagurd.socialdating.client.ui.navigation.StatementsDestination

@HiltAndroidTest
class StatementAddingScreenNavigationTest {
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
    fun appNavHost_performStatementAdding_navigatesToStatementsScreen() {
        composeTestRule.navigateToStatementAdding()
        composeTestRule.addStatementAndNavigateToStatementsScreen()

        navController.assertCurrentRouteName(StatementsDestination.routeWithArgs)
        navController.assertRouteNotInBackStack(StatementAddingDestination.routeWithArgs)
        //navController.assertBackStackDepth(4)
    }

    @Test
    fun appNavHost_clickBack_navigatesToStatementsScreen() {
        composeTestRule.navigateToStatementAdding()
        composeTestRule.performNavigateUp()

        navController.assertCurrentRouteName(StatementsDestination.routeWithArgs)
        navController.assertRouteNotInBackStack(StatementAddingDestination.routeWithArgs)
        //navController.assertBackStackDepth(4)
    }

    @Test
    fun appNavHost_navigateToCategories_stayOnStatementAddingScreen() {
        composeTestRule.navigateToStatementAdding()
        val previousRoute = navController.getCurrentRoute()
        composeTestRule.navigateToCategoriesFromBottomNavBar()
        val currentRoute = navController.getCurrentRoute()

        assertEquals(previousRoute, currentRoute)
        //navController.assertBackStackDepth(5)
    }

    @Test
    fun appNavHost_navigateToProfileAndBack_restoredStatementAddingScreenWithStatements() {
        composeTestRule.navigateToStatementAdding()
        composeTestRule.navigateToProfileFromBottomNavBar()
        composeTestRule.navigateToCategoriesFromBottomNavBar()

        navController.assertCurrentRouteName(StatementAddingDestination.routeWithArgs)
        navController.assertRouteInBackStack(StatementsDestination.routeWithArgs)
        //navController.assertBackStackDepth(5)
    }

    @Test
    fun appNavHost_navigateToSimilarUsersAndBack_restoredStatementAddingScreenWithStatements() {
        composeTestRule.navigateToStatementAdding()
        composeTestRule.navigateToSimilarUsersFromBottomNavBar()
        composeTestRule.navigateToCategoriesFromBottomNavBar()

        navController.assertCurrentRouteName(StatementAddingDestination.routeWithArgs)
        navController.assertRouteInBackStack(StatementsDestination.routeWithArgs)
        //navController.assertBackStackDepth(5)
    }

    @Test
    fun appNavHost_navigateToSettingsAndBack_restoredStatementAddingScreenWithStatements() {
        composeTestRule.navigateToStatementAdding()
        composeTestRule.navigateToSettingsFromBottomNavBar()
        composeTestRule.navigateToCategoriesFromBottomNavBar()

        navController.assertCurrentRouteName(StatementAddingDestination.routeWithArgs)
        navController.assertRouteInBackStack(StatementsDestination.routeWithArgs)
        //navController.assertBackStackDepth(5)
    }
}