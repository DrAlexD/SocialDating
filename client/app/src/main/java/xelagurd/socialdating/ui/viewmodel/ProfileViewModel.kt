package xelagurd.socialdating.ui.viewmodel

import java.io.IOException
import javax.inject.Inject
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
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
    private val userId: Int = checkNotNull(savedStateHandle[ProfileDestination.userId])

    private val internetStatusFlow = MutableStateFlow(InternetStatus.LOADING)
    private val userFlow = localRepository.getUser(userId).distinctUntilChanged()

    val uiState = combine(userFlow, internetStatusFlow) { user, internetStatus ->
        ProfileUiState(
            user = user,
            internetStatus = internetStatus
        )
    }.stateIn(
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
                internetStatusFlow.update { InternetStatus.LOADING }

                delay(3000L) // FixMe: remove after implementing server

                val remoteUser = remoteRepository.getUser(userId)
                localRepository.insertUser(remoteUser)

                internetStatusFlow.update { InternetStatus.ONLINE }
            } catch (_: IOException) {
                internetStatusFlow.update { InternetStatus.OFFLINE }
            }
        }
    }

    companion object {
        private const val TIMEOUT_MILLIS = 5_000L
    }
}