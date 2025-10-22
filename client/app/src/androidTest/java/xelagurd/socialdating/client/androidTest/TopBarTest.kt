package xelagurd.socialdating.client.androidTest

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
import xelagurd.socialdating.client.MainActivity
import xelagurd.socialdating.client.R
import xelagurd.socialdating.client.onNodeWithContentDescriptionId
import xelagurd.socialdating.client.onNodeWithTextId
import xelagurd.socialdating.client.ui.AppTopBar
import xelagurd.socialdating.client.ui.navigation.CategoriesDestination
import xelagurd.socialdating.client.ui.state.RequestStatus

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
    fun topBar_loadingState_loadingText() {
        setContentToAppTopBar(RequestStatus.LOADING)

        composeTestRule.onNodeWithContentDescriptionId(R.string.back_button).assertIsNotDisplayed()
        composeTestRule.onNodeWithContentDescriptionId(R.string.refresh).assertIsNotDisplayed()
        composeTestRule.onNodeWithTextId(R.string.loading).assertIsDisplayed()
    }

    @Test
    fun topBar_errorState_offlineTextWithRefresh() {
        setContentToAppTopBar(RequestStatus.ERROR())

        composeTestRule.onNodeWithContentDescriptionId(R.string.back_button).assertIsNotDisplayed()
        composeTestRule.onNodeWithContentDescriptionId(R.string.refresh).assertIsDisplayed()
        composeTestRule.onNodeWithTextId(R.string.offline).assertIsDisplayed()
    }

    @Test
    fun topBar_successState_onlineText() {
        setContentToAppTopBar(RequestStatus.SUCCESS)

        composeTestRule.onNodeWithContentDescriptionId(R.string.back_button).assertIsNotDisplayed()
        composeTestRule.onNodeWithContentDescriptionId(R.string.refresh).assertIsNotDisplayed()
        composeTestRule.onNodeWithTextId(R.string.online).assertIsDisplayed()
    }

    @Test
    fun topBar_backButton() {
        setContentToAppTopBar(RequestStatus.SUCCESS) {}

        composeTestRule.onNodeWithContentDescriptionId(R.string.back_button).assertIsDisplayed()
    }

    private fun setContentToAppTopBar(
        dataRequestStatus: RequestStatus,
        navigateUp: (() -> Unit)? = null
    ) {
        composeTestRule.activity.setContent {
            Scaffold(
                topBar = {
                    AppTopBar(
                        title = stringResource(CategoriesDestination.titleRes),
                        dataRequestStatus = dataRequestStatus,
                        refreshAction = { },
                        navigateUp = navigateUp
                    )
                }
            ) { innerPadding ->
                Text(
                    text = "",
                    modifier = Modifier.padding(innerPadding)
                )
            }
        }
    }
}