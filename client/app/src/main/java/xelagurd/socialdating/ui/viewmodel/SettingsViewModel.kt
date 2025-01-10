package xelagurd.socialdating.ui.viewmodel

import java.io.IOException
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import xelagurd.socialdating.PreferencesRepository
import xelagurd.socialdating.ui.state.RequestStatus
import xelagurd.socialdating.ui.state.SettingsUiState

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val preferencesRepository: PreferencesRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(SettingsUiState())
    val uiState = _uiState.asStateFlow()

    fun logout() {
        viewModelScope.launch {
            try {
                _uiState.update { it.copy(requestStatus = RequestStatus.LOADING) }

                preferencesRepository.saveCurrentUserId(-1)

                _uiState.update { it.copy(requestStatus = RequestStatus.SUCCESS) }
            } catch (_: IOException) {
                _uiState.update { it.copy(requestStatus = RequestStatus.ERROR) }
            }
        }
    }
}