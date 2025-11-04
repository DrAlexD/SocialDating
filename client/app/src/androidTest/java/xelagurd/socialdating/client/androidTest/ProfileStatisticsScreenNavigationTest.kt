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
import xelagurd.socialdating.client.performNavigateUp
import xelagurd.socialdating.client.ui.navigation.AppNavHost
import xelagurd.socialdating.client.ui.navigation.ProfileDestination
import xelagurd.socialdating.client.ui.navigation.ProfileStatisticsDestination

@HiltAndroidTest
class ProfileStatisticsScreenNavigationTest {
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
    fun appNavHost_clickBack_navigatesToProfile() {
        composeTestRule.loginAndNavigateToCategories()
        composeTestRule.navigateToProfileStatistics()
        composeTestRule.performNavigateUp()

        navController.assertCurrentRouteName(ProfileDestination.routeWithArgs)
        //navController.assertBackStackDepth(?)
    }

    @Test
    fun appNavHost_navigateToProfile_stayOnProfileStatisticsScreen() {
        composeTestRule.loginAndNavigateToCategories()
        composeTestRule.navigateToProfileStatistics()
        val previousRoute = navController.getCurrentRoute()
        composeTestRule.navigateToProfileFromBottomNavBar()
        val currentRoute = navController.getCurrentRoute()

        assertEquals(previousRoute, currentRoute)
        //navController.assertBackStackDepth(?)
    }

    @Test
    fun appNavHost_navigateToCategoriesAndBack_navigatesToProfileStatisticsScreen() {
        composeTestRule.loginAndNavigateToCategories()
        composeTestRule.navigateToProfileStatistics()
        composeTestRule.navigateToCategoriesFromBottomNavBar()
        composeTestRule.navigateToProfileFromBottomNavBar()

        navController.assertCurrentRouteName(ProfileStatisticsDestination.routeWithArgs)
        //navController.assertBackStackDepth(?)
    }

    @Test
    fun appNavHost_navigateToSettingsAndBack_navigatesToProfileStatisticsScreen() {
        composeTestRule.loginAndNavigateToCategories()
        composeTestRule.navigateToProfileStatistics()
        composeTestRule.navigateToSettingsFromBottomNavBar()
        composeTestRule.navigateToProfileFromBottomNavBar()

        navController.assertCurrentRouteName(ProfileStatisticsDestination.routeWithArgs)
        //navController.assertBackStackDepth(?)
    }
}