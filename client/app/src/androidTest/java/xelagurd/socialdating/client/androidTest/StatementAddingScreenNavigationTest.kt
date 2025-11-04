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
import xelagurd.socialdating.client.addStatementAndNavigateToStatementsScreen
import xelagurd.socialdating.client.assertCurrentRouteName
import xelagurd.socialdating.client.getCurrentRoute
import xelagurd.socialdating.client.navigateToCategoriesFromBottomNavBar
import xelagurd.socialdating.client.navigateToProfileFromBottomNavBar
import xelagurd.socialdating.client.navigateToSettingsFromBottomNavBar
import xelagurd.socialdating.client.navigateToStatementAdding
import xelagurd.socialdating.client.performNavigateUp
import xelagurd.socialdating.client.ui.navigation.AppNavHost
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
    fun appNavHost_performStatementAdding_navigatesToStatements() {
        composeTestRule.navigateToStatementAdding()
        composeTestRule.addStatementAndNavigateToStatementsScreen()

        navController.assertCurrentRouteName(StatementsDestination.routeWithArgs)
        //navController.assertBackStackDepth(?)
    }

    @Test
    fun appNavHost_clickBack_navigatesToStatements() {
        composeTestRule.navigateToStatementAdding()
        composeTestRule.performNavigateUp()

        navController.assertCurrentRouteName(StatementsDestination.routeWithArgs)
        //navController.assertBackStackDepth(?)
    }

    @Test
    fun appNavHost_navigateToCategories_stayOnStatementAddingScreen() {
        composeTestRule.navigateToStatementAdding()
        val previousRoute = navController.getCurrentRoute()
        composeTestRule.navigateToCategoriesFromBottomNavBar()
        val currentRoute = navController.getCurrentRoute()

        assertEquals(previousRoute, currentRoute)
        //navController.assertBackStackDepth(?)
    }

    @Test
    fun appNavHost_navigateToProfileAndBack_navigatesToStatementAddingScreen() {
        composeTestRule.navigateToStatementAdding()
        composeTestRule.navigateToProfileFromBottomNavBar()
        composeTestRule.navigateToCategoriesFromBottomNavBar()

        navController.assertCurrentRouteName(StatementAddingDestination.routeWithArgs)
        //navController.assertBackStackDepth(?)
    }

    @Test
    fun appNavHost_navigateToSettingsAndBack_navigatesToStatementAddingScreen() {
        composeTestRule.navigateToStatementAdding()
        composeTestRule.navigateToSettingsFromBottomNavBar()
        composeTestRule.navigateToCategoriesFromBottomNavBar()

        navController.assertCurrentRouteName(StatementAddingDestination.routeWithArgs)
        //navController.assertBackStackDepth(?)
    }
}