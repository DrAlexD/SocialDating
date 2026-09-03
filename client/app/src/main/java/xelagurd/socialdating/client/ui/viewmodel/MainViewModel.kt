package xelagurd.socialdating.client.ui.viewmodel

import javax.inject.Inject
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.runBlocking
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import xelagurd.socialdating.client.data.AppUiModeManager
import xelagurd.socialdating.client.data.PreferencesRepository
import xelagurd.socialdating.client.data.model.DataUtils.TIMEOUT_MILLIS

@HiltViewModel
class MainViewModel @Inject constructor(
    private val preferencesRepository: PreferencesRepository,
    appUiModeManager: AppUiModeManager
) : ViewModel() {
    val themeMode = preferencesRepository.themeMode
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(TIMEOUT_MILLIS),
            initialValue = runBlocking { preferencesRepository.themeMode.first() }
        )

    init {
        appUiModeManager.setThemeMode(themeMode.value)
    }
}
