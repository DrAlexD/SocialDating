package xelagurd.socialdating.client.androidTest

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
import xelagurd.socialdating.client.AndroidTestUtils.onNodeWithContentDescriptionId
import xelagurd.socialdating.client.AndroidTestUtils.onNodeWithTextId
import xelagurd.socialdating.client.AndroidTestUtils.setContentToScreen
import xelagurd.socialdating.client.HiltTestActivity
import xelagurd.socialdating.client.R
import xelagurd.socialdating.client.ui.AppTopBar
import xelagurd.socialdating.client.ui.navigation.CategoriesDestination
import xelagurd.socialdating.client.ui.state.RequestStatus

@OptIn(ExperimentalMaterial3Api::class)
@HiltAndroidTest
class TopBarTest {
    @get:Rule(order = 0)
    val hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 1)
    val composeTestRule = createAndroidComposeRule<HiltTestActivity>()

    @Before
    fun setup() {
        hiltRule.inject()
    }

    @Test
    fun topBar_undefinedState_loadingIndicator() {
        setContentToAppTopBar(RequestStatus.UNDEFINED)

        composeTestRule.onNodeWithContentDescriptionId(R.string.back_button).assertIsNotDisplayed()
        composeTestRule.onNodeWithContentDescriptionId(R.string.loading).assertIsDisplayed()
    }

    @Test
    fun topBar_loadingState_loadingIndicator() {
        setContentToAppTopBar(RequestStatus.LOADING)

        composeTestRule.onNodeWithContentDescriptionId(R.string.back_button).assertIsNotDisplayed()
        composeTestRule.onNodeWithContentDescriptionId(R.string.loading).assertIsDisplayed()
    }

    @Test
    fun topBar_errorState_offlineIndicator() {
        setContentToAppTopBar(RequestStatus.ERROR())

        composeTestRule.onNodeWithContentDescriptionId(R.string.back_button).assertIsNotDisplayed()
        composeTestRule.onNodeWithContentDescriptionId(R.string.offline).assertIsDisplayed()
    }

    @Test
    fun topBar_failureState_offlineIndicator() {
        setContentToAppTopBar(RequestStatus.FAILURE())

        composeTestRule.onNodeWithContentDescriptionId(R.string.back_button).assertIsNotDisplayed()
        composeTestRule.onNodeWithContentDescriptionId(R.string.offline).assertIsDisplayed()
    }

    @Test
    fun topBar_successState_onlineIndicator() {
        setContentToAppTopBar(RequestStatus.SUCCESS)

        composeTestRule.onNodeWithContentDescriptionId(R.string.back_button).assertIsNotDisplayed()
        composeTestRule.onNodeWithContentDescriptionId(R.string.online).assertIsDisplayed()
    }

    @Test
    fun topBar_withNavigateUpAction_displayedBackButton() {
        setContentToAppTopBar(RequestStatus.SUCCESS) {}

        composeTestRule.onNodeWithContentDescriptionId(R.string.back_button).assertIsDisplayed()
    }

    @Test
    fun topBar_withoutDataRequestStatus_onlyTitle() {
        composeTestRule.setContentToScreen {
            AppTopBar(
                title = stringResource(CategoriesDestination.titleRes),
                modifier = Modifier
            )
        }

        composeTestRule.onNodeWithTextId(R.string.app_name).assertIsDisplayed()
        composeTestRule.onNodeWithContentDescriptionId(R.string.online).assertIsNotDisplayed()
        composeTestRule.onNodeWithContentDescriptionId(R.string.loading).assertIsNotDisplayed()
        composeTestRule.onNodeWithContentDescriptionId(R.string.offline).assertIsNotDisplayed()
    }

    private fun setContentToAppTopBar(
        dataRequestStatus: RequestStatus,
        navigateUp: (() -> Unit)? = null
    ) {
        composeTestRule.setContentToScreen {
            Scaffold(
                topBar = {
                    AppTopBar(
                        title = stringResource(CategoriesDestination.titleRes),
                        dataRequestStatus = dataRequestStatus,
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
