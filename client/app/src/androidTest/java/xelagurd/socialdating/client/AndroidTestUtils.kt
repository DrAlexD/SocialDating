package xelagurd.socialdating.client

import androidx.activity.ComponentActivity
import androidx.annotation.StringRes
import androidx.compose.ui.test.SemanticsNodeInteraction
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsEnabled
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.junit4.AndroidComposeTestRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.navigation.NavController
import androidx.test.ext.junit.rules.ActivityScenarioRule
import org.junit.Assert.assertEquals

object AndroidTestUtils {

    fun NavController.getCurrentRoute() = currentBackStackEntry?.destination?.route

    fun NavController.assertCurrentRouteName(expectedRouteName: String) =
        assertEquals(expectedRouteName, getCurrentRoute())

    fun NavController.assertBackStackDepth(expectedDepth: Int) =
        assertEquals(expectedDepth, currentBackStack.value.size)

    fun <A : ComponentActivity> AndroidComposeTestRule<ActivityScenarioRule<A>, A>.onNodeWithContentDescriptionId(
        @StringRes id: Int
    ) =
        onNodeWithContentDescription(activity.getString(id))

    fun <A : ComponentActivity> AndroidComposeTestRule<ActivityScenarioRule<A>, A>.onNodeWithTagId(
        @StringRes id: Int
    ) =
        onNodeWithTag(activity.getString(id))

    fun <A : ComponentActivity> AndroidComposeTestRule<ActivityScenarioRule<A>, A>.onNodeWithTextId(
        @StringRes id: Int
    ) =
        onNodeWithText(activity.getString(id))

    fun <A : ComponentActivity> AndroidComposeTestRule<ActivityScenarioRule<A>, A>.onNodeWithTextIdWithColon(
        @StringRes id: Int
    ) =
        onNodeWithText(activity.getString(R.string.text_with_colon, activity.getString(id)))

    fun SemanticsNodeInteraction.checkEnabledButton() {
        this.assertIsDisplayed()
        this.assertIsEnabled()
    }

    fun SemanticsNodeInteraction.checkDisabledButton() {
        this.assertIsDisplayed()
        this.assertIsNotEnabled()
    }

    fun SemanticsNodeInteraction.checkButtonAndClick() {
        this.checkEnabledButton()
        this.performClick()
    }

    fun SemanticsNodeInteraction.checkTextField() {
        this.assertIsDisplayed()
        this.assertIsEnabled()
    }

    fun SemanticsNodeInteraction.checkTextFieldAndInput(text: String) {
        this.checkTextField()
        this.performTextInput(text)
    }
}