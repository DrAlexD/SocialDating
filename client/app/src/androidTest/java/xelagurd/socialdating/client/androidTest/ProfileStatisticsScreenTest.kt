package xelagurd.socialdating.client.androidTest

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsNotDisplayed
import androidx.compose.ui.test.assertIsNotSelected
import androidx.compose.ui.test.assertIsSelected
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithText
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
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
import xelagurd.socialdating.client.data.model.DataUtils.toUserCategoriesWithData
import xelagurd.socialdating.client.data.model.DataUtils.toUserDefiningThemesWithData
import xelagurd.socialdating.client.ui.screen.ProfileStatisticsScreenComponent
import xelagurd.socialdating.client.ui.state.ProfileStatisticsUiState
import xelagurd.socialdating.client.ui.state.RequestStatus

@HiltAndroidTest
class ProfileStatisticsScreenTest {
    @get:Rule(order = 0)
    val hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 1)
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    @Before
    fun setup() {
        hiltRule.inject()
    }

    private val userCategoriesWithData = FakeData.userCategories.toUserCategoriesWithData(FakeData.categories)
    private val userDefiningThemesWithData = FakeData.userDefiningThemes
        .toUserDefiningThemesWithData(FakeData.definingThemes)

    private val userCategoryWithData = userCategoriesWithData[0]
    private val userDefiningThemeWithData = userDefiningThemesWithData[0]

    private val entityIdToData = userDefiningThemesWithData.groupBy { it.categoryId }

    @Test
    fun profileStatisticsScreen_defaultParameters_loadingIndicator() {
        composeTestRule.setContentToScreen {
            ProfileStatisticsScreenComponent()
        }

        composeTestRule.onNodeWithTagId(R.string.loading).assertIsDisplayed()
    }

    @Test
    fun profileStatisticsScreen_recomposition_loadingIndicator() {
        composeTestRule.setContentToScreenAndRecompose {
            ProfileStatisticsScreenComponent(profileStatisticsUiState = ProfileStatisticsUiState())
        }

        composeTestRule.onNodeWithTagId(R.string.loading).assertIsDisplayed()
    }

    @Test
    fun profileStatisticsScreen_expandedCategoryWithDefiningThemes_displayedDefiningThemes() {
        val profileStatisticsUiState = ProfileStatisticsUiState(
            entities = listOf(userCategoryWithData),
            entityIdToData = mapOf(userCategoryWithData.categoryId to listOf(userDefiningThemeWithData)),
            dataRequestStatus = RequestStatus.SUCCESS
        )

        setContentToProfileStatisticsBody(profileStatisticsUiState)

        composeTestRule.onNodeWithText(userCategoryWithData.categoryName).assertIsDisplayed()
        composeTestRule.onNodeWithText(userDefiningThemeWithData.definingThemeName).assertIsNotDisplayed()
        composeTestRule.onNodeWithText(userDefiningThemeWithData.definingThemeFromOpinion).assertIsNotDisplayed()
        composeTestRule.onNodeWithText(userDefiningThemeWithData.definingThemeToOpinion).assertIsNotDisplayed()
        composeTestRule.onNodeWithTagId(R.string.progress_indicator).assertIsNotDisplayed()

        composeTestRule.onNodeWithContentDescriptionId(R.string.expand_list).checkButtonAndClick()

        composeTestRule.onNodeWithText(userCategoryWithData.categoryName).assertIsDisplayed()
        composeTestRule.onNodeWithText(userDefiningThemeWithData.definingThemeName).assertIsDisplayed()
        composeTestRule.onNodeWithText(userDefiningThemeWithData.definingThemeFromOpinion).assertIsDisplayed()
        composeTestRule.onNodeWithText(userDefiningThemeWithData.definingThemeToOpinion).assertIsDisplayed()
        composeTestRule.onNodeWithTagId(R.string.progress_indicator).assertIsDisplayed()
    }

    @Test
    fun profileStatisticsScreen_similarCategory_displayedSimilarityData() {
        assertSimilarityDataIsDisplayed(categoryIndex = 0)
    }

    @Test
    fun profileStatisticsScreen_oppositeCategory_displayedSimilarityData() {
        assertSimilarityDataIsDisplayed(categoryIndex = 1)
    }

    @Test
    fun profileStatisticsScreen_expandedCategoryWithoutDefiningThemes_displayedOnlyCategory() {
        val profileStatisticsUiState = ProfileStatisticsUiState(
            entities = listOf(userCategoryWithData),
            entityIdToData = mapOf(),
            dataRequestStatus = RequestStatus.SUCCESS
        )

        setContentToProfileStatisticsBody(profileStatisticsUiState)

        composeTestRule.onNodeWithContentDescriptionId(R.string.expand_list).checkButtonAndClick()

        composeTestRule.onNodeWithText(userCategoryWithData.categoryName).assertIsDisplayed()
        composeTestRule.onNodeWithTagId(R.string.progress_indicator).assertIsNotDisplayed()
    }

    @Test
    fun profileStatisticsScreen_expandedCategoryWithEmptyDefiningThemes_displayedOnlyCategory() {
        val profileStatisticsUiState = ProfileStatisticsUiState(
            entities = listOf(userCategoryWithData),
            entityIdToData = mapOf(userCategoryWithData.categoryId to listOf()),
            dataRequestStatus = RequestStatus.SUCCESS
        )

        setContentToProfileStatisticsBody(profileStatisticsUiState)

        composeTestRule.onNodeWithContentDescriptionId(R.string.expand_list).checkButtonAndClick()

        composeTestRule.onNodeWithText(userCategoryWithData.categoryName).assertIsDisplayed()
        composeTestRule.onNodeWithTagId(R.string.progress_indicator).assertIsNotDisplayed()
    }

    @Test
    fun profileStatisticsScreen_anotherUser_selectedSimilarUsersNavigationItem() {
        val profileStatisticsUiState = ProfileStatisticsUiState(
            userId = FakeData.mainUser.id,
            anotherUserId = FakeData.mainUser.id + 1,
            entities = listOf(userCategoryWithData),
            dataRequestStatus = RequestStatus.SUCCESS
        )

        setContentToProfileStatisticsBody(profileStatisticsUiState)

        composeTestRule.onNodeWithTagId(R.string.nav_similar_users).assertIsSelected()
        composeTestRule.onNodeWithTagId(R.string.nav_profile).assertIsNotSelected()
    }

    @Test
    fun profileStatisticsScreen_currentUser_selectedProfileNavigationItem() {
        val profileStatisticsUiState = ProfileStatisticsUiState(
            userId = FakeData.mainUser.id,
            anotherUserId = FakeData.mainUser.id,
            entities = listOf(userCategoryWithData),
            dataRequestStatus = RequestStatus.SUCCESS
        )

        setContentToProfileStatisticsBody(profileStatisticsUiState)

        composeTestRule.onNodeWithTagId(R.string.nav_profile).assertIsSelected()
        composeTestRule.onNodeWithTagId(R.string.nav_similar_users).assertIsNotSelected()
    }

    @Test
    fun profileStatisticsScreen_allActions_calledAllActions() {
        var isNavigateUpClicked = false
        var isRefreshClicked = false
        val profileStatisticsUiState = ProfileStatisticsUiState(
            entities = listOf(userCategoryWithData),
            entityIdToData = mapOf(userCategoryWithData.categoryId to listOf(userDefiningThemeWithData)),
            dataRequestStatus = RequestStatus.SUCCESS
        )

        composeTestRule.setContentToScreen {
            ProfileStatisticsScreenComponent(
                profileStatisticsUiState = profileStatisticsUiState,
                onNavigateUp = { isNavigateUpClicked = true },
                refreshAction = { isRefreshClicked = true }
            )
        }

        composeTestRule.onNodeWithContentDescriptionId(R.string.back_button).checkButtonAndClick()
        assertTrue(isNavigateUpClicked)

        composeTestRule.onNodeWithTextId(R.string.online).checkButtonAndClick()
        assertTrue(isRefreshClicked)
    }

    private fun assertSimilarityDataIsDisplayed(categoryIndex: Int) {
        val userCategory = userCategoriesWithData[categoryIndex]
        val userDefiningThemes = entityIdToData[userCategory.categoryId].orEmpty()
        val detailedSimilarCategory = FakeData.detailedSimilarUser.categories[userCategory.categoryId]!!

        val profileStatisticsUiState = ProfileStatisticsUiState(
            entities = listOf(userCategory),
            entityIdToData = mapOf(userCategory.categoryId to userDefiningThemes),
            entitiesMask = FakeData.detailedSimilarUser,
            dataRequestStatus = RequestStatus.SUCCESS
        )

        setContentToProfileStatisticsBody(profileStatisticsUiState)

        composeTestRule.onNodeWithText(userCategory.categoryName).assertIsDisplayed()
        composeTestRule.onNodeWithText(detailedSimilarCategory.similarNumber.toString()).assertIsDisplayed()
        composeTestRule.onNodeWithText(detailedSimilarCategory.oppositeNumber.toString()).assertIsDisplayed()

        composeTestRule.onNodeWithContentDescriptionId(R.string.expand_list).checkButtonAndClick()

        userDefiningThemes.forEach {
            composeTestRule.onNodeWithText(it.definingThemeName).assertIsDisplayed()
        }
    }

    private fun setContentToProfileStatisticsBody(profileStatisticsUiState: ProfileStatisticsUiState) {
        composeTestRule.setContentToScreen {
            ProfileStatisticsScreenComponent(
                profileStatisticsUiState = profileStatisticsUiState
            )
        }
    }
}