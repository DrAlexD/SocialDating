package xelagurd.socialdating.client.ui.viewmodel

import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import xelagurd.socialdating.client.data.AppLocaleManager
import xelagurd.socialdating.client.data.AppUiModeManager
import xelagurd.socialdating.client.data.PreferencesRepository
import xelagurd.socialdating.client.data.local.repository.CommonLocalRepository
import xelagurd.socialdating.client.data.model.enums.AppLanguage
import xelagurd.socialdating.client.data.model.enums.ThemeMode
import xelagurd.socialdating.client.ui.state.RequestStatus
import xelagurd.socialdating.client.ui.state.SettingsUiState

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val preferencesRepository: PreferencesRepository,
    private val commonLocalRepository: CommonLocalRepository,
    private val appLocaleManager: AppLocaleManager,
    private val appUiModeManager: AppUiModeManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(
        SettingsUiState(
            themeMode = runBlocking { preferencesRepository.themeMode.first() },
            language = appLocaleManager.getAppLanguage()
        )
    )
    val uiState = _uiState.asStateFlow()

    fun updateThemeMode(themeMode: ThemeMode) {
        _uiState.update { it.copy(themeMode = themeMode) }

        appUiModeManager.setThemeMode(themeMode)

        viewModelScope.launch {
            preferencesRepository.saveThemeMode(themeMode)
        }
    }

    fun updateLanguage(language: AppLanguage) {
        _uiState.update { it.copy(language = language) }

        appLocaleManager.setAppLanguage(language)
    }

    fun logout() {
        viewModelScope.launch {
            _uiState.update { it.copy(actionRequestStatus = RequestStatus.LOADING) }

            preferencesRepository.clearPreferences()
            commonLocalRepository.clearData()

            _uiState.update { it.copy(actionRequestStatus = RequestStatus.SUCCESS) }
        }
    }
}
