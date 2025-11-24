package xelagurd.socialdating.client

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsNotSelected
import androidx.compose.ui.test.assertIsSelected
import androidx.compose.ui.test.isDisplayed
import androidx.compose.ui.test.isNotDisplayed
import androidx.compose.ui.test.junit4.AndroidComposeTestRule
import androidx.compose.ui.test.onNodeWithText
import androidx.test.ext.junit.rules.ActivityScenarioRule
import xelagurd.socialdating.client.data.fake.FakeData
import xelagurd.socialdating.client.data.fake.FakeData.TEST_TIMEOUT_MILLIS

typealias MainComposeTestRule = AndroidComposeTestRule<ActivityScenarioRule<MainActivity>, MainActivity>

fun MainComposeTestRule.loginAndNavigateToCategories() {
    onNodeWithTextId(R.string.username).checkTextFieldAndInput(FakeData.loginFormData.username)
    onNodeWithTextId(R.string.password).checkTextFieldAndInput(FakeData.loginFormData.password)

    onNodeWithTextId(R.string.login).checkButtonAndClick()
    waitUntil(TEST_TIMEOUT_MILLIS) {
        onNodeWithTextId(R.string.login).isNotDisplayed()
    }

    checkBottomNavBarWithCategoriesTopLevel()
}

fun MainComposeTestRule.navigateToRegistration() {
    onNodeWithTextId(R.string.register).checkButtonAndClick()
}

fun MainComposeTestRule.registerAndNavigateToCategories() {
    onNodeWithTextId(R.string.username).checkTextFieldAndInput(FakeData.mainUser.username)
    onNodeWithTextId(R.string.name).checkTextFieldAndInput(FakeData.mainUser.name)
    onNodeWithTagId(R.string.male).checkButtonAndClick()
    onNodeWithTextId(R.string.age).checkTextFieldAndInput(FakeData.mainUser.age.toString())
    onNodeWithTextId(R.string.city).checkTextFieldAndInput(FakeData.mainUser.city)
    onNodeWithTagId(R.string.all_at_once).checkButtonAndClick()
    onNodeWithTextId(R.string.password).checkTextFieldAndInput(FakeData.registrationFormData.password)
    onNodeWithTextId(R.string.repeat_password).checkTextFieldAndInput(FakeData.registrationFormData.repeatedPassword)

    onNodeWithTextId(R.string.register).checkButtonAndClick()
    waitUntil(TEST_TIMEOUT_MILLIS) {
        onNodeWithTextId(R.string.register).isNotDisplayed()
    }

    checkBottomNavBarWithCategoriesTopLevel()
}

fun MainComposeTestRule.logoutAndNavigateToLoginScreen() {
    onNodeWithTextId(R.string.logout).checkButtonAndClick()
    waitUntil(TEST_TIMEOUT_MILLIS) {
        onNodeWithTextId(R.string.logout).isNotDisplayed()
    }
}

fun MainComposeTestRule.navigateToStatements() {
    loginAndNavigateToCategories()

    waitUntil(TEST_TIMEOUT_MILLIS) {
        onNodeWithText(FakeData.mainCategory.name).isDisplayed()
    }

    onNodeWithText(FakeData.mainCategory.name).checkButtonAndClick()
    checkBottomNavBarWithCategoriesTopLevel()
}

fun MainComposeTestRule.navigateToStatementAdding() {
    navigateToStatements()

    waitUntil(TEST_TIMEOUT_MILLIS) {
        onNodeWithText(FakeData.mainStatement.text).isDisplayed()
    }

    onNodeWithContentDescriptionId(R.string.add_statement).checkButtonAndClick()
    checkBottomNavBarWithCategoriesTopLevel()
}

fun MainComposeTestRule.addStatementAndNavigateToStatementsScreen() {
    onNodeWithTextId(R.string.statement_text).checkTextFieldAndInput(FakeData.newStatement.text)
    onNodeWithText(FakeData.mainDefiningTheme.name).checkButtonAndClick()
    onNodeWithTagId(R.string.yes).checkButtonAndClick()

    onNodeWithTextId(R.string.add_statement).checkButtonAndClick()
    waitUntil(TEST_TIMEOUT_MILLIS) {
        onNodeWithTextId(R.string.add_statement).isNotDisplayed()
    }

    checkBottomNavBarWithCategoriesTopLevel()
}

fun MainComposeTestRule.navigateToProfileStatistics() {
    navigateToProfileFromBottomNavBar()

    waitUntil(TEST_TIMEOUT_MILLIS) {
        onNodeWithTextId(R.string.open_profile_statistics).isDisplayed()
    }

    onNodeWithTextId(R.string.open_profile_statistics).checkButtonAndClick()
    checkBottomNavBarWithProfileTopLevel()
}

fun MainComposeTestRule.navigateToCategoriesFromBottomNavBar() {
    onNodeWithTagId(R.string.nav_categories).checkButtonAndClick()
    checkBottomNavBarWithCategoriesTopLevel()
}

fun MainComposeTestRule.navigateToProfileFromBottomNavBar() {
    onNodeWithTagId(R.string.nav_profile).checkButtonAndClick()
    checkBottomNavBarWithProfileTopLevel()
}

fun MainComposeTestRule.navigateToSettingsFromBottomNavBar() {
    onNodeWithTagId(R.string.nav_settings).checkButtonAndClick()
    checkBottomNavBarWithSettingsTopLevel()
}

fun MainComposeTestRule.checkBottomNavBarWithCategoriesTopLevel() {
    onNodeWithTagId(R.string.nav_profile).assertIsDisplayed()
    onNodeWithTagId(R.string.nav_profile).assertIsNotSelected()

    onNodeWithTagId(R.string.nav_settings).assertIsDisplayed()
    onNodeWithTagId(R.string.nav_settings).assertIsNotSelected()

    onNodeWithTagId(R.string.nav_categories).assertIsDisplayed()
    onNodeWithTagId(R.string.nav_categories).assertIsSelected()
}

fun MainComposeTestRule.checkBottomNavBarWithProfileTopLevel() {
    onNodeWithTagId(R.string.nav_categories).assertIsDisplayed()
    onNodeWithTagId(R.string.nav_categories).assertIsNotSelected()

    onNodeWithTagId(R.string.nav_settings).assertIsDisplayed()
    onNodeWithTagId(R.string.nav_settings).assertIsNotSelected()

    onNodeWithTagId(R.string.nav_profile).assertIsDisplayed()
    onNodeWithTagId(R.string.nav_profile).assertIsSelected()
}

fun MainComposeTestRule.checkBottomNavBarWithSettingsTopLevel() {
    onNodeWithTagId(R.string.nav_profile).assertIsDisplayed()
    onNodeWithTagId(R.string.nav_profile).assertIsNotSelected()

    onNodeWithTagId(R.string.nav_categories).assertIsDisplayed()
    onNodeWithTagId(R.string.nav_categories).assertIsNotSelected()

    onNodeWithTagId(R.string.nav_settings).assertIsDisplayed()
    onNodeWithTagId(R.string.nav_settings).assertIsSelected()
}

fun MainComposeTestRule.performNavigateUp() {
    onNodeWithContentDescriptionId(R.string.back_button).checkButtonAndClick()
}