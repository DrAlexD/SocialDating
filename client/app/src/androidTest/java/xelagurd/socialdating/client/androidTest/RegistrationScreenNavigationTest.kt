package xelagurd.socialdating.client.androidTest

import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.navigation.testing.TestNavHostController
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import xelagurd.socialdating.client.AndroidNavigationTestUtils.navigateToRegistration
import xelagurd.socialdating.client.AndroidNavigationTestUtils.performNavigateUp
import xelagurd.socialdating.client.AndroidNavigationTestUtils.registerAndNavigateToCategories
import xelagurd.socialdating.client.AndroidNavigationTestUtils.setContentToAppNavHost
import xelagurd.socialdating.client.AndroidTestUtils.assertBackStackDepth
import xelagurd.socialdating.client.AndroidTestUtils.assertCurrentRouteName
import xelagurd.socialdating.client.AndroidTestUtils.assertRouteNotInBackStack
import xelagurd.socialdating.client.MainActivity
import xelagurd.socialdating.client.ui.navigation.CategoriesDestination
import xelagurd.socialdating.client.ui.navigation.LoginDestination
import xelagurd.socialdating.client.ui.navigation.RegistrationDestination

@HiltAndroidTest
class RegistrationScreenNavigationTest {
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
    fun appNavHost_performRegistration_navigatesToCategoriesScreenWithoutRegistration() {
        composeTestRule.navigateToRegistration()
        composeTestRule.registerAndNavigateToCategories()

        navController.assertCurrentRouteName(CategoriesDestination.route)
        navController.assertRouteNotInBackStack(RegistrationDestination.route)
        //navController.assertBackStackDepth(3)
    }

    @Test
    fun appNavHost_clickBack_navigatesToLoginScreen() {
        composeTestRule.navigateToRegistration()
        composeTestRule.performNavigateUp()

        navController.assertCurrentRouteName(LoginDestination.route)
        navController.assertRouteNotInBackStack(RegistrationDestination.route)
        //navController.assertBackStackDepth(2)
    }
}