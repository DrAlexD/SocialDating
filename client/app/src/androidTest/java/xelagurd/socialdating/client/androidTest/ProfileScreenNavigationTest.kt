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
import xelagurd.socialdating.client.AndroidNavigationTestUtils.setContentToAppNavHost
import xelagurd.socialdating.client.AndroidTestUtils.assertBackStackDepth
import xelagurd.socialdating.client.AndroidTestUtils.assertCurrentRouteName
import xelagurd.socialdating.client.AndroidTestUtils.assertRouteInBackStack
import xelagurd.socialdating.client.AndroidTestUtils.assertRouteNotInBackStack
import xelagurd.socialdating.client.AndroidTestUtils.getCurrentRoute
import xelagurd.socialdating.client.HiltTestActivity
import xelagurd.socialdating.client.ui.navigation.CategoriesDestination
import xelagurd.socialdating.client.ui.navigation.ProfileDestination
import xelagurd.socialdating.client.ui.navigation.ProfileStatisticsDestination
import xelagurd.socialdating.client.ui.navigation.SettingsDestination
import xelagurd.socialdating.client.ui.navigation.SimilarUsersDestination

@HiltAndroidTest
class ProfileScreenNavigationTest {
    @get:Rule(order = 0)
    val hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 1)
    val composeTestRule = createAndroidComposeRule<HiltTestActivity>()

    private lateinit var navController: TestNavHostController

    @Before
    fun setup() {
        hiltRule.inject()

        navController = composeTestRule.setContentToAppNavHost()
    }

    @Test
    fun appNavHost_clickProfileStatistics_navigatesToProfileStatisticsScreenWithProfileInBackStack() {
        composeTestRule.loginAndNavigateToCategories()
        composeTestRule.navigateToProfileStatistics()

        navController.assertCurrentRouteName(ProfileStatisticsDestination.routeWithArgs)
        navController.assertRouteInBackStack(ProfileDestination.routeWithArgs)
        navController.assertBackStackDepth(4)
    }

    @Test
    fun appNavHost_navigateToProfile_stayOnProfileScreen() {
        composeTestRule.loginAndNavigateToCategories()
        composeTestRule.navigateToProfileFromBottomNavBar()
        val previousRoute = navController.getCurrentRoute()
        composeTestRule.navigateToProfileFromBottomNavBar()
        val currentRoute = navController.getCurrentRoute()

        assertEquals(previousRoute, currentRoute)
        navController.assertBackStackDepth(3)
    }

    @Test
    fun appNavHost_navigateToCategories_navigatesToCategoriesScreenWithoutProfile() {
        composeTestRule.loginAndNavigateToCategories()
        composeTestRule.navigateToProfileFromBottomNavBar()
        composeTestRule.navigateToCategoriesFromBottomNavBar()

        navController.assertCurrentRouteName(CategoriesDestination.route)
        navController.assertRouteNotInBackStack(ProfileDestination.routeWithArgs)
        navController.assertBackStackDepth(2)
    }

    @Test
    fun appNavHost_navigateToSimilarUsers_navigatesToSimilarUsersScreenWithoutProfile() {
        composeTestRule.loginAndNavigateToCategories()
        composeTestRule.navigateToProfileFromBottomNavBar()
        composeTestRule.navigateToSimilarUsersFromBottomNavBar()

        navController.assertCurrentRouteName(SimilarUsersDestination.routeWithArgs)
        navController.assertRouteNotInBackStack(ProfileDestination.routeWithArgs)
        navController.assertBackStackDepth(3)
    }

    @Test
    fun appNavHost_navigateToSettings_navigatesToSettingsScreenWithoutProfile() {
        composeTestRule.loginAndNavigateToCategories()
        composeTestRule.navigateToProfileFromBottomNavBar()
        composeTestRule.navigateToSettingsFromBottomNavBar()

        navController.assertCurrentRouteName(SettingsDestination.route)
        navController.assertRouteNotInBackStack(ProfileDestination.routeWithArgs)
        navController.assertBackStackDepth(3)
    }
}