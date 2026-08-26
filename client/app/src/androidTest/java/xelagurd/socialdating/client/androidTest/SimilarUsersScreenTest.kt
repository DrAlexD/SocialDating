package xelagurd.socialdating.client.androidTest

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithText
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import xelagurd.socialdating.client.AndroidTestUtils.checkButtonAndClick
import xelagurd.socialdating.client.AndroidTestUtils.onNodeWithContentDescriptionId
import xelagurd.socialdating.client.AndroidTestUtils.onNodeWithTagId
import xelagurd.socialdating.client.AndroidTestUtils.onNodeWithTextId
import xelagurd.socialdating.client.AndroidTestUtils.setContentToScreen
import xelagurd.socialdating.client.AndroidTestUtils.setContentToScreenAndRecompose
import xelagurd.socialdating.client.MainActivity
import xelagurd.socialdating.client.R
import xelagurd.socialdating.client.data.fake.FakeData
import xelagurd.socialdating.client.data.model.DataUtils.toSimilarUsersWithData
import xelagurd.socialdating.client.ui.screen.SimilarUsersScreenComponent
import xelagurd.socialdating.client.ui.state.RequestStatus
import xelagurd.socialdating.client.ui.state.SimilarUsersUiState

@HiltAndroidTest
class SimilarUsersScreenTest {
    @get:Rule(order = 0)
    val hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 1)
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    @Before
    fun setup() {
        hiltRule.inject()
    }

    private val similarUsers = FakeData.similarUsers.toSimilarUsersWithData(FakeData.users)
    private val similarUser = similarUsers[0]

    @Test
    fun similarUsersScreen_defaultParameters_loadingIndicator() {
        composeTestRule.setContentToScreen {
            SimilarUsersScreenComponent()
        }

        composeTestRule.onNodeWithTagId(R.string.loading).assertIsDisplayed()
    }

    @Test
    fun similarUsersScreen_recomposition_loadingIndicator() {
        composeTestRule.setContentToScreenAndRecompose {
            SimilarUsersScreenComponent(similarUsersUiState = SimilarUsersUiState())
        }

        composeTestRule.onNodeWithTagId(R.string.loading).assertIsDisplayed()
    }

    @Test
    fun similarUsersScreen_data_displayedData() {
        val similarUsersUiState = SimilarUsersUiState(
            entities = similarUsers,
            dataRequestStatus = RequestStatus.SUCCESS
        )

        setContentToSimilarUsersBody(similarUsersUiState)

        val purpose = composeTestRule.activity.getString(similarUser.purpose.descriptionRes)

        composeTestRule.onNodeWithText("${similarUser.name}, ${similarUser.age}").assertIsDisplayed()
        composeTestRule.onNodeWithText("${similarUser.city}, $purpose").assertIsDisplayed()

        composeTestRule.onNodeWithContentDescriptionId(R.string.full_maintain).assertIsDisplayed()
        composeTestRule.onNodeWithText(similarUser.similarNumber.toString()).assertIsDisplayed()
        composeTestRule.onNodeWithContentDescriptionId(R.string.full_no_maintain).assertIsDisplayed()
        composeTestRule.onNodeWithText(similarUser.oppositeNumber.toString()).assertIsDisplayed()

        composeTestRule.onNodeWithText(similarUser.similarCategories.joinToString()).assertIsDisplayed()
        composeTestRule.onNodeWithText(similarUser.oppositeCategories.joinToString()).assertIsDisplayed()
    }

    @Test
    fun similarUsersScreen_allActions_calledAllActions() {
        var clickedSimilarUserId = -1
        var isRefreshClicked = false
        val similarUsersUiState = SimilarUsersUiState(
            entities = similarUsers,
            dataRequestStatus = RequestStatus.SUCCESS
        )

        composeTestRule.setContentToScreen {
            SimilarUsersScreenComponent(
                similarUsersUiState = similarUsersUiState,
                onSimilarUserClick = { clickedSimilarUserId = it },
                refreshAction = { isRefreshClicked = true }
            )
        }

        composeTestRule.onNodeWithText("${similarUser.name}, ${similarUser.age}").checkButtonAndClick()
        assertEquals(similarUser.id, clickedSimilarUserId)

        composeTestRule.onNodeWithTextId(R.string.online).checkButtonAndClick()
        assertTrue(isRefreshClicked)
    }

    private fun setContentToSimilarUsersBody(similarUsersUiState: SimilarUsersUiState) {
        composeTestRule.setContentToScreen {
            SimilarUsersScreenComponent(
                similarUsersUiState = similarUsersUiState
            )
        }
    }
}