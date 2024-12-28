package xelagurd.socialdating.ui.viewmodel

import java.io.IOException
import javax.inject.Inject
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import xelagurd.socialdating.data.fake.FakeDataSource
import xelagurd.socialdating.data.local.repository.LocalUsersRepository
import xelagurd.socialdating.data.remote.repository.RemoteUsersRepository
import xelagurd.socialdating.ui.navigation.ProfileDestination
import xelagurd.socialdating.ui.state.InternetStatus
import xelagurd.socialdating.ui.state.ProfileUiState

@HiltViewModel
class ProfileViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val remoteRepository: RemoteUsersRepository,
    private val localRepository: LocalUsersRepository
) : ViewModel() {
    var internetStatus by mutableStateOf(InternetStatus.LOADING)
        private set

    private val userId: Int = checkNotNull(savedStateHandle[ProfileDestination.userId])

    val uiState: StateFlow<ProfileUiState> = localRepository.getUser(userId)
        .map { ProfileUiState(it) }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(TIMEOUT_MILLIS),
            initialValue = ProfileUiState()
        )

    init {
        getUser()
    }

    fun getUser() {
        viewModelScope.launch {
            try {
                internetStatus = InternetStatus.LOADING

                delay(3000L) // FixMe: remove after implementing server

                val remoteUser = remoteRepository.getUser(userId)
                localRepository.insertUser(remoteUser)

                internetStatus = InternetStatus.ONLINE
            } catch (_: IOException) {
                localRepository.insertUser(FakeDataSource.users[0]) // FixMe: remove after implementing server
                internetStatus = InternetStatus.OFFLINE
            }
        }
    }

    companion object {
        private const val TIMEOUT_MILLIS = 5_000L
    }
}