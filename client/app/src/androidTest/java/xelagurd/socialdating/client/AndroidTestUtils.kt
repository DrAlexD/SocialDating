package xelagurd.socialdating.client

import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.StringRes
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableIntStateOf
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
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import xelagurd.socialdating.client.ui.theme.AppTheme

object AndroidTestUtils {

    const val INPUT_TEXT = "Text"

    fun <A : ComponentActivity> AndroidComposeTestRule<ActivityScenarioRule<A>, A>.setContentToScreen(
        content: @Composable () -> Unit
    ) {
        activity.setContent {
            AppTheme {
                content()
            }
        }
    }

    fun <A : ComponentActivity> AndroidComposeTestRule<ActivityScenarioRule<A>, A>.setContentToScreenAndRecompose(
        content: @Composable () -> Unit
    ) {
        val recompositionTrigger = mutableIntStateOf(0)

        setContentToScreen {
            if (recompositionTrigger.intValue >= 0) {
                content()
            }
        }
        waitForIdle()

        runOnUiThread { recompositionTrigger.intValue++ }
        waitForIdle()
    }

    fun NavController.getCurrentRoute() = currentBackStackEntry?.destination?.route

    fun NavController.assertCurrentRouteName(expectedRouteName: String) =
        assertEquals(expectedRouteName, getCurrentRoute())

    fun NavController.assertBackStackDepth(expectedDepth: Int) =
        assertEquals(expectedDepth, currentBackStack.value.size)

    fun NavController.assertRouteInBackStack(expectedRouteName: String) =
        assertTrue(isRouteInBackStack(expectedRouteName))

    fun NavController.assertRouteNotInBackStack(notExpectedRouteName: String) =
        assertFalse(isRouteInBackStack(notExpectedRouteName))

    private fun NavController.isRouteInBackStack(routeName: String) =
        currentBackStack.value.any { it.destination.route == routeName }

    fun <A : ComponentActivity> AndroidComposeTestRule<ActivityScenarioRule<A>, A>.onNodeWithContentDescriptionId(
        @StringRes id: Int
    ) =
        onNodeWithContentDescription(activity.getString(id))

    fun <A : ComponentActivity> AndroidComposeTestRule<ActivityScenarioRule<A>, A>.onNodeWithTagId(
        @StringRes id: Int,
        suffix: String = ""
    ) =
        onNodeWithTag(activity.getString(id) + suffix)

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