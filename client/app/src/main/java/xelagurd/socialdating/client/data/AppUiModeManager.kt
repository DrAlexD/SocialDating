package xelagurd.socialdating.client.data

import javax.inject.Inject
import javax.inject.Singleton
import android.app.UiModeManager
import xelagurd.socialdating.client.data.model.enums.ThemeMode

@Singleton
class AppUiModeManager @Inject constructor(
    private val uiModeManager: UiModeManager
) {

    fun setThemeMode(themeMode: ThemeMode) {
        uiModeManager.setApplicationNightMode(
            when (themeMode) {
                ThemeMode.SYSTEM -> UiModeManager.MODE_NIGHT_AUTO
                ThemeMode.LIGHT -> UiModeManager.MODE_NIGHT_NO
                ThemeMode.DARK -> UiModeManager.MODE_NIGHT_YES
            }
        )
    }
}
