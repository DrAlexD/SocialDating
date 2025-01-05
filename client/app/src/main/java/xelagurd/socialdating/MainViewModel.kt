package xelagurd.socialdating

import java.io.IOException
import javax.inject.Inject
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import xelagurd.socialdating.data.fake.FakeDataSource
import xelagurd.socialdating.data.local.repository.LocalUsersRepository

@HiltViewModel
class MainViewModel @Inject constructor(
    private val preferencesRepository: PreferencesRepository,
    private val localUsersRepository: LocalUsersRepository
) : ViewModel() {
    val currentUserid = preferencesRepository.currentUserId
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(TIMEOUT_MILLIS),
            initialValue = runBlocking { preferencesRepository.currentUserId.first() }
        )

    init {
        initializeApp()
    }

    fun initializeApp() {
        viewModelScope.launch {
            try {
                localUsersRepository.insertUser(FakeDataSource.users[0]) // TODO: Remove after adding registration screen
                preferencesRepository.saveCurrentUserId(FakeDataSource.users[0].id) // TODO: Remove after adding login screen
            } catch (_: IOException) {
            }
        }
    }

    companion object {
        private const val TIMEOUT_MILLIS = 5_000L
    }
}