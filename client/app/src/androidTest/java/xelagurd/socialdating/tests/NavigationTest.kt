package xelagurd.socialdating.tests

import androidx.activity.compose.setContent
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsNotSelected
import androidx.compose.ui.test.assertIsSelected
import androidx.compose.ui.test.isDisplayed
import androidx.compose.ui.test.isNotDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performScrollTo
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
import xelagurd.socialdating.checkButtonAndClick
import xelagurd.socialdating.checkTextFieldAndInput
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
import xelagurd.socialdating.ui.navigation.SettingsDestination
import xelagurd.socialdating.ui.navigation.StatementAddingDestination
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
    fun appNavHost_navigateToCategoriesOnCategoriesScreen_stayOnCategoriesScreen() {
        loginAndNavigateToCategories()
        val previousRoute = navController.getCurrentRoute()
        navigateToCategoriesFromBottomNavBar()
        val currentRoute = navController.getCurrentRoute()

        assertEquals(previousRoute, currentRoute)
        //navController.assertBackStackDepth(?)
    }

    @Test
    fun appNavHost_clickCategory_navigatesToStatements() {
        navigateToStatements()

        navController.assertCurrentRouteName(StatementsDestination.routeWithArgs)
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
    fun appNavHost_navigateToCategoriesOnStatementsScreen_stayOnStatementsScreen() {
        navigateToStatements()
        val previousRoute = navController.getCurrentRoute()
        navigateToCategoriesFromBottomNavBar()
        val currentRoute = navController.getCurrentRoute()

        assertEquals(previousRoute, currentRoute)
        //navController.assertBackStackDepth(?)
    }

    @Test
    fun appNavHost_clickAddStatementOnStatementsScreen_navigatesToStatementAddingScreen() {
        navigateToStatementAdding()

        navController.assertCurrentRouteName(StatementAddingDestination.routeWithArgs)
        //navController.assertBackStackDepth(?)
    }

    @Test
    fun appNavHost_clickBackOnStatementAddingScreen_navigatesToStatements() {
        navigateToStatementAdding()
        performNavigateUp()

        navController.assertCurrentRouteName(StatementsDestination.routeWithArgs)
        //navController.assertBackStackDepth(?)
    }

    @Test
    fun appNavHost_performStatementAdding_navigatesToStatements() {
        navigateToStatementAdding()
        addStatementAndNavigateToStatementsScreen()

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

    @Test
    fun appNavHost_navigateToSettingsOnCategoriesScreen_navigatesToSettings() {
        loginAndNavigateToCategories()
        navigateToSettingsFromBottomNavBar()

        navController.assertCurrentRouteName(SettingsDestination.route)
        //navController.assertBackStackDepth(?)
    }

    @Test
    fun appNavHost_performLogout_navigatesToLoginScreen() {
        loginAndNavigateToCategories()
        navigateToSettingsFromBottomNavBar()
        logoutAndNavigateToLoginScreen()

        navController.assertCurrentRouteName(LoginDestination.route)
        //navController.assertBackStackDepth(?)
    }

    private fun loginAndNavigateToCategories() {
        composeTestRule.onNodeWithTextId(R.string.username).checkTextFieldAndInput("username")
        composeTestRule.onNodeWithTextId(R.string.password).checkTextFieldAndInput("password")

        composeTestRule.onNodeWithTextId(R.string.login).checkButtonAndClick()
        composeTestRule.waitUntil(TIMEOUT_MILLIS) {
            composeTestRule.onNodeWithTextId(R.string.login).isNotDisplayed()
        }

        checkBottomNavBarWithCategoriesTopLevel()
    }

    private fun navigateToRegistration() {
        composeTestRule.onNodeWithTextId(R.string.register).checkButtonAndClick()
    }

    private fun registerAndNavigateToCategories() {
        composeTestRule.onNodeWithTextId(R.string.username).checkTextFieldAndInput("username")
        composeTestRule.onNodeWithTextId(R.string.name).checkTextFieldAndInput("name")
        composeTestRule.onNodeWithTagId(R.string.male).checkButtonAndClick()
        composeTestRule.onNodeWithTextId(R.string.age).checkTextFieldAndInput("20")

        composeTestRule.onNodeWithTextId(R.string.register).performScrollTo()

        composeTestRule.onNodeWithTextId(R.string.city).checkTextFieldAndInput("Moscow")
        composeTestRule.onNodeWithTagId(R.string.all_at_once).checkButtonAndClick()
        composeTestRule.onNodeWithTextId(R.string.password).checkTextFieldAndInput("1234")
        composeTestRule.onNodeWithTextId(R.string.repeat_password).checkTextFieldAndInput("1234")

        composeTestRule.onNodeWithTextId(R.string.register).checkButtonAndClick()
        composeTestRule.waitUntil(TIMEOUT_MILLIS) {
            composeTestRule.onNodeWithTextId(R.string.register).isNotDisplayed()
        }

        checkBottomNavBarWithCategoriesTopLevel()
    }

    private fun logoutAndNavigateToLoginScreen() {
        composeTestRule.onNodeWithTextId(R.string.logout).checkButtonAndClick()
        composeTestRule.waitUntil(TIMEOUT_MILLIS) {
            composeTestRule.onNodeWithTextId(R.string.logout).isNotDisplayed()
        }
    }

    private fun navigateToStatements() {
        loginAndNavigateToCategories()

        composeTestRule.waitUntil(TIMEOUT_MILLIS) {
            composeTestRule.onNodeWithText(FakeDataSource.categories[0].name).isDisplayed()
        }

        composeTestRule.onNodeWithText(FakeDataSource.categories[0].name).checkButtonAndClick()
        checkBottomNavBarWithCategoriesTopLevel()
    }

    private fun navigateToStatementAdding() {
        navigateToStatements()

        composeTestRule.waitUntil(TIMEOUT_MILLIS) {
            composeTestRule.onNodeWithText(FakeDataSource.statements[0].text).isDisplayed()
        }

        composeTestRule.onNodeWithContentDescriptionId(R.string.add_statement).checkButtonAndClick()
        checkBottomNavBarWithCategoriesTopLevel()
    }

    private fun addStatementAndNavigateToStatementsScreen() {
        composeTestRule.onNodeWithTextId(R.string.statement_text).checkTextFieldAndInput("Statement text")
        composeTestRule.onNodeWithText(FakeDataSource.definingThemes[0].name).checkButtonAndClick()
        composeTestRule.onNodeWithTagId(R.string.yes).checkButtonAndClick()

        composeTestRule.onNodeWithTextId(R.string.add_statement).checkButtonAndClick()
        composeTestRule.waitUntil(TIMEOUT_MILLIS) {
            composeTestRule.onNodeWithTextId(R.string.add_statement).isNotDisplayed()
        }

        checkBottomNavBarWithCategoriesTopLevel()
    }

    private fun navigateToStatementDetails() {
        navigateToStatements()

        composeTestRule.waitUntil(TIMEOUT_MILLIS) {
            composeTestRule.onNodeWithText(FakeDataSource.statements[0].text).isDisplayed()
        }

        composeTestRule.onNodeWithText(FakeDataSource.statements[0].text).checkButtonAndClick()
        //checkBottomNavBarWithCategoriesTopLevel()
    }

    private fun navigateToProfileStatistics() {
        navigateToProfileFromBottomNavBar()

        composeTestRule.waitUntil(TIMEOUT_MILLIS) {
            composeTestRule.onNodeWithTextId(R.string.open_profile_statistics).isDisplayed()
        }

        composeTestRule.onNodeWithTextId(R.string.open_profile_statistics).checkButtonAndClick()
        checkBottomNavBarWithProfileTopLevel()
    }

    private fun navigateToCategoriesFromBottomNavBar() {
        composeTestRule.onNodeWithTagId(R.string.nav_categories).checkButtonAndClick()
        checkBottomNavBarWithCategoriesTopLevel()
    }

    private fun navigateToProfileFromBottomNavBar() {
        composeTestRule.onNodeWithTagId(R.string.nav_profile).checkButtonAndClick()
        checkBottomNavBarWithProfileTopLevel()
    }

    private fun navigateToSettingsFromBottomNavBar() {
        composeTestRule.onNodeWithTagId(R.string.nav_settings).checkButtonAndClick()
        checkBottomNavBarWithSettingsTopLevel()
    }

    private fun checkBottomNavBarWithCategoriesTopLevel() {
        composeTestRule.onNodeWithTagId(R.string.nav_profile).assertIsDisplayed()
        composeTestRule.onNodeWithTagId(R.string.nav_profile).assertIsNotSelected()

        composeTestRule.onNodeWithTagId(R.string.nav_settings).assertIsDisplayed()
        composeTestRule.onNodeWithTagId(R.string.nav_settings).assertIsNotSelected()

        composeTestRule.onNodeWithTagId(R.string.nav_categories).assertIsDisplayed()
        composeTestRule.onNodeWithTagId(R.string.nav_categories).assertIsSelected()
    }

    private fun checkBottomNavBarWithProfileTopLevel() {
        composeTestRule.onNodeWithTagId(R.string.nav_categories).assertIsDisplayed()
        composeTestRule.onNodeWithTagId(R.string.nav_categories).assertIsNotSelected()

        composeTestRule.onNodeWithTagId(R.string.nav_settings).assertIsDisplayed()
        composeTestRule.onNodeWithTagId(R.string.nav_settings).assertIsNotSelected()

        composeTestRule.onNodeWithTagId(R.string.nav_profile).assertIsDisplayed()
        // FixMe: composeTestRule.onNodeWithTagId(R.string.nav_profile).assertIsSelected()
    }

    private fun checkBottomNavBarWithSettingsTopLevel() {
        composeTestRule.onNodeWithTagId(R.string.nav_profile).assertIsDisplayed()
        composeTestRule.onNodeWithTagId(R.string.nav_profile).assertIsNotSelected()

        composeTestRule.onNodeWithTagId(R.string.nav_categories).assertIsDisplayed()
        composeTestRule.onNodeWithTagId(R.string.nav_categories).assertIsNotSelected()

        composeTestRule.onNodeWithTagId(R.string.nav_settings).assertIsDisplayed()
        composeTestRule.onNodeWithTagId(R.string.nav_settings).assertIsSelected()
    }

    private fun performNavigateUp() {
        composeTestRule.onNodeWithContentDescriptionId(R.string.back_button).checkButtonAndClick()
    }

    companion object {
        private const val TIMEOUT_MILLIS = 3_000L // FixMe: remove after implementing server
    }
}