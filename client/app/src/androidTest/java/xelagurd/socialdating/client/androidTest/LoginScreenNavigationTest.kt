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
import xelagurd.socialdating.client.MainActivity
import xelagurd.socialdating.client.assertCurrentRouteName
import xelagurd.socialdating.client.loginAndNavigateToCategories
import xelagurd.socialdating.client.navigateToRegistration
import xelagurd.socialdating.client.ui.navigation.AppNavHost
import xelagurd.socialdating.client.ui.navigation.CategoriesDestination
import xelagurd.socialdating.client.ui.navigation.LoginDestination
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
    fun appNavHost_performLogin_navigatesToCategories() {
        composeTestRule.loginAndNavigateToCategories()

        navController.assertCurrentRouteName(CategoriesDestination.route)
        //navController.assertBackStackDepth(?)
    }

    @Test
    fun appNavHost_clickRegistration_navigatesToRegistration() {
        composeTestRule.navigateToRegistration()

        navController.assertCurrentRouteName(RegistrationDestination.route)
        //navController.assertBackStackDepth(?)
    }
}