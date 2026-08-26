package xelagurd.socialdating.client.androidTest

import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.navigation.testing.TestNavHostController
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import xelagurd.socialdating.client.AndroidNavigationTestUtils.loginAndNavigateToCategories
import xelagurd.socialdating.client.AndroidNavigationTestUtils.navigateToRegistration
import xelagurd.socialdating.client.AndroidNavigationTestUtils.setContentToAppNavHost
import xelagurd.socialdating.client.AndroidTestUtils.assertBackStackDepth
import xelagurd.socialdating.client.AndroidTestUtils.assertCurrentRouteName
import xelagurd.socialdating.client.AndroidTestUtils.assertRouteInBackStack
import xelagurd.socialdating.client.AndroidTestUtils.assertRouteNotInBackStack
import xelagurd.socialdating.client.MainActivity
import xelagurd.socialdating.client.data.PreferencesRepository.Defaults.CURRENT_USER_ID_DEFAULT
import xelagurd.socialdating.client.data.fake.FakeData.TEST_TIMEOUT_MILLIS
import xelagurd.socialdating.client.ui.navigation.CategoriesDestination
import xelagurd.socialdating.client.ui.navigation.LoginDestination
import xelagurd.socialdating.client.ui.navigation.ProfileDestination
import xelagurd.socialdating.client.ui.navigation.RegistrationDestination

@HiltAndroidTest
class LoginScreenNavigationTest {
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
    fun appNavHost_withoutLoggedInUser_startScreenIsLogin() {
        navController.assertCurrentRouteName(LoginDestination.route)
        navController.assertRouteNotInBackStack(CategoriesDestination.route)
        //navController.assertBackStackDepth(2)
    }

    @Test
    fun appNavHost_loggedInUser_startScreenIsCategories() {
        composeTestRule.loginAndNavigateToCategories()
        composeTestRule.waitUntil(TEST_TIMEOUT_MILLIS) {
            ProfileDestination.currentUserId != CURRENT_USER_ID_DEFAULT
        }

        navController = composeTestRule.setContentToAppNavHost()

        navController.assertCurrentRouteName(CategoriesDestination.route)
        navController.assertRouteNotInBackStack(LoginDestination.route)
        //navController.assertBackStackDepth(2)
    }

    @Test
    fun appNavHost_performLogin_navigatesToCategoriesScreenWithoutLogin() {
        composeTestRule.loginAndNavigateToCategories()

        navController.assertCurrentRouteName(CategoriesDestination.route)
        navController.assertRouteNotInBackStack(LoginDestination.route)
        //navController.assertBackStackDepth(3)
    }

    @Test
    fun appNavHost_clickRegistration_navigatesToRegistrationScreenWithLoginInBackStack() {
        composeTestRule.navigateToRegistration()

        navController.assertCurrentRouteName(RegistrationDestination.route)
        navController.assertRouteInBackStack(LoginDestination.route)
        //navController.assertBackStackDepth(3)
    }
}