package xelagurd.socialdating.client.androidTest

import androidx.activity.compose.setContent
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.navigation.compose.ComposeNavigator
import androidx.navigation.testing.TestNavHostController
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import xelagurd.socialdating.client.AndroidNavigationTestUtils.navigateToRegistration
import xelagurd.socialdating.client.AndroidNavigationTestUtils.performNavigateUp
import xelagurd.socialdating.client.AndroidNavigationTestUtils.registerAndNavigateToCategories
import xelagurd.socialdating.client.AndroidTestUtils.assertCurrentRouteName
import xelagurd.socialdating.client.MainActivity
import xelagurd.socialdating.client.ui.navigation.AppNavHost
import xelagurd.socialdating.client.ui.navigation.CategoriesDestination
import xelagurd.socialdating.client.ui.navigation.LoginDestination

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
    fun appNavHost_performRegistration_navigatesToCategories() {
        composeTestRule.navigateToRegistration()
        composeTestRule.registerAndNavigateToCategories()

        navController.assertCurrentRouteName(CategoriesDestination.route)
        //navController.assertBackStackDepth(?)
    }

    @Test
    fun appNavHost_clickBack_navigatesToLogin() {
        composeTestRule.navigateToRegistration()
        composeTestRule.performNavigateUp()

        navController.assertCurrentRouteName(LoginDestination.route)
        //navController.assertBackStackDepth(?)
    }
}