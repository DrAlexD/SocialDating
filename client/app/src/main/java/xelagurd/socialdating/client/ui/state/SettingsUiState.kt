package xelagurd.socialdating.client.ui.state

import xelagurd.socialdating.client.data.model.enums.AppLanguage
import xelagurd.socialdating.client.data.model.enums.ThemeMode

data class SettingsUiState(
    val themeMode: ThemeMode = ThemeMode.SYSTEM,
    val language: AppLanguage = AppLanguage.SYSTEM,
    override val actionRequestStatus: RequestStatus = RequestStatus.UNDEFINED
) : ActionRequestUiState
