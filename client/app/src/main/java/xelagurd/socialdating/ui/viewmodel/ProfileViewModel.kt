package xelagurd.socialdating.ui.viewmodel

import java.io.IOException
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import android.content.Context
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import retrofit2.HttpException
import xelagurd.socialdating.R
import xelagurd.socialdating.data.local.repository.LocalUsersRepository
import xelagurd.socialdating.data.remote.repository.RemoteUsersRepository
import xelagurd.socialdating.ui.navigation.ProfileDestination
import xelagurd.socialdating.ui.state.ProfileUiState
import xelagurd.socialdating.ui.state.RequestStatus

@HiltViewModel
class ProfileViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    savedStateHandle: SavedStateHandle,
    private val remoteRepository: RemoteUsersRepository,
    private val localRepository: LocalUsersRepository
) : ViewModel() {
    private val userId: Int = checkNotNull(savedStateHandle[ProfileDestination.userId])

    private val dataRequestStatusFlow = MutableStateFlow<RequestStatus>(RequestStatus.UNDEFINED)
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

                val remoteUser = remoteRepository.getUser(userId)

                if (remoteUser != null) {
                    localRepository.insertUser(remoteUser)

                    dataRequestStatusFlow.update { RequestStatus.SUCCESS }
                } else {
                    dataRequestStatusFlow.update {
                        RequestStatus.FAILURE(
                            failureText = context.getString(R.string.no_data)
                        )
                    }
                }
            } catch (e: Exception) {
                when (e) {
                    is IOException, is HttpException -> {
                        dataRequestStatusFlow.update {
                            RequestStatus.ERROR(
                                errorText = context.getString(R.string.no_internet_connection)
                            )
                        }
                    }

                    else -> throw e
                }
            }
        }
    }

    companion object {
        private const val TIMEOUT_MILLIS = 5_000L
    }
}