package xelagurd.socialdating.tests

import androidx.activity.compose.setContent
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.test.isDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.navigation.compose.ComposeNavigator
import androidx.navigation.testing.TestNavHostController
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import xelagurd.socialdating.MainActivity
import xelagurd.socialdating.R
import xelagurd.socialdating.assertCurrentRouteName
import xelagurd.socialdating.data.fake.FakeDataSource
import xelagurd.socialdating.ui.navigation.AppNavHost
import xelagurd.socialdating.ui.navigation.CategoriesDestination
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
            navController = TestNavHostController(LocalContext.current).apply {
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
        navController.assertCurrentRouteName(CategoriesDestination.route)
    }

    @Test
    fun appNavHost_verifyBackNavigationNotShownOnStartScreen() {
        val backText = composeTestRule.activity.getString(R.string.back_button)
        composeTestRule.onNodeWithContentDescription(backText).assertDoesNotExist()
    }

    @Test
    fun appNavHost_clickCategory_navigatesToStatements() {
        composeTestRule.waitUntil(TIMEOUT_MILLIS) {
            composeTestRule.onNodeWithText(FakeDataSource.categories[0].name).isDisplayed()
        }

        composeTestRule.onNodeWithText(FakeDataSource.categories[0].name)
            .performClick()

        navController.assertCurrentRouteName(StatementsDestination.routeWithArgs)
    }

    @Test
    fun appNavHost_clickStatement_navigatesToStatementDetails() {
        composeTestRule.waitUntil(TIMEOUT_MILLIS) {
            composeTestRule.onNodeWithText(FakeDataSource.categories[0].name).isDisplayed()
        }

        composeTestRule.onNodeWithText(FakeDataSource.categories[0].name)
            .performClick()

        composeTestRule.waitUntil(TIMEOUT_MILLIS) {
            composeTestRule.onNodeWithText(FakeDataSource.statements[0].text).isDisplayed()
        }

        composeTestRule.onNodeWithText(FakeDataSource.statements[0].text)
            .performClick()

        navController.assertCurrentRouteName(StatementDetailsDestination.routeWithArgs)
    }

    companion object {
        private const val TIMEOUT_MILLIS = 5_000L
    }
}