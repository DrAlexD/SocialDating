package xelagurd.socialdating.client.ui.navigation

import javax.inject.Inject
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.runBlocking
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import xelagurd.socialdating.client.data.PreferencesRepository

@HiltViewModel
class AppNavViewModel @Inject constructor(
    private val preferencesRepository: PreferencesRepository
) : ViewModel() {
    val currentUserId = preferencesRepository.currentUserId
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(TIMEOUT_MILLIS),
            initialValue = runBlocking { preferencesRepository.currentUserId.first() }
        )

    companion object {
        private const val TIMEOUT_MILLIS = 5_000L
    }
}