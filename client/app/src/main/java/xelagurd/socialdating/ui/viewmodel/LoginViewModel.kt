package xelagurd.socialdating.ui.viewmodel

import java.io.IOException
import javax.inject.Inject
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import org.mindrot.jbcrypt.BCrypt
import xelagurd.socialdating.PreferencesRepository
import xelagurd.socialdating.data.fake.FAKE_SERVER_LATENCY
import xelagurd.socialdating.data.fake.FakeDataSource
import xelagurd.socialdating.data.local.repository.LocalUsersRepository
import xelagurd.socialdating.data.remote.repository.RemoteUsersRepository
import xelagurd.socialdating.ui.state.LoginDetails
import xelagurd.socialdating.ui.state.LoginUiState
import xelagurd.socialdating.ui.state.RequestStatus

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val remoteRepository: RemoteUsersRepository,
    private val localRepository: LocalUsersRepository,
    private val preferencesRepository: PreferencesRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState = _uiState.asStateFlow()

    fun updateUiState(loginDetails: LoginDetails) {
        _uiState.update {
            it.copy(loginDetails = loginDetails)
        }
    }

    fun logIn() {
        viewModelScope.launch {
            try {
                _uiState.update { it.copy(requestStatus = RequestStatus.LOADING) }

                delay(FAKE_SERVER_LATENCY) // FixMe: remove after implementing server

                val user = remoteRepository.logInUser(
                    uiState.value.loginDetails.username,
                    BCrypt.hashpw(uiState.value.loginDetails.password, BCrypt.gensalt())
                )
                localRepository.insertUser(user)

                preferencesRepository.saveCurrentUserId(user.id)

                _uiState.update { it.copy(requestStatus = RequestStatus.SUCCESS) }
            } catch (_: IOException) {
                localRepository.insertUser(FakeDataSource.users[0]) // TODO: Remove after adding registration screen
                preferencesRepository.saveCurrentUserId(FakeDataSource.users[0].id) // TODO: Remove after adding registration screen

                _uiState.update { it.copy(requestStatus = RequestStatus.SUCCESS) } // TODO: Change to ERROR after adding registration screen
            }
        }
    }
}