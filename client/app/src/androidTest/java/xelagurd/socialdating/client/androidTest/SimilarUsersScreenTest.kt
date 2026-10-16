package xelagurd.socialdating.client.androidTest

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.SemanticsNodeInteraction
import androidx.compose.ui.test.assert
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsNotDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import xelagurd.socialdating.client.AndroidTestUtils.checkButtonAndClick
import xelagurd.socialdating.client.AndroidTestUtils.checkDisabledButton
import xelagurd.socialdating.client.AndroidTestUtils.checkTextFieldAndInput
import xelagurd.socialdating.client.AndroidTestUtils.onNodeWithContentDescriptionId
import xelagurd.socialdating.client.AndroidTestUtils.onNodeWithRange
import xelagurd.socialdating.client.AndroidTestUtils.onNodeWithTagId
import xelagurd.socialdating.client.AndroidTestUtils.onNodeWithTextId
import xelagurd.socialdating.client.AndroidTestUtils.onNodeWithTextIdWithColon
import xelagurd.socialdating.client.AndroidTestUtils.performPullToRefresh
import xelagurd.socialdating.client.AndroidTestUtils.setContentToScreen
import xelagurd.socialdating.client.AndroidTestUtils.setContentToScreenAndRecompose
import xelagurd.socialdating.client.HiltTestActivity
import xelagurd.socialdating.client.R
import xelagurd.socialdating.client.data.fake.FakeData
import xelagurd.socialdating.client.data.model.DefaultDataProperties.AGE_MAX
import xelagurd.socialdating.client.data.model.DefaultDataProperties.AGE_MIN
import xelagurd.socialdating.client.data.model.DefaultDataProperties.DIFFERENCE_NUMBER_MIN
import xelagurd.socialdating.client.data.model.enums.DifferenceRangeType.CUSTOM
import xelagurd.socialdating.client.data.model.enums.Gender.FEMALE
import xelagurd.socialdating.client.data.model.enums.Purpose.RELATIONSHIPS
import xelagurd.socialdating.client.ui.form.SimilarUsersFilterFormData
import xelagurd.socialdating.client.ui.screen.SimilarUsersScreenComponent
import xelagurd.socialdating.client.ui.state.RequestStatus
import xelagurd.socialdating.client.ui.state.SimilarUsersUiState

@HiltAndroidTest
class SimilarUsersScreenTest {
    @get:Rule(order = 0)
    val hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 1)
    val composeTestRule = createAndroidComposeRule<HiltTestActivity>()

    @Before
    fun setup() {
        hiltRule.inject()
    }

    private val similarUsers = FakeData.similarUsers
    private val similarUser = similarUsers[0]
    private val categories = FakeData.categories
    private val formData = FakeData.similarUsersFilterFormData
    private val hasError = SemanticsMatcher.keyIsDefined(SemanticsProperties.Error)
    private val nextPageErrorText = "NextPageError"

    @Test
    fun similarUsersScreen_defaultParameters_displayedCalculationHint() {
        composeTestRule.setContentToScreen {
            SimilarUsersScreenComponent()
        }

        assertCalculationHintIsDisplayed()
    }

    @Test
    fun similarUsersScreen_recomposition_displayedCalculationHint() {
        composeTestRule.setContentToScreenAndRecompose {
            SimilarUsersScreenComponent(similarUsersUiState = SimilarUsersUiState())
        }

        assertCalculationHintIsDisplayed()
    }

    @Test
    fun similarUsersScreen_loadingState_loadingIndicators() {
        setContentToSimilarUsersBody(SimilarUsersUiState(dataRequestStatus = RequestStatus.LOADING))

        composeTestRule.onNodeWithTagId(R.string.loading).assertIsDisplayed()
        composeTestRule.onNodeWithContentDescriptionId(R.string.loading).assertIsDisplayed()
        composeTestRule.onNodeWithTextId(R.string.calculate_similar_users_hint).assertDoesNotExist()
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

        composeTestRule.onNodeWithContentDescriptionId(R.string.online).assertIsDisplayed()
    }

    @Test
    fun similarUsersScreen_lastPage_displayedNoMoreData() {
        val similarUsersUiState = SimilarUsersUiState(
            entities = similarUsers,
            dataRequestStatus = RequestStatus.SUCCESS,
            isLastPage = true
        )

        setContentToSimilarUsersBody(similarUsersUiState)

        composeTestRule.onNodeWithTextId(R.string.no_more_data).assertIsDisplayed()
    }

    @Test
    fun similarUsersScreen_nextPageLoading_displayedLoadingIndicator() {
        val similarUsersUiState = SimilarUsersUiState(
            entities = similarUsers,
            dataRequestStatus = RequestStatus.SUCCESS,
            nextPageRequestStatus = RequestStatus.LOADING
        )

        setContentToSimilarUsersBody(similarUsersUiState)

        composeTestRule.onNodeWithTagId(R.string.loading).assertIsDisplayed()
    }

    @Test
    fun similarUsersScreen_nextPageError_loadedNextPageOnRetry() {
        var isLoadNextPageClicked = false
        val similarUsersUiState = SimilarUsersUiState(
            entities = similarUsers,
            dataRequestStatus = RequestStatus.SUCCESS,
            nextPageRequestStatus = RequestStatus.ERROR(nextPageErrorText)
        )

        composeTestRule.setContentToScreen {
            SimilarUsersScreenComponent(
                similarUsersUiState = similarUsersUiState,
                onLoadNextPage = { isLoadNextPageClicked = true }
            )
        }

        composeTestRule.onNodeWithText(nextPageErrorText).checkButtonAndClick()
        assertTrue(isLoadNextPageClicked)
    }

    @Test
    fun similarUsersScreen_calculateClickBeforeCalculation_calledCalculateAction() {
        var calculationsCount = 0

        composeTestRule.setContentToScreen {
            SimilarUsersScreenComponent(
                onCalculate = { calculationsCount++ }
            )
        }

        composeTestRule.onNodeWithTextId(R.string.calculate).checkButtonAndClick()
        assertEquals(1, calculationsCount)
    }

    @Test
    fun similarUsersScreen_allActions_calledAllActions() {
        var clickedSimilarUserId = -1
        var calculationsCount = 0
        var refreshesCount = 0
        val similarUsersUiState = SimilarUsersUiState(
            entities = similarUsers,
            dataRequestStatus = RequestStatus.SUCCESS
        )

        composeTestRule.setContentToScreen {
            SimilarUsersScreenComponent(
                similarUsersUiState = similarUsersUiState,
                onSimilarUserClick = { clickedSimilarUserId = it },
                onCalculate = { calculationsCount++ },
                onRefresh = { refreshesCount++ }
            )
        }

        composeTestRule.onNodeWithText("${similarUser.name}, ${similarUser.age}").checkButtonAndClick()
        assertEquals(similarUser.id, clickedSimilarUserId)

        composeTestRule.performPullToRefresh()
        assertEquals(1, refreshesCount)
        assertEquals(0, calculationsCount)

        composeTestRule.onNodeWithTextId(R.string.calculate).checkButtonAndClick()
        assertEquals(1, calculationsCount)
        assertEquals(1, refreshesCount)
    }

    @Test
    fun similarUsersScreen_activeFilters_displayedBadgeWithFiltersNumber() {
        setContentToSimilarUsersBody(SimilarUsersUiState(formData = formData))

        composeTestRule.onNodeWithText(formData.activeFiltersNumber.toString()).assertIsDisplayed()
    }

    @Test
    fun similarUsersScreen_invalidForm_displayedErrorAndDisabledCalculation() {
        var calculationsCount = 0

        composeTestRule.setContentToScreen {
            SimilarUsersScreenComponent(
                similarUsersUiState = SimilarUsersUiState(
                    formData = formData.copy(minDifferenceNumber = "", maxDifferenceNumber = "")
                ),
                onCalculate = { calculationsCount++ }
            )
        }

        composeTestRule.onNodeWithTextId(R.string.calculate).apply {
            checkDisabledButton()
            performClick()
        }
        assertEquals(0, calculationsCount)

        openFilterSheet()

        // the empty range is the error of both fields, but its message is shown once
        composeTestRule.onNodeWithTextId(R.string.error_empty_range).scrollToAndAssertIsDisplayed()
        composeTestRule.onNodeWithTextId(R.string.min_similarity).assert(hasError)
        composeTestRule.onNodeWithTextId(R.string.max_similarity).assert(hasError)
    }

    @Test
    fun similarUsersScreen_filtersClick_displayedAllFilters() {
        setContentToSimilarUsersBody(SimilarUsersUiState(categories = categories))

        openFilterSheet()

        composeTestRule.onNodeWithTextId(R.string.filters).assertIsDisplayed()
        composeTestRule.onNodeWithTextIdWithColon(R.string.categories).scrollToAndAssertIsDisplayed()
        categories.forEach {
            composeTestRule.onNodeWithText(it.name).scrollToAndAssertIsDisplayed()
        }
        composeTestRule.onNodeWithTextIdWithColon(R.string.similarity).scrollToAndAssertIsDisplayed()
        composeTestRule.onNodeWithTextId(R.string.default_similarity).scrollToAndAssertIsDisplayed()
        composeTestRule.onNodeWithTextId(R.string.custom_similarity).scrollToAndAssertIsDisplayed()
        composeTestRule.onNodeWithTextId(R.string.default_similarity_hint, DIFFERENCE_NUMBER_MIN)
            .scrollToAndAssertIsDisplayed()
        // the borders are typed only for the custom range
        composeTestRule.onNodeWithTextId(R.string.min_similarity).assertDoesNotExist()
        composeTestRule.onNodeWithTextId(R.string.max_similarity).assertDoesNotExist()
        composeTestRule.onNodeWithTextIdWithColon(R.string.gender).scrollToAndAssertIsDisplayed()
        composeTestRule.onNodeWithTextId(R.string.male).scrollToAndAssertIsDisplayed()
        composeTestRule.onNodeWithTextId(R.string.female).scrollToAndAssertIsDisplayed()
        composeTestRule.onNodeWithTextIdWithColon(R.string.age).scrollToAndAssertIsDisplayed()
        composeTestRule.onNodeWithRange(AGE_MIN..AGE_MAX).scrollToAndAssertIsDisplayed()
        composeTestRule.onNodeWithTextIdWithColon(R.string.purpose).scrollToAndAssertIsDisplayed()
        composeTestRule.onNodeWithTextId(R.string.friends).scrollToAndAssertIsDisplayed()
        composeTestRule.onNodeWithTextId(R.string.relationships).scrollToAndAssertIsDisplayed()
        composeTestRule.onNodeWithTextId(R.string.all_at_once).assertDoesNotExist()
        composeTestRule.onNodeWithTextId(R.string.all_at_once_always_displayed).scrollToAndAssertIsDisplayed()
        composeTestRule.onNodeWithTextIdWithColon(R.string.city).scrollToAndAssertIsDisplayed()
        composeTestRule.onNodeWithTextId(R.string.only_my_city).scrollToAndAssertIsDisplayed()
        composeTestRule.onNodeWithTextId(R.string.reset).scrollToAndAssertIsDisplayed()
        composeTestRule.onNodeWithTextId(R.string.done).scrollToAndAssertIsDisplayed()
    }

    @Test
    fun similarUsersScreen_filterChanges_calledFilterChangeActionWithChangedFilter() {
        var changedFormData by mutableStateOf(SimilarUsersFilterFormData())

        composeTestRule.setContentToScreen {
            SimilarUsersScreenComponent(
                similarUsersUiState = SimilarUsersUiState(
                    categories = categories,
                    formData = changedFormData
                ),
                onValueChange = { changedFormData = it }
            )
        }

        openFilterSheet()

        composeTestRule.onNodeWithText(categories[0].name).scrollToAndClick()
        composeTestRule.onNodeWithTextId(R.string.female).scrollToAndClick()
        composeTestRule.onNodeWithTextId(R.string.relationships).scrollToAndClick()
        composeTestRule.onNodeWithTextId(R.string.only_my_city).scrollToAndClick()
        composeTestRule.onNodeWithTextId(R.string.custom_similarity).scrollToAndClick()
        // the keyboard shifts the sheet content while it is appearing, so the text is typed after all the clicks
        composeTestRule.onNodeWithTextId(R.string.min_similarity).apply {
            performScrollTo()
            checkTextFieldAndInput(MIN_DIFFERENCE_NUMBER.toString())
        }
        composeTestRule.onNodeWithTextId(R.string.max_similarity).apply {
            performScrollTo()
            checkTextFieldAndInput(MAX_DIFFERENCE_NUMBER.toString())
        }
        // the default range is described only while it is chosen
        composeTestRule.onNodeWithTextId(R.string.default_similarity_hint, DIFFERENCE_NUMBER_MIN).assertDoesNotExist()

        val expectedFormData = SimilarUsersFilterFormData(
            categoryIds = setOf(categories[0].id),
            differenceRangeType = CUSTOM,
            minDifferenceNumber = MIN_DIFFERENCE_NUMBER.toString(),
            maxDifferenceNumber = MAX_DIFFERENCE_NUMBER.toString(),
            genders = setOf(FEMALE),
            purposes = setOf(RELATIONSHIPS),
            isSameCity = true
        )
        assertEquals(expectedFormData, changedFormData)
    }

    @Test
    fun similarUsersScreen_filterReset_calledFilterChangeActionWithEmptyFilter() {
        var changedFormData: SimilarUsersFilterFormData? = null

        composeTestRule.setContentToScreen {
            SimilarUsersScreenComponent(
                similarUsersUiState = SimilarUsersUiState(formData = formData),
                onValueChange = { changedFormData = it }
            )
        }

        openFilterSheet()

        composeTestRule.onNodeWithTextId(R.string.reset).scrollToAndClick()
        assertEquals(SimilarUsersFilterFormData(), changedFormData)
    }

    @Test
    fun similarUsersScreen_filterDone_hiddenFiltersWithoutCalculation() {
        var calculationsCount = 0

        composeTestRule.setContentToScreen {
            SimilarUsersScreenComponent(
                similarUsersUiState = SimilarUsersUiState(categories = categories),
                onCalculate = { calculationsCount++ }
            )
        }

        openFilterSheet()

        composeTestRule.onNodeWithTextId(R.string.done).scrollToAndClick()
        composeTestRule.waitForIdle()

        composeTestRule.onNodeWithTextId(R.string.filters).assertDoesNotExist()
        composeTestRule.onNodeWithTextId(R.string.calculate_similar_users_hint).assertIsDisplayed()
        assertEquals(0, calculationsCount)
    }

    private fun openFilterSheet() {
        composeTestRule.onNodeWithContentDescriptionId(R.string.filters).checkButtonAndClick()
        composeTestRule.waitForIdle()
    }

    private fun assertCalculationHintIsDisplayed() {
        composeTestRule.onNodeWithTextId(R.string.calculate_similar_users_hint).assertIsDisplayed()
        composeTestRule.onNodeWithTextId(R.string.calculate).assertIsDisplayed()
        composeTestRule.onNodeWithContentDescriptionId(R.string.filters).assertIsDisplayed()
        composeTestRule.onNodeWithContentDescriptionId(R.string.not_requested).assertIsDisplayed()
        composeTestRule.onNodeWithTagId(R.string.refresh_indicator).assertIsNotDisplayed()
        composeTestRule.onNodeWithContentDescriptionId(R.string.loading).assertIsNotDisplayed()
    }

    private fun SemanticsNodeInteraction.scrollToAndAssertIsDisplayed() {
        performScrollTo()
        assertIsDisplayed()
    }

    private fun SemanticsNodeInteraction.scrollToAndClick() {
        performScrollTo()
        checkButtonAndClick()
    }

    private fun setContentToSimilarUsersBody(similarUsersUiState: SimilarUsersUiState) {
        composeTestRule.setContentToScreen {
            SimilarUsersScreenComponent(
                similarUsersUiState = similarUsersUiState
            )
        }
    }

    private companion object {
        const val MIN_DIFFERENCE_NUMBER = -3
        const val MAX_DIFFERENCE_NUMBER = 5
    }
}
