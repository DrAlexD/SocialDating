package xelagurd.socialdating.client.androidTest

import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.platform.UriHandler
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsNotSelected
import androidx.compose.ui.test.assertIsSelected
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import xelagurd.socialdating.client.AndroidTestUtils.checkButtonAndClick
import xelagurd.socialdating.client.AndroidTestUtils.checkEnabledButton
import xelagurd.socialdating.client.AndroidTestUtils.initializeTestTopLevelDestinations
import xelagurd.socialdating.client.AndroidTestUtils.onNodeWithTagId
import xelagurd.socialdating.client.AndroidTestUtils.onNodeWithTextId
import xelagurd.socialdating.client.AndroidTestUtils.onNodeWithTextIdWithColon
import xelagurd.socialdating.client.AndroidTestUtils.performPullToRefresh
import xelagurd.socialdating.client.AndroidTestUtils.setContentToScreen
import xelagurd.socialdating.client.AndroidTestUtils.setContentToScreenAndRecompose
import xelagurd.socialdating.client.HiltTestActivity
import xelagurd.socialdating.client.R
import xelagurd.socialdating.client.data.fake.FakeData
import xelagurd.socialdating.client.ui.screen.ProfileScreenComponent
import xelagurd.socialdating.client.ui.state.ProfileUiState
import xelagurd.socialdating.client.ui.state.RequestStatus

@HiltAndroidTest
class ProfileScreenTest {
    @get:Rule(order = 0)
    val hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 1)
    val composeTestRule = createAndroidComposeRule<HiltTestActivity>()

    @Before
    fun setup() {
        hiltRule.inject()
        composeTestRule.initializeTestTopLevelDestinations()
    }

    private val user = FakeData.users[0]

    @Test
    fun profileScreen_defaultParameters_loadingIndicator() {
        composeTestRule.setContentToScreen {
            ProfileScreenComponent()
        }

        composeTestRule.onNodeWithTagId(R.string.loading).assertIsDisplayed()
    }

    @Test
    fun profileScreen_recomposition_loadingIndicator() {
        composeTestRule.setContentToScreenAndRecompose {
            ProfileScreenComponent(profileUiState = ProfileUiState())
        }

        composeTestRule.onNodeWithTagId(R.string.loading).assertIsDisplayed()
    }

    @Test
    fun profileScreen_data_displayedData() {
        val profileUiState = ProfileUiState(
            entity = user,
            dataRequestStatus = RequestStatus.SUCCESS
        )

        setContentToProfileBody(profileUiState)

        composeTestRule.onNodeWithTextIdWithColon(R.string.username).assertIsDisplayed()
        composeTestRule.onNodeWithTextIdWithColon(R.string.telegram).assertIsDisplayed()
        composeTestRule.onNodeWithTextIdWithColon(R.string.name).assertIsDisplayed()
        composeTestRule.onNodeWithTextIdWithColon(R.string.age).assertIsDisplayed()
        composeTestRule.onNodeWithTextIdWithColon(R.string.city).assertIsDisplayed()
        composeTestRule.onNodeWithTextIdWithColon(R.string.purpose).assertIsDisplayed()

        composeTestRule.onNodeWithText(user.username).assertIsDisplayed()
        composeTestRule.onNodeWithText(user.telegram).assertIsDisplayed()
        composeTestRule.onNodeWithText(user.name).assertIsDisplayed()
        composeTestRule.onNodeWithText(user.age.toString()).assertIsDisplayed()
        composeTestRule.onNodeWithText(user.city).assertIsDisplayed()
        composeTestRule.onNodeWithTextId(R.string.all_at_once).assertIsDisplayed()

        composeTestRule.onNodeWithTextId(R.string.open_profile_statistics).checkEnabledButton()
    }

    @Test
    fun profileScreen_telegramClick_openedTelegramUrl() {
        var openedUrl: String? = null
        val uriHandler = object : UriHandler {
            override fun openUri(uri: String) {
                openedUrl = uri
            }
        }
        val profileUiState = ProfileUiState(
            entity = user,
            dataRequestStatus = RequestStatus.SUCCESS
        )

        composeTestRule.setContentToScreen {
            CompositionLocalProvider(LocalUriHandler provides uriHandler) {
                ProfileScreenComponent(profileUiState = profileUiState)
            }
        }

        composeTestRule.onNodeWithText(user.telegram).performClick()

        assertEquals("https://t.me/telegram1", openedUrl)
    }

    @Test
    fun profileScreen_anotherUser_selectedSimilarUsersNavigationItem() {
        val profileUiState = ProfileUiState(
            userId = user.id,
            anotherUserId = user.id + 1,
            entity = user,
            dataRequestStatus = RequestStatus.SUCCESS
        )

        setContentToProfileBody(profileUiState)

        composeTestRule.onNodeWithTagId(R.string.nav_similar_users).assertIsSelected()
        composeTestRule.onNodeWithTagId(R.string.nav_profile).assertIsNotSelected()
    }

    @Test
    fun profileScreen_currentUser_selectedProfileNavigationItem() {
        val profileUiState = ProfileUiState(
            userId = user.id,
            anotherUserId = user.id,
            entity = user,
            dataRequestStatus = RequestStatus.SUCCESS
        )

        setContentToProfileBody(profileUiState)

        composeTestRule.onNodeWithTagId(R.string.nav_profile).assertIsSelected()
        composeTestRule.onNodeWithTagId(R.string.nav_similar_users).assertIsNotSelected()
    }

    @Test
    fun profileScreen_allActions_calledAllActions() {
        var clickedUserId = -1
        var isRefreshed = false
        val profileUiState = ProfileUiState(
            entity = user,
            dataRequestStatus = RequestStatus.SUCCESS
        )

        composeTestRule.setContentToScreen {
            ProfileScreenComponent(
                profileUiState = profileUiState,
                onProfileStatisticsClick = { clickedUserId = it },
                onRefresh = { isRefreshed = true }
            )
        }

        composeTestRule.onNodeWithTextId(R.string.open_profile_statistics).checkButtonAndClick()
        assertEquals(user.id, clickedUserId)

        composeTestRule.performPullToRefresh()
        assertTrue(isRefreshed)
    }

    private fun setContentToProfileBody(profileUiState: ProfileUiState) {
        composeTestRule.setContentToScreen {
            ProfileScreenComponent(
                profileUiState = profileUiState
            )
        }
    }
}