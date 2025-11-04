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
import xelagurd.socialdating.client.navigateToProfileStatistics
import xelagurd.socialdating.client.navigateToSettingsFromBottomNavBar
import xelagurd.socialdating.client.ui.navigation.AppNavHost
import xelagurd.socialdating.client.ui.navigation.CategoriesDestination
import xelagurd.socialdating.client.ui.navigation.ProfileStatisticsDestination
import xelagurd.socialdating.client.ui.navigation.SettingsDestination

@HiltAndroidTest
class ProfileScreenNavigationTest {
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
    fun appNavHost_clickProfileStatistics_navigatesToProfileStatistics() {
        composeTestRule.loginAndNavigateToCategories()
        composeTestRule.navigateToProfileStatistics()

        navController.assertCurrentRouteName(ProfileStatisticsDestination.routeWithArgs)
        //navController.assertBackStackDepth(?)
    }

    @Test
    fun appNavHost_navigateToProfile_stayOnProfileScreen() {
        composeTestRule.loginAndNavigateToCategories()
        composeTestRule.navigateToProfileFromBottomNavBar()
        val previousRoute = navController.getCurrentRoute()
        composeTestRule.navigateToProfileFromBottomNavBar()
        val currentRoute = navController.getCurrentRoute()

        assertEquals(previousRoute, currentRoute)
        //navController.assertBackStackDepth(?)
    }

    @Test
    fun appNavHost_navigateToCategories_navigatesToCategoriesScreen() {
        composeTestRule.loginAndNavigateToCategories()
        composeTestRule.navigateToProfileFromBottomNavBar()
        composeTestRule.navigateToCategoriesFromBottomNavBar()

        navController.assertCurrentRouteName(CategoriesDestination.route)
        //navController.assertBackStackDepth(?)
    }

    @Test
    fun appNavHost_navigateToSettings_navigatesToSettingsScreen() {
        composeTestRule.loginAndNavigateToCategories()
        composeTestRule.navigateToProfileFromBottomNavBar()
        composeTestRule.navigateToSettingsFromBottomNavBar()

        navController.assertCurrentRouteName(SettingsDestination.route)
        //navController.assertBackStackDepth(?)
    }
}