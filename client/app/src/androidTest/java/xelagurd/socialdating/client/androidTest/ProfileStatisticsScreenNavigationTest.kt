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
import xelagurd.socialdating.client.AndroidNavigationTestUtils.navigateToProfileStatistics
import xelagurd.socialdating.client.AndroidNavigationTestUtils.navigateToSettingsFromBottomNavBar
import xelagurd.socialdating.client.AndroidNavigationTestUtils.navigateToSimilarUsersFromBottomNavBar
import xelagurd.socialdating.client.AndroidNavigationTestUtils.performNavigateUp
import xelagurd.socialdating.client.AndroidNavigationTestUtils.setContentToAppNavHost
import xelagurd.socialdating.client.AndroidTestUtils.assertBackStackDepth
import xelagurd.socialdating.client.AndroidTestUtils.assertCurrentRouteName
import xelagurd.socialdating.client.AndroidTestUtils.assertRouteInBackStack
import xelagurd.socialdating.client.AndroidTestUtils.assertRouteNotInBackStack
import xelagurd.socialdating.client.AndroidTestUtils.getCurrentRoute
import xelagurd.socialdating.client.MainActivity
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

        navController = composeTestRule.setContentToAppNavHost()
    }

    @Test
    fun appNavHost_clickBack_navigatesToProfileScreen() {
        composeTestRule.loginAndNavigateToCategories()
        composeTestRule.navigateToProfileStatistics()
        composeTestRule.performNavigateUp()

        navController.assertCurrentRouteName(ProfileDestination.routeWithArgs)
        navController.assertRouteNotInBackStack(ProfileStatisticsDestination.routeWithArgs)
        //navController.assertBackStackDepth(4)
    }

    @Test
    fun appNavHost_navigateToProfile_stayOnProfileStatisticsScreen() {
        composeTestRule.loginAndNavigateToCategories()
        composeTestRule.navigateToProfileStatistics()
        val previousRoute = navController.getCurrentRoute()
        composeTestRule.navigateToProfileFromBottomNavBar()
        val currentRoute = navController.getCurrentRoute()

        assertEquals(previousRoute, currentRoute)
        //navController.assertBackStackDepth(5)
    }

    @Test
    fun appNavHost_navigateToCategoriesAndBack_restoredProfileStatisticsScreenWithProfile() {
        composeTestRule.loginAndNavigateToCategories()
        composeTestRule.navigateToProfileStatistics()
        composeTestRule.navigateToCategoriesFromBottomNavBar()
        composeTestRule.navigateToProfileFromBottomNavBar()

        navController.assertCurrentRouteName(ProfileStatisticsDestination.routeWithArgs)
        navController.assertRouteInBackStack(ProfileDestination.routeWithArgs)
        //navController.assertBackStackDepth(5)
    }

    @Test
    fun appNavHost_navigateToSimilarUsersAndBack_restoredProfileStatisticsScreenWithProfile() {
        composeTestRule.loginAndNavigateToCategories()
        composeTestRule.navigateToProfileStatistics()
        composeTestRule.navigateToSimilarUsersFromBottomNavBar()
        composeTestRule.navigateToProfileFromBottomNavBar()

        navController.assertCurrentRouteName(ProfileStatisticsDestination.routeWithArgs)
        navController.assertRouteInBackStack(ProfileDestination.routeWithArgs)
        //navController.assertBackStackDepth(5)
    }

    @Test
    fun appNavHost_navigateToSettingsAndBack_restoredProfileStatisticsScreenWithProfile() {
        composeTestRule.loginAndNavigateToCategories()
        composeTestRule.navigateToProfileStatistics()
        composeTestRule.navigateToSettingsFromBottomNavBar()
        composeTestRule.navigateToProfileFromBottomNavBar()

        navController.assertCurrentRouteName(ProfileStatisticsDestination.routeWithArgs)
        navController.assertRouteInBackStack(ProfileDestination.routeWithArgs)
        //navController.assertBackStackDepth(5)
    }
}