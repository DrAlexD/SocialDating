package xelagurd.socialdating.client.androidTest

import androidx.compose.ui.test.junit4.createAndroidComposeRule
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import androidx.compose.ui.test.assertIsDisplayed
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import xelagurd.socialdating.client.AndroidTestUtils.checkButtonAndClick
import xelagurd.socialdating.client.AndroidTestUtils.checkEnabledButton
import xelagurd.socialdating.client.AndroidTestUtils.onNodeWithTagId
import xelagurd.socialdating.client.AndroidTestUtils.onNodeWithTextId
import xelagurd.socialdating.client.AndroidTestUtils.onNodeWithTextIdWithColon
import xelagurd.socialdating.client.AndroidTestUtils.setContentToScreen
import xelagurd.socialdating.client.AndroidTestUtils.setContentToScreenAndRecompose
import xelagurd.socialdating.client.MainActivity
import xelagurd.socialdating.client.R
import xelagurd.socialdating.client.data.model.enums.AppLanguage
import xelagurd.socialdating.client.data.model.enums.ThemeMode
import xelagurd.socialdating.client.ui.screen.SettingsScreenComponent
import xelagurd.socialdating.client.ui.state.SettingsUiState

@HiltAndroidTest
class SettingsScreenTest {
    @get:Rule(order = 0)
    val hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 1)
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    @Before
    fun setup() {
        hiltRule.inject()
    }

    @Test
    fun settingsScreen_defaultParameters_displayedLogoutButton() {
        composeTestRule.setContentToScreen {
            SettingsScreenComponent()
        }

        composeTestRule.onNodeWithTextId(R.string.logout).checkEnabledButton()
    }

    @Test
    fun settingsScreen_recomposition_displayedLogoutButton() {
        composeTestRule.setContentToScreenAndRecompose {
            SettingsScreenComponent(settingsUiState = SettingsUiState())
        }

        composeTestRule.onNodeWithTextId(R.string.logout).checkEnabledButton()
    }

    @Test
    fun settingsScreen_defaultParameters_displayedThemeAndLanguageSettings() {
        composeTestRule.setContentToScreen {
            SettingsScreenComponent()
        }

        composeTestRule.onNodeWithTextIdWithColon(R.string.theme).assertIsDisplayed()
        composeTestRule.onNodeWithTextId(R.string.theme_dark).assertIsDisplayed()
        composeTestRule.onNodeWithTextIdWithColon(R.string.language).assertIsDisplayed()
        composeTestRule.onNodeWithTextId(R.string.language_russian).assertIsDisplayed()
    }

    @Test
    fun settingsScreen_themeModeClick_calledThemeModeSelectAction() {
        var selectedThemeMode: ThemeMode? = null

        composeTestRule.setContentToScreen {
            SettingsScreenComponent(
                settingsUiState = SettingsUiState(),
                onThemeModeSelect = { selectedThemeMode = it }
            )
        }

        composeTestRule
            .onNodeWithTagId(R.string.theme_dark, composeTestRule.activity.getString(R.string.theme))
            .checkButtonAndClick()

        assertEquals(ThemeMode.DARK, selectedThemeMode)
    }

    @Test
    fun settingsScreen_languageClick_calledLanguageSelectAction() {
        var selectedLanguage: AppLanguage? = null

        composeTestRule.setContentToScreen {
            SettingsScreenComponent(
                settingsUiState = SettingsUiState(),
                onLanguageSelect = { selectedLanguage = it }
            )
        }

        composeTestRule
            .onNodeWithTagId(
                R.string.language_russian,
                composeTestRule.activity.getString(R.string.language)
            )
            .checkButtonAndClick()

        assertEquals(AppLanguage.RUSSIAN, selectedLanguage)
    }

    @Test
    fun settingsScreen_logoutClick_calledLogoutAction() {
        var isLogoutClicked = false

        composeTestRule.setContentToScreen {
            SettingsScreenComponent(
                settingsUiState = SettingsUiState(),
                onSuccessLogout = {},
                onLogoutClick = { isLogoutClicked = true }
            )
        }

        composeTestRule.onNodeWithTextId(R.string.logout).checkButtonAndClick()

        assertTrue(isLogoutClicked)
    }
}