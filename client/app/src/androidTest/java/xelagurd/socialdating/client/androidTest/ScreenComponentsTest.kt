package xelagurd.socialdating.client.androidTest

import androidx.compose.foundation.layout.Column
import androidx.compose.ui.Modifier
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsNotDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.unit.dp
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import xelagurd.socialdating.client.AndroidTestUtils.INPUT_TEXT
import xelagurd.socialdating.client.AndroidTestUtils.checkButtonAndClick
import xelagurd.socialdating.client.AndroidTestUtils.checkDisabledButton
import xelagurd.socialdating.client.AndroidTestUtils.checkTextFieldAndInput
import xelagurd.socialdating.client.AndroidTestUtils.onNodeWithTagId
import xelagurd.socialdating.client.AndroidTestUtils.onNodeWithTextId
import xelagurd.socialdating.client.AndroidTestUtils.onNodeWithTextIdWithColon
import xelagurd.socialdating.client.AndroidTestUtils.setContentToScreen
import xelagurd.socialdating.client.MainActivity
import xelagurd.socialdating.client.R
import xelagurd.socialdating.client.data.fake.FakeData
import xelagurd.socialdating.client.data.model.Category
import xelagurd.socialdating.client.ui.screen.AppDataChoosingList
import xelagurd.socialdating.client.ui.screen.AppDataList
import xelagurd.socialdating.client.ui.screen.AppEntityCard
import xelagurd.socialdating.client.ui.screen.AppExpandedEntityCard
import xelagurd.socialdating.client.ui.screen.AppLargeBodyText
import xelagurd.socialdating.client.ui.screen.AppLargeTextCard
import xelagurd.socialdating.client.ui.screen.AppLargeTitleText
import xelagurd.socialdating.client.ui.screen.AppList
import xelagurd.socialdating.client.ui.screen.AppLoadingIndicator
import xelagurd.socialdating.client.ui.screen.AppMediumBodyText
import xelagurd.socialdating.client.ui.screen.AppMediumTextCard
import xelagurd.socialdating.client.ui.screen.AppMediumTitleText
import xelagurd.socialdating.client.ui.screen.AppSmallBodyText
import xelagurd.socialdating.client.ui.screen.AppSmallTitleText
import xelagurd.socialdating.client.ui.screen.AppTextField
import xelagurd.socialdating.client.ui.screen.stringResourceWithColon

@HiltAndroidTest
class ScreenComponentsTest {
    @get:Rule(order = 0)
    val hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 1)
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    @Before
    fun setup() {
        hiltRule.inject()
    }

    private val categories = FakeData.categories.take(CATEGORIES_COUNT)

    @Test
    fun appLoadingIndicator_defaultParameters_displayedLoadingIndicator() {
        composeTestRule.setContentToScreen {
            AppLoadingIndicator()
        }

        composeTestRule.onNodeWithTagId(R.string.loading).assertIsDisplayed()
    }

    @Test
    fun stringResourceWithColon_stringResourceId_displayedTextWithColon() {
        composeTestRule.setContentToScreen {
            AppLargeTitleText(text = stringResourceWithColon(R.string.username))
        }

        composeTestRule.onNodeWithTextIdWithColon(R.string.username).assertIsDisplayed()
    }

    @Test
    fun appTexts_defaultModifier_areDisplayed() {
        composeTestRule.setContentToScreen {
            Column {
                AppSmallBodyText(text = SMALL_BODY_TEXT)
                AppMediumBodyText(text = MEDIUM_BODY_TEXT)
                AppLargeBodyText(text = LARGE_BODY_TEXT)
                AppSmallTitleText(text = SMALL_TITLE_TEXT)
                AppMediumTitleText(text = MEDIUM_TITLE_TEXT)
                AppLargeTitleText(text = LARGE_TITLE_TEXT)
            }
        }

        assertAllTextsAreDisplayed()
    }

    @Test
    fun appTexts_overrideModifier_areDisplayed() {
        composeTestRule.setContentToScreen {
            Column {
                AppSmallBodyText(text = SMALL_BODY_TEXT, overrideModifier = Modifier)
                AppMediumBodyText(text = MEDIUM_BODY_TEXT, overrideModifier = Modifier)
                AppLargeBodyText(text = LARGE_BODY_TEXT, overrideModifier = Modifier)
                AppSmallTitleText(text = SMALL_TITLE_TEXT, overrideModifier = Modifier)
                AppMediumTitleText(text = MEDIUM_TITLE_TEXT, overrideModifier = Modifier)
                AppLargeTitleText(text = LARGE_TITLE_TEXT, overrideModifier = Modifier)
            }
        }

        assertAllTextsAreDisplayed()
    }

    @Test
    fun appMediumTextCard_enabled_calledClickAction() {
        var isClicked = false

        composeTestRule.setContentToScreen {
            AppMediumTextCard(
                text = MEDIUM_TITLE_TEXT,
                onClick = { isClicked = true },
                isHasBorder = true
            )
        }

        composeTestRule.onNodeWithText(MEDIUM_TITLE_TEXT).checkButtonAndClick()

        assertTrue(isClicked)
    }

    @Test
    fun appMediumTextCard_disabled_isNotEnabled() {
        composeTestRule.setContentToScreen {
            AppMediumTextCard(
                text = MEDIUM_TITLE_TEXT,
                onClick = {},
                isEnabled = false,
                overrideModifier = Modifier
            )
        }

        composeTestRule.onNodeWithText(MEDIUM_TITLE_TEXT).checkDisabledButton()
    }

    @Test
    fun appLargeTextCard_enabled_calledClickAction() {
        var isClicked = false

        composeTestRule.setContentToScreen {
            AppLargeTextCard(
                text = LARGE_TITLE_TEXT,
                onClick = { isClicked = true },
                isHasBorder = true
            )
        }

        composeTestRule.onNodeWithText(LARGE_TITLE_TEXT).checkButtonAndClick()

        assertTrue(isClicked)
    }

    @Test
    fun appLargeTextCard_disabled_isNotEnabled() {
        composeTestRule.setContentToScreen {
            AppLargeTextCard(
                text = LARGE_TITLE_TEXT,
                onClick = {},
                isEnabled = false,
                overrideModifier = Modifier
            )
        }

        composeTestRule.onNodeWithText(LARGE_TITLE_TEXT).checkDisabledButton()
    }

    @Test
    fun appTextField_input_calledValueChangeAction() {
        var changedValue: String? = null

        composeTestRule.setContentToScreen {
            AppTextField(
                value = "",
                onValueChange = { changedValue = it },
                label = composeTestRule.activity.getString(R.string.username)
            )
        }

        composeTestRule.onNodeWithTextId(R.string.username).checkTextFieldAndInput(INPUT_TEXT)

        assertEquals(INPUT_TEXT, changedValue)
    }

    @Test
    fun appTextField_password_calledValueChangeAction() {
        var changedValue: String? = null

        composeTestRule.setContentToScreen {
            AppTextField(
                value = "",
                onValueChange = { changedValue = it },
                label = composeTestRule.activity.getString(R.string.password),
                overrideModifier = Modifier,
                isPassword = true
            )
        }

        composeTestRule.onNodeWithTextId(R.string.password).checkTextFieldAndInput(INPUT_TEXT)

        assertEquals(INPUT_TEXT, changedValue)
    }

    @Test
    fun appDataList_entities_displayedAllData() {
        composeTestRule.setContentToScreen {
            AppDataList(entities = categories) {
                AppLargeTitleText(text = (it as Category).name)
            }
        }

        assertAllCategoriesAreDisplayed()
    }

    @Test
    fun appDataChoosingList_withoutChosenEntity_displayedAllData() {
        composeTestRule.setContentToScreen {
            AppDataChoosingList(
                entities = categories,
                chosenEntityId = null,
                maxHeight = MAX_HEIGHT_DP.dp
            ) { entity, _ ->
                AppLargeTitleText(text = (entity as Category).name)
            }
        }

        assertAllCategoriesAreDisplayed()
    }

    @Test
    fun appDataChoosingList_chosenEntity_displayedOnlyChosenData() {
        composeTestRule.setContentToScreen {
            AppDataChoosingList(
                entities = categories,
                chosenEntityId = categories[0].id,
                maxHeight = MAX_HEIGHT_DP.dp
            ) { entity, _ ->
                AppLargeTitleText(text = (entity as Category).name)
            }
        }

        composeTestRule.onNodeWithText(categories[0].name).assertIsDisplayed()
        composeTestRule.onNodeWithText(categories[1].name).assertIsNotDisplayed()
    }

    @Test
    fun appList_entities_displayedAllData() {
        composeTestRule.setContentToScreen {
            AppList(entities = categories) {
                AppLargeTitleText(text = (it as Category).name)
            }
        }

        assertAllCategoriesAreDisplayed()
    }

    @Test
    fun appEntityCard_click_calledEntityClickAction() {
        var clickedEntityId = -1

        composeTestRule.setContentToScreen {
            AppEntityCard(
                entity = categories[0],
                onEntityClick = { clickedEntityId = it }
            ) {
                AppLargeTitleText(text = (it as Category).name)
            }
        }

        composeTestRule.onNodeWithText(categories[0].name).checkButtonAndClick()

        assertEquals(categories[0].id, clickedEntityId)
    }

    @Test
    fun appExpandedEntityCard_click_expandedAndCollapsedContent() {
        composeTestRule.setContentToScreen {
            AppExpandedEntityCard(entity = categories[0]) { entity, isExpanded ->
                AppLargeTitleText(text = (entity as Category).name)
                if (isExpanded) {
                    AppLargeTitleText(text = EXPANDED_TEXT)
                }
            }
        }

        composeTestRule.onNodeWithText(categories[0].name).assertIsDisplayed()
        composeTestRule.onNodeWithText(EXPANDED_TEXT).assertIsNotDisplayed()

        composeTestRule.onNodeWithText(categories[0].name).checkButtonAndClick()
        composeTestRule.onNodeWithText(EXPANDED_TEXT).assertIsDisplayed()

        composeTestRule.onNodeWithText(categories[0].name).checkButtonAndClick()
        composeTestRule.onNodeWithText(EXPANDED_TEXT).assertIsNotDisplayed()
    }

    private fun assertAllTextsAreDisplayed() {
        composeTestRule.onNodeWithText(SMALL_BODY_TEXT).assertIsDisplayed()
        composeTestRule.onNodeWithText(MEDIUM_BODY_TEXT).assertIsDisplayed()
        composeTestRule.onNodeWithText(LARGE_BODY_TEXT).assertIsDisplayed()
        composeTestRule.onNodeWithText(SMALL_TITLE_TEXT).assertIsDisplayed()
        composeTestRule.onNodeWithText(MEDIUM_TITLE_TEXT).assertIsDisplayed()
        composeTestRule.onNodeWithText(LARGE_TITLE_TEXT).assertIsDisplayed()
    }

    private fun assertAllCategoriesAreDisplayed() {
        categories.forEach {
            composeTestRule.onNodeWithText(it.name).assertIsDisplayed()
        }
    }

    private companion object {
        const val CATEGORIES_COUNT = 3
        const val MAX_HEIGHT_DP = 300
        const val EXPANDED_TEXT = "Expanded"
        const val SMALL_BODY_TEXT = "Small body"
        const val MEDIUM_BODY_TEXT = "Medium body"
        const val LARGE_BODY_TEXT = "Large body"
        const val SMALL_TITLE_TEXT = "Small title"
        const val MEDIUM_TITLE_TEXT = "Medium title"
        const val LARGE_TITLE_TEXT = "Large title"
    }
}