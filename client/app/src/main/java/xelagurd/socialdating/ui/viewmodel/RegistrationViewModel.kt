package xelagurd.socialdating.ui.viewmodel

import java.io.IOException
import javax.inject.Inject
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import org.mindrot.jbcrypt.BCrypt
import xelagurd.socialdating.AccountManager
import xelagurd.socialdating.PreferencesRepository
import xelagurd.socialdating.R
import xelagurd.socialdating.data.fake.FAKE_SERVER_LATENCY
import xelagurd.socialdating.data.fake.FakeDataSource
import xelagurd.socialdating.data.local.repository.LocalUsersRepository
import xelagurd.socialdating.data.model.details.LoginDetails
import xelagurd.socialdating.data.model.details.RegistrationDetails
import xelagurd.socialdating.data.remote.repository.RemoteUsersRepository
import xelagurd.socialdating.ui.state.RegistrationUiState
import xelagurd.socialdating.ui.state.RequestStatus

@HiltViewModel
class RegistrationViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val remoteRepository: RemoteUsersRepository,
    private val localRepository: LocalUsersRepository,
    private val preferencesRepository: PreferencesRepository,
    private val accountManager: AccountManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(RegistrationUiState())
    val uiState = _uiState.asStateFlow()

    fun updateUiState(registrationDetails: RegistrationDetails) {
        _uiState.update {
            it.copy(formDetails = registrationDetails)
        }
    }

    fun register() {
        viewModelScope.launch {
            try {
                _uiState.update { it.copy(actionRequestStatus = RequestStatus.LOADING) }

                delay(FAKE_SERVER_LATENCY) // FixMe: remove after implementing server

                val registrationDetails = uiState.value.formDetails
                val user = remoteRepository.registerUser(
                    registrationDetails.copy(
                        password = BCrypt.hashpw(registrationDetails.password, BCrypt.gensalt())
                    )
                )

                if (user != null) {
                    accountManager.saveCredentials(registrationDetails.toLoginDetails())

                    localRepository.insertUser(user)
                    preferencesRepository.saveCurrentUserId(user.id)

                    _uiState.update { it.copy(actionRequestStatus = RequestStatus.SUCCESS) }
                } else {
                    _uiState.update {
                        it.copy(
                            actionRequestStatus = RequestStatus.FAILURE(
                                failureText = context.getString(R.string.failed_registration)
                            )
                        )
                    }
                }
            } catch (_: IOException) {
                accountManager.saveCredentials(
                    LoginDetails(
                        FakeDataSource.users[0].username,
                        FakeDataSource.users[0].password
                    )
                ) // TODO: remove after implementing server

                localRepository.insertUser(FakeDataSource.users[0]) // TODO: remove after implementing server
                preferencesRepository.saveCurrentUserId(FakeDataSource.users[0].id) // TODO: remove after implementing server

                _uiState.update { it.copy(actionRequestStatus = RequestStatus.SUCCESS) } // TODO: Change to ERROR after implementing server
            }
        }
    }
}