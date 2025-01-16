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
import xelagurd.socialdating.data.fake.FAKE_SERVER_LATENCY
import xelagurd.socialdating.data.local.repository.LocalUsersRepository
import xelagurd.socialdating.data.remote.repository.RemoteUsersRepository
import xelagurd.socialdating.ui.navigation.ProfileDestination
import xelagurd.socialdating.ui.state.ProfileUiState
import xelagurd.socialdating.ui.state.RequestStatus

@HiltViewModel
class ProfileViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val remoteRepository: RemoteUsersRepository,
    private val localRepository: LocalUsersRepository
) : ViewModel() {
    private val userId: Int = checkNotNull(savedStateHandle[ProfileDestination.userId])

    private val dataRequestStatusFlow = MutableStateFlow(RequestStatus.UNDEFINED)
    private val userFlow = localRepository.getUser(userId).distinctUntilChanged()

    val uiState = combine(userFlow, dataRequestStatusFlow) { user, dataRequestStatus ->
        ProfileUiState(
            entity = user,
            dataRequestStatus = dataRequestStatus
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
                dataRequestStatusFlow.update { RequestStatus.LOADING }

                delay(FAKE_SERVER_LATENCY) // FixMe: remove after implementing server

                val remoteUser = remoteRepository.getUser(userId)
                remoteUser?.let { localRepository.insertUser(it) }

                dataRequestStatusFlow.update { RequestStatus.SUCCESS }
            } catch (_: IOException) {
                dataRequestStatusFlow.update { RequestStatus.ERROR }
            }
        }
    }

    companion object {
        private const val TIMEOUT_MILLIS = 5_000L
    }
}