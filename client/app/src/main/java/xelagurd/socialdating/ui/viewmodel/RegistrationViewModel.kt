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
import xelagurd.socialdating.data.model.additional.RegistrationDetails
import xelagurd.socialdating.data.remote.repository.RemoteUsersRepository
import xelagurd.socialdating.ui.state.RegistrationUiState
import xelagurd.socialdating.ui.state.RequestStatus

@HiltViewModel
class RegistrationViewModel @Inject constructor(
    private val remoteRepository: RemoteUsersRepository,
    private val localRepository: LocalUsersRepository,
    private val preferencesRepository: PreferencesRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(RegistrationUiState())
    val uiState = _uiState.asStateFlow()

    fun updateUiState(registrationDetails: RegistrationDetails) {
        _uiState.update {
            it.copy(registrationDetails = registrationDetails)
        }
    }

    fun register() {
        viewModelScope.launch {
            try {
                _uiState.update { it.copy(requestStatus = RequestStatus.LOADING) }

                delay(FAKE_SERVER_LATENCY) // FixMe: remove after implementing server

                val registrationDetails = uiState.value.registrationDetails
                val user = remoteRepository.registerUser(
                    registrationDetails.copy(
                        password = BCrypt.hashpw(registrationDetails.password, BCrypt.gensalt())
                    )
                )

                if (user != null) {
                    localRepository.insertUser(user)
                    preferencesRepository.saveCurrentUserId(user.id)

                    _uiState.update { it.copy(requestStatus = RequestStatus.SUCCESS) }
                } else {
                    _uiState.update { it.copy(requestStatus = RequestStatus.FAILED) }
                }
            } catch (_: IOException) {
                localRepository.insertUser(FakeDataSource.users[0]) // TODO: remove after implementing server
                preferencesRepository.saveCurrentUserId(FakeDataSource.users[0].id) // TODO: remove after implementing server

                _uiState.update { it.copy(requestStatus = RequestStatus.SUCCESS) } // TODO: Change to ERROR after implementing server
            }
        }
    }
}