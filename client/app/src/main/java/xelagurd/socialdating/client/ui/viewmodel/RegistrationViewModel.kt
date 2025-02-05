package xelagurd.socialdating.client.ui.viewmodel

import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import org.mindrot.jbcrypt.BCrypt
import xelagurd.socialdating.client.data.AccountManager
import xelagurd.socialdating.client.data.PreferencesRepository
import xelagurd.socialdating.client.data.fake.FakeDataSource
import xelagurd.socialdating.client.data.local.repository.LocalUsersRepository
import xelagurd.socialdating.client.data.model.details.LoginDetails
import xelagurd.socialdating.client.data.model.details.RegistrationDetails
import xelagurd.socialdating.client.data.remote.repository.RemoteUsersRepository
import xelagurd.socialdating.client.data.safeApiCall
import xelagurd.socialdating.client.ui.state.RegistrationUiState
import xelagurd.socialdating.client.ui.state.RequestStatus

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
            _uiState.update { it.copy(actionRequestStatus = RequestStatus.LOADING) }

            val registrationDetails = uiState.value.formDetails
            val encodedRegistrationDetails = registrationDetails.copy(
                password = BCrypt.hashpw(registrationDetails.password, BCrypt.gensalt())
            )

            val (user, status) = safeApiCall(context) {
                remoteRepository.registerUser(encodedRegistrationDetails)
            }

            if (user != null) {
                accountManager.saveCredentials(registrationDetails.toLoginDetails())

                localRepository.insertUser(user)
                preferencesRepository.saveCurrentUserId(user.id)

                _uiState.update { it.copy(actionRequestStatus = status) } // TODO: Move outside after implementing server
            } else { // TODO: remove after implementing server
                accountManager.saveCredentials(
                    LoginDetails(
                        FakeDataSource.users[0].username,
                        FakeDataSource.users[0].password
                    )
                )

                if (!localRepository.getUsers().first().map { it.id }
                        .contains(FakeDataSource.users[0].id)) {
                    localRepository.insertUser(FakeDataSource.users[0])
                }

                preferencesRepository.saveCurrentUserId(FakeDataSource.users[0].id)

                _uiState.update { it.copy(actionRequestStatus = RequestStatus.SUCCESS) }
            }
        }
    }
}