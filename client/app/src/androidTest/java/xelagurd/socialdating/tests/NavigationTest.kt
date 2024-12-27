package xelagurd.socialdating.tests

import androidx.activity.compose.setContent
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsSelected
import androidx.compose.ui.test.isDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.navigation.compose.ComposeNavigator
import androidx.navigation.testing.TestNavHostController
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import xelagurd.socialdating.MainActivity
import xelagurd.socialdating.R
import xelagurd.socialdating.assertCurrentRouteName
import xelagurd.socialdating.data.fake.FakeDataSource
import xelagurd.socialdating.onNodeWithContentDescriptionId
import xelagurd.socialdating.onNodeWithTagId
import xelagurd.socialdating.ui.navigation.AppNavHost
import xelagurd.socialdating.ui.navigation.CategoriesDestination
import xelagurd.socialdating.ui.navigation.StatementDetailsDestination
import xelagurd.socialdating.ui.navigation.StatementsDestination

@HiltAndroidTest
class NavigationTest {
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
    fun appNavHost_verifyStartScreen() {
        navController.assertCurrentRouteName(CategoriesDestination.route)
        //navController.assertBackStackDepth(1)
    }

    @Test
    fun appNavHost_clickCategory_navigatesToStatements() {
        navigateFromCategoriesToStatements()

        navController.assertCurrentRouteName(StatementsDestination.routeWithArgs)
        //navController.assertBackStackDepth(2)
    }

    @Test
    fun appNavHost_clickStatement_navigatesToStatementDetails() {
        navigateFromCategoriesToStatements()
        navigateFromStatementsToStatementDetails()

        navController.assertCurrentRouteName(StatementDetailsDestination.routeWithArgs)
        //navController.assertBackStackDepth(3)
    }

    @Test
    fun appNavHost_clickBackOnStatementsScreen_navigatesToCategories() {
        navigateFromCategoriesToStatements()
        performNavigateUp()

        navController.assertCurrentRouteName(CategoriesDestination.route)
        //navController.assertBackStackDepth(1)
    }

    /*@Test
    fun appNavHost_clickBackOnStatementDetailsScreen_navigatesToStatements() {
        navigateFromCategoriesToStatements()
        navigateFromStatementsToStatementDetails()
        performNavigateUp()

        navController.assertCurrentRouteName(StatementsDestination.routeWithArgs)
        navController.assertBackStackDepth(2)
    }*/

    @Test
    fun appNavHost_navigateToCategoriesScreenOnCategoriesScreen_stayOnCategoriesScreen() {
        checkBottomNavToCategoriesWithCategoriesTopLevel()

        val previousRoute = navController.currentBackStackEntry?.destination?.route

        navigateToCategoriesFromBottomNavBar()
        checkBottomNavToCategoriesWithCategoriesTopLevel()

        val currentRoute = navController.currentBackStackEntry?.destination?.route

        assertEquals(previousRoute, currentRoute)
        //navController.assertBackStackDepth(1)
    }

    @Test
    fun appNavHost_navigateToCategoriesScreenOnStatementsScreen_stayOnStatementsScreen() {
        navigateFromCategoriesToStatements()
        checkBottomNavToCategoriesWithCategoriesTopLevel()

        val previousRoute = navController.currentBackStackEntry?.destination?.route

        navigateToCategoriesFromBottomNavBar()
        checkBottomNavToCategoriesWithCategoriesTopLevel()

        val currentRoute = navController.currentBackStackEntry?.destination?.route

        assertEquals(previousRoute, currentRoute)
        //navController.assertBackStackDepth(2)
    }

    /*@Test
    fun appNavHost_navigateToCategoriesScreenOnStatementDetailsScreen_stayOnStatementDetailsScreen() {
        navigateFromCategoriesToStatements()
        navigateFromStatementsToStatementDetails()
        checkBottomNavToCategoriesWithCategoriesTopLevel()

        val previousRoute = navController.currentBackStackEntry?.destination?.route

        navigateToCategoriesFromBottomNavBar()
        checkBottomNavToCategoriesWithCategoriesTopLevel()

        val currentRoute = navController.currentBackStackEntry?.destination?.route

        assertEquals(previousRoute, currentRoute)
        navController.assertBackStackDepth(3)
    }*/

    private fun navigateFromCategoriesToStatements() {
        composeTestRule.waitUntil(TIMEOUT_MILLIS) {
            composeTestRule.onNodeWithText(FakeDataSource.categories[0].name).isDisplayed()
        }

        composeTestRule.onNodeWithText(FakeDataSource.categories[0].name)
            .performClick()
    }

    private fun navigateFromStatementsToStatementDetails() {
        composeTestRule.waitUntil(TIMEOUT_MILLIS) {
            composeTestRule.onNodeWithText(FakeDataSource.statements[0].text).isDisplayed()
        }

        composeTestRule.onNodeWithText(FakeDataSource.statements[0].text)
            .performClick()
    }

    private fun checkBottomNavToCategoriesWithCategoriesTopLevel() {
        composeTestRule.onNodeWithTagId(R.string.nav_categories).assertIsDisplayed()
        composeTestRule.onNodeWithTagId(R.string.nav_categories).assertIsSelected()
    }

    private fun navigateToCategoriesFromBottomNavBar() {
        composeTestRule.onNodeWithTagId(R.string.nav_categories)
            .performClick()
    }

    private fun performNavigateUp() {
        composeTestRule.onNodeWithContentDescriptionId(R.string.back_button).performClick()
    }

    companion object {
        private const val TIMEOUT_MILLIS = 5_000L // FixMe: remove after implementing server
    }
}