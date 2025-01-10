package xelagurd.socialdating.tests

import androidx.activity.compose.setContent
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsNotSelected
import androidx.compose.ui.test.assertIsSelected
import androidx.compose.ui.test.isDisplayed
import androidx.compose.ui.test.isNotDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
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
import xelagurd.socialdating.getCurrentRoute
import xelagurd.socialdating.onNodeWithContentDescriptionId
import xelagurd.socialdating.onNodeWithTagId
import xelagurd.socialdating.onNodeWithTextId
import xelagurd.socialdating.ui.navigation.AppNavHost
import xelagurd.socialdating.ui.navigation.CategoriesDestination
import xelagurd.socialdating.ui.navigation.LoginDestination
import xelagurd.socialdating.ui.navigation.ProfileDestination
import xelagurd.socialdating.ui.navigation.ProfileStatisticsDestination
import xelagurd.socialdating.ui.navigation.RegistrationDestination
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
        navController.assertCurrentRouteName(LoginDestination.route)
        //navController.assertBackStackDepth(?)
    }

    @Test
    fun appNavHost_clickRegistration_navigatesToRegistration() {
        navigateToRegistration()

        navController.assertCurrentRouteName(RegistrationDestination.route)
        //navController.assertBackStackDepth(?)
    }

    @Test
    fun appNavHost_clickBackOnRegistrationScreen_navigatesToLogin() {
        navigateToRegistration()
        performNavigateUp()

        navController.assertCurrentRouteName(LoginDestination.route)
        //navController.assertBackStackDepth(?)
    }

    @Test
    fun appNavHost_performRegistration_navigatesToCategories() {
        navigateToRegistration()
        registerAndNavigateToCategories()

        navController.assertCurrentRouteName(CategoriesDestination.route)
        //navController.assertBackStackDepth(?)
    }

    @Test
    fun appNavHost_performLogin_navigatesToCategories() {
        loginAndNavigateToCategories()

        navController.assertCurrentRouteName(CategoriesDestination.route)
        //navController.assertBackStackDepth(?)
    }

    @Test
    fun appNavHost_clickCategory_navigatesToStatements() {
        navigateToStatements()

        navController.assertCurrentRouteName(StatementsDestination.routeWithArgs)
        //navController.assertBackStackDepth(?)
    }

    @Test
    fun appNavHost_clickStatement_navigatesToStatementDetails() {
        navigateToStatementDetails()

        navController.assertCurrentRouteName(StatementDetailsDestination.routeWithArgs)
        //navController.assertBackStackDepth(?)
    }

    @Test
    fun appNavHost_clickBackOnStatementsScreen_navigatesToCategories() {
        navigateToStatements()
        performNavigateUp()

        navController.assertCurrentRouteName(CategoriesDestination.route)
        //navController.assertBackStackDepth(?)
    }

    @Test
    fun appNavHost_navigateToCategoriesOnCategoriesScreen_stayOnCategoriesScreen() {
        loginAndNavigateToCategories()
        val previousRoute = navController.getCurrentRoute()
        navigateToCategoriesFromBottomNavBar()
        val currentRoute = navController.getCurrentRoute()

        assertEquals(previousRoute, currentRoute)
        //navController.assertBackStackDepth(?)
    }

    @Test
    fun appNavHost_navigateToCategoriesOnStatementsScreen_stayOnStatementsScreen() {
        navigateToStatements()
        val previousRoute = navController.getCurrentRoute()
        navigateToCategoriesFromBottomNavBar()
        val currentRoute = navController.getCurrentRoute()

        assertEquals(previousRoute, currentRoute)
        //navController.assertBackStackDepth(?)
    }

    @Test
    fun appNavHost_navigateToProfileOnCategoriesScreen_navigatesToProfile() {
        loginAndNavigateToCategories()
        navigateToProfileFromBottomNavBar()

        navController.assertCurrentRouteName(ProfileDestination.routeWithArgs)
        //navController.assertBackStackDepth(?)
    }

    @Test
    fun appNavHost_navigateToProfileOnProfileScreen_stayOnProfileScreen() {
        loginAndNavigateToCategories()
        navigateToProfileFromBottomNavBar()
        val previousRoute = navController.getCurrentRoute()
        navigateToProfileFromBottomNavBar()
        val currentRoute = navController.getCurrentRoute()

        assertEquals(previousRoute, currentRoute)
        //navController.assertBackStackDepth(?)
    }

    @Test
    fun appNavHost_navigateToProfileOnStatementsScreenAndBack_navigatesToStatementsScreen() {
        navigateToStatements()
        navigateToProfileFromBottomNavBar()
        navigateToCategoriesFromBottomNavBar()

        navController.assertCurrentRouteName(StatementsDestination.routeWithArgs)
        //navController.assertBackStackDepth(?)
    }

    @Test
    fun appNavHost_clickProfileStatistics_navigatesToProfileStatistics() {
        loginAndNavigateToCategories()
        navigateToProfileStatistics()

        navController.assertCurrentRouteName(ProfileStatisticsDestination.routeWithArgs)
        //navController.assertBackStackDepth(?)
    }

    @Test
    fun appNavHost_clickBackOnProfileStatisticsScreen_navigatesToProfile() {
        loginAndNavigateToCategories()
        navigateToProfileStatistics()
        performNavigateUp()

        navController.assertCurrentRouteName(ProfileDestination.routeWithArgs)
        //navController.assertBackStackDepth(?)
    }

    @Test
    fun appNavHost_navigateToProfileStatisticsOnProfileStatisticsScreen_stayOnProfileStatisticsScreen() {
        loginAndNavigateToCategories()
        navigateToProfileStatistics()
        val previousRoute = navController.getCurrentRoute()
        navigateToProfileFromBottomNavBar()
        val currentRoute = navController.getCurrentRoute()

        assertEquals(previousRoute, currentRoute)
        //navController.assertBackStackDepth(?)
    }

    @Test
    fun appNavHost_navigateToCategoriesOnProfileStatisticsScreenAndBack_navigatesToProfileStatisticsScreen() {
        loginAndNavigateToCategories()
        navigateToProfileStatistics()
        navigateToCategoriesFromBottomNavBar()
        navigateToProfileFromBottomNavBar()

        navController.assertCurrentRouteName(ProfileStatisticsDestination.routeWithArgs)
        //navController.assertBackStackDepth(?)
    }

    private fun navigateToRegistration() {
        composeTestRule.onNodeWithTextId(R.string.register)
            .performClick()
    }

    private fun loginAndNavigateToCategories() {
        composeTestRule.onNodeWithTextId(R.string.username)
            .performTextInput("username")
        composeTestRule.onNodeWithTextId(R.string.password)
            .performTextInput("password")

        composeTestRule.onNodeWithTextId(R.string.login)
            .performClick()
        composeTestRule.waitUntil(TIMEOUT_MILLIS) {
            composeTestRule.onNodeWithTextId(R.string.login).isNotDisplayed()
        }

        checkBottomNavBarWithCategoriesTopLevel()
    }

    private fun registerAndNavigateToCategories() {
        composeTestRule.onNodeWithTextId(R.string.username)
            .performTextInput("username")
        composeTestRule.onNodeWithTextId(R.string.name)
            .performTextInput("name")
        composeTestRule.onNodeWithTagId(R.string.male)
            .performClick()
        composeTestRule.onNodeWithTextId(R.string.age)
            .performTextInput("20")
        composeTestRule.onNodeWithTextId(R.string.city)
            .performTextInput("Moscow")
        composeTestRule.onNodeWithTagId(R.string.all_at_once)
            .performClick()
        composeTestRule.onNodeWithTextId(R.string.password)
            .performTextInput("1234")
        composeTestRule.onNodeWithTextId(R.string.repeat_password)
            .performTextInput("1234")

        composeTestRule.onNodeWithTextId(R.string.register)
            .performClick()
        composeTestRule.waitUntil(20_000L) {
            composeTestRule.onNodeWithTextId(R.string.register).isNotDisplayed()
        }

        checkBottomNavBarWithCategoriesTopLevel()
    }

    private fun navigateToStatements() {
        loginAndNavigateToCategories()

        composeTestRule.waitUntil(TIMEOUT_MILLIS) {
            composeTestRule.onNodeWithText(FakeDataSource.categories[0].name).isDisplayed()
        }

        composeTestRule.onNodeWithText(FakeDataSource.categories[0].name)
            .performClick()
        checkBottomNavBarWithCategoriesTopLevel()
    }

    private fun navigateToStatementDetails() {
        navigateToStatements()

        composeTestRule.waitUntil(TIMEOUT_MILLIS) {
            composeTestRule.onNodeWithText(FakeDataSource.statements[0].text).isDisplayed()
        }

        composeTestRule.onNodeWithText(FakeDataSource.statements[0].text)
            .performClick()
        //checkBottomNavBarWithCategoriesTopLevel()
    }

    private fun navigateToCategoriesFromBottomNavBar() {
        composeTestRule.onNodeWithTagId(R.string.nav_categories)
            .performClick()
        checkBottomNavBarWithCategoriesTopLevel()
    }

    private fun navigateToProfileFromBottomNavBar() {
        composeTestRule.onNodeWithTagId(R.string.nav_profile)
            .performClick()
        checkBottomNavBarWithProfileTopLevel()
    }

    private fun navigateToProfileStatistics() {
        navigateToProfileFromBottomNavBar()

        composeTestRule.waitUntil(TIMEOUT_MILLIS) {
            composeTestRule.onNodeWithTextId(R.string.open_profile_statistics).isDisplayed()
        }

        composeTestRule.onNodeWithTextId(R.string.open_profile_statistics)
            .performClick()
        checkBottomNavBarWithProfileTopLevel()
    }

    private fun performNavigateUp() {
        composeTestRule.onNodeWithContentDescriptionId(R.string.back_button).performClick()
    }

    private fun checkBottomNavBarWithCategoriesTopLevel() {
        composeTestRule.waitUntil(20_000L) {
            composeTestRule.onNodeWithTagId(R.string.nav_profile).isDisplayed()
        }
        composeTestRule.onNodeWithTagId(R.string.nav_profile).assertIsDisplayed()
        composeTestRule.onNodeWithTagId(R.string.nav_profile).assertIsNotSelected()

        composeTestRule.onNodeWithTagId(R.string.nav_categories).assertIsDisplayed()
        composeTestRule.onNodeWithTagId(R.string.nav_categories).assertIsSelected()
    }

    private fun checkBottomNavBarWithProfileTopLevel() {
        composeTestRule.onNodeWithTagId(R.string.nav_categories).assertIsDisplayed()
        composeTestRule.onNodeWithTagId(R.string.nav_categories).assertIsNotSelected()

        composeTestRule.onNodeWithTagId(R.string.nav_profile).assertIsDisplayed()
        // FixMe: composeTestRule.onNodeWithTagId(R.string.nav_profile).assertIsSelected()
    }

    companion object {
        private const val TIMEOUT_MILLIS = 3_000L // FixMe: remove after implementing server
    }
}