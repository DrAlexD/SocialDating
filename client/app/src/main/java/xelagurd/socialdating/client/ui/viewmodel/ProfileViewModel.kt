package xelagurd.socialdating.client.ui.viewmodel

import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import android.content.Context
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import xelagurd.socialdating.client.data.PreferencesRepository
import xelagurd.socialdating.client.data.fake.FakeData
import xelagurd.socialdating.client.data.local.repository.LocalUsersRepository
import xelagurd.socialdating.client.data.model.DataUtils.TIMEOUT_MILLIS
import xelagurd.socialdating.client.data.model.User
import xelagurd.socialdating.client.data.remote.ApiUtils.safeApiCall
import xelagurd.socialdating.client.data.remote.repository.RemoteUsersRepository
import xelagurd.socialdating.client.ui.navigation.ProfileDestination
import xelagurd.socialdating.client.ui.state.ProfileUiState
import xelagurd.socialdating.client.ui.state.RequestStatus

@HiltViewModel
class ProfileViewModel @Inject constructor(
    @param:ApplicationContext private val context: Context,
    savedStateHandle: SavedStateHandle,
    private val preferencesRepository: PreferencesRepository,
    private val remoteUsersRepository: RemoteUsersRepository,
    private val localUsersRepository: LocalUsersRepository
) : ViewModel() {

    private val userId: Int = checkNotNull(savedStateHandle[ProfileDestination.userId])
    private val anotherUserId: Int = checkNotNull(savedStateHandle[ProfileDestination.anotherUserId])
    private val isOfflineMode = runBlocking { preferencesRepository.isOfflineMode.first() }

    private val dataRequestStatusFlow = MutableStateFlow<RequestStatus>(RequestStatus.UNDEFINED)
    private val userStateFlow = MutableStateFlow<User?>(null)
    private val userFlow = when (anotherUserId) {
        userId -> localUsersRepository.getUser(anotherUserId).distinctUntilChanged()
        else -> userStateFlow
    }

    val uiState = combine(userFlow, dataRequestStatusFlow) { user, dataRequestStatus ->
        ProfileUiState(
            userId = userId,
            anotherUserId = anotherUserId,
            entity = user,
            dataRequestStatus = dataRequestStatus
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(TIMEOUT_MILLIS),
        initialValue = ProfileUiState()
    )

    init {
        if (!isOfflineMode) { // FixMe: remove after adding server hosting
            getUser()
        } else if (anotherUserId != userId) {
            dataRequestStatusFlow.update { RequestStatus.LOADING }
            userStateFlow.update { FakeData.users[1] }
            dataRequestStatusFlow.update { RequestStatus.SUCCESS }
        } else {
            dataRequestStatusFlow.update { RequestStatus.SUCCESS }
        }
    }

    fun getUser() {
        viewModelScope.launch {
            dataRequestStatusFlow.update { RequestStatus.LOADING }

            val (remoteUser, status) = safeApiCall(context) {
                remoteUsersRepository.getUser(anotherUserId)
            }

            if (remoteUser != null) {
                when (anotherUserId) {
                    userId -> localUsersRepository.insertUser(remoteUser)
                    else -> userStateFlow.update { remoteUser }
                }
            }

            dataRequestStatusFlow.update { status }
        }
    }
}