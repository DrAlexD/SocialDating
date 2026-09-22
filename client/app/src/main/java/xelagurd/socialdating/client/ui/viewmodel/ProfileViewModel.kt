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
import xelagurd.socialdating.client.data.remote.ApiUtils.offlineModeStatus
import xelagurd.socialdating.client.data.remote.ApiUtils.safeApiCall
import xelagurd.socialdating.client.data.remote.repository.RemoteUsersRepository
import xelagurd.socialdating.client.ui.navigation.ProfileDestination
import xelagurd.socialdating.client.ui.state.ProfileUiState
import xelagurd.socialdating.client.ui.state.RequestStatus
import xelagurd.socialdating.client.ui.state.updateLoadingNotification
import xelagurd.socialdating.client.ui.state.hideWhileLoading

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
    private val notificationFlow = MutableStateFlow<String?>(null)
    private val userStateFlow = MutableStateFlow<User?>(null)
    private val userFlow = when (anotherUserId) {
        userId -> localUsersRepository.getUser(anotherUserId).distinctUntilChanged()
        else -> userStateFlow
    }

    val uiState = combine(
        userFlow,
        dataRequestStatusFlow,
        notificationFlow
    ) { user, dataRequestStatus, notification ->
        ProfileUiState(
            userId = userId,
            anotherUserId = anotherUserId,
            entity = user.hideWhileLoading(dataRequestStatus),
            dataRequestStatus = dataRequestStatus,
            notification = notification
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(TIMEOUT_MILLIS),
        initialValue = ProfileUiState(userId = userId, anotherUserId = anotherUserId)
    )

    init {
        if (!isOfflineMode) { // FixMe: remove after adding server hosting
            loadUser(isRequestedByUser = false)
        } else if (anotherUserId != userId) {
            dataRequestStatusFlow.update { RequestStatus.LOADING }
            userStateFlow.update { FakeData.users[1] }
            dataRequestStatusFlow.update { offlineModeStatus(context) }
        } else {
            dataRequestStatusFlow.update { offlineModeStatus(context) }
        }

        viewModelScope.launch {
            preferencesRepository.languageChanges.collect { loadUser(isRequestedByUser = false) }
        }
    }

    fun onNotificationShown() = notificationFlow.update { null }

    fun getUser() = loadUser(isRequestedByUser = true)

    private fun loadUser(isRequestedByUser: Boolean) {
        if (isOfflineMode) { // FixMe: remove after adding server hosting
            if (isRequestedByUser) {
                notificationFlow.update { offlineModeStatus(context).notificationText() }
            }
            return
        }

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
            notificationFlow.updateLoadingNotification(
                requestStatus = status,
                isRequestedByUser = isRequestedByUser,
                isDataExist = userFlow.first() != null
            )
        }
    }
}