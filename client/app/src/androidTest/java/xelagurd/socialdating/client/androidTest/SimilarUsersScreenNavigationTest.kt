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
import xelagurd.socialdating.client.AndroidNavigationTestUtils.navigateToSettingsFromBottomNavBar
import xelagurd.socialdating.client.AndroidNavigationTestUtils.navigateToSimilarUserProfileStatistics
import xelagurd.socialdating.client.AndroidNavigationTestUtils.navigateToSimilarUsersFromBottomNavBar
import xelagurd.socialdating.client.AndroidNavigationTestUtils.setContentToAppNavHost
import xelagurd.socialdating.client.AndroidTestUtils.assertBackStackDepth
import xelagurd.socialdating.client.AndroidTestUtils.assertCurrentRouteName
import xelagurd.socialdating.client.AndroidTestUtils.assertRouteInBackStack
import xelagurd.socialdating.client.AndroidTestUtils.assertRouteNotInBackStack
import xelagurd.socialdating.client.AndroidTestUtils.getCurrentRoute
import xelagurd.socialdating.client.MainActivity
import xelagurd.socialdating.client.ui.navigation.CategoriesDestination
import xelagurd.socialdating.client.ui.navigation.ProfileDestination
import xelagurd.socialdating.client.ui.navigation.ProfileStatisticsDestination
import xelagurd.socialdating.client.ui.navigation.SettingsDestination
import xelagurd.socialdating.client.ui.navigation.SimilarUsersDestination

@HiltAndroidTest
class SimilarUsersScreenNavigationTest {
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
    fun appNavHost_clickSimilarUser_navigatesToProfileStatisticsScreenWithSimilarUsersInBackStack() {
        composeTestRule.loginAndNavigateToCategories()
        composeTestRule.navigateToSimilarUserProfileStatistics()

        navController.assertCurrentRouteName(ProfileStatisticsDestination.routeWithArgs)
        navController.assertRouteInBackStack(SimilarUsersDestination.routeWithArgs)
        //navController.assertBackStackDepth(5)
    }

    @Test
    fun appNavHost_navigateToSimilarUsers_stayOnSimilarUsersScreen() {
        composeTestRule.loginAndNavigateToCategories()
        composeTestRule.navigateToSimilarUsersFromBottomNavBar()
        val previousRoute = navController.getCurrentRoute()
        composeTestRule.navigateToSimilarUsersFromBottomNavBar()
        val currentRoute = navController.getCurrentRoute()

        assertEquals(previousRoute, currentRoute)
        //navController.assertBackStackDepth(4)
    }

    @Test
    fun appNavHost_navigateToCategories_navigatesToCategoriesScreenWithoutSimilarUsers() {
        composeTestRule.loginAndNavigateToCategories()
        composeTestRule.navigateToSimilarUsersFromBottomNavBar()
        composeTestRule.navigateToCategoriesFromBottomNavBar()

        navController.assertCurrentRouteName(CategoriesDestination.route)
        navController.assertRouteNotInBackStack(SimilarUsersDestination.routeWithArgs)
        //navController.assertBackStackDepth(3)
    }

    @Test
    fun appNavHost_navigateToProfile_navigatesToProfileScreenWithoutSimilarUsers() {
        composeTestRule.loginAndNavigateToCategories()
        composeTestRule.navigateToSimilarUsersFromBottomNavBar()
        composeTestRule.navigateToProfileFromBottomNavBar()

        navController.assertCurrentRouteName(ProfileDestination.routeWithArgs)
        navController.assertRouteNotInBackStack(SimilarUsersDestination.routeWithArgs)
        //navController.assertBackStackDepth(4)
    }

    @Test
    fun appNavHost_navigateToSettings_navigatesToSettingsScreenWithoutSimilarUsers() {
        composeTestRule.loginAndNavigateToCategories()
        composeTestRule.navigateToSimilarUsersFromBottomNavBar()
        composeTestRule.navigateToSettingsFromBottomNavBar()

        navController.assertCurrentRouteName(SettingsDestination.route)
        navController.assertRouteNotInBackStack(SimilarUsersDestination.routeWithArgs)
        //navController.assertBackStackDepth(4)
    }
}