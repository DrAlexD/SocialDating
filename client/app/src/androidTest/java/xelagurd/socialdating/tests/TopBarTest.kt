package xelagurd.socialdating.tests

import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsNotDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import xelagurd.socialdating.AppTopBar
import xelagurd.socialdating.MainActivity
import xelagurd.socialdating.R
import xelagurd.socialdating.onNodeWithContentDescriptionId
import xelagurd.socialdating.onNodeWithTextId
import xelagurd.socialdating.ui.navigation.CategoriesDestination
import xelagurd.socialdating.ui.state.InternetStatus

@OptIn(ExperimentalMaterial3Api::class)
@HiltAndroidTest
class TopBarTest {
    @get:Rule(order = 0)
    val hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 1)
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    @Before
    fun setup() {
        hiltRule.inject()
    }

    @Test
    fun topBar_loadingStatus_loadingText() {
        composeTestRule.activity.setContent {
            Scaffold(
                topBar = {
                    AppTopBar(
                        title = stringResource(CategoriesDestination.titleRes),
                        internetStatus = InternetStatus.LOADING,
                        refreshAction = { }
                    )
                }
            ) { innerPadding ->
                Text(
                    text = "",
                    modifier = Modifier.padding(innerPadding)
                )
            }
        }

        composeTestRule.onNodeWithContentDescriptionId(R.string.back_button).assertIsNotDisplayed()
        composeTestRule.onNodeWithContentDescriptionId(R.string.refresh).assertIsNotDisplayed()
        composeTestRule.onNodeWithTextId(R.string.loading).assertIsDisplayed()
    }

    @Test
    fun topBar_offlineStatus_offlineTextWithRefresh() {
        composeTestRule.activity.setContent {
            Scaffold(
                topBar = {
                    AppTopBar(
                        title = stringResource(CategoriesDestination.titleRes),
                        internetStatus = InternetStatus.OFFLINE,
                        refreshAction = { }
                    )
                }
            ) { innerPadding ->
                Text(
                    text = "",
                    modifier = Modifier.padding(innerPadding)
                )
            }
        }

        composeTestRule.onNodeWithContentDescriptionId(R.string.back_button).assertIsNotDisplayed()
        composeTestRule.onNodeWithContentDescriptionId(R.string.refresh).assertIsDisplayed()
        composeTestRule.onNodeWithTextId(R.string.offline).assertIsDisplayed()
    }

    @Test
    fun topBar_onlineStatus_onlineText() {
        composeTestRule.activity.setContent {
            Scaffold(
                topBar = {
                    AppTopBar(
                        title = stringResource(CategoriesDestination.titleRes),
                        internetStatus = InternetStatus.ONLINE,
                        refreshAction = { }
                    )
                }
            ) { innerPadding ->
                Text(
                    text = "",
                    modifier = Modifier.padding(innerPadding)
                )
            }
        }

        composeTestRule.onNodeWithContentDescriptionId(R.string.back_button).assertIsNotDisplayed()
        composeTestRule.onNodeWithContentDescriptionId(R.string.refresh).assertIsNotDisplayed()
        composeTestRule.onNodeWithTextId(R.string.online).assertIsDisplayed()
    }

    @Test
    fun topBar_backButton() {
        composeTestRule.activity.setContent {
            Scaffold(
                topBar = {
                    AppTopBar(
                        title = stringResource(CategoriesDestination.titleRes),
                        internetStatus = InternetStatus.ONLINE,
                        refreshAction = { },
                        navigateUp = {}
                    )
                }
            ) { innerPadding ->
                Text(
                    text = "",
                    modifier = Modifier.padding(innerPadding)
                )
            }
        }

        composeTestRule.onNodeWithContentDescriptionId(R.string.back_button).assertIsDisplayed()
    }
}