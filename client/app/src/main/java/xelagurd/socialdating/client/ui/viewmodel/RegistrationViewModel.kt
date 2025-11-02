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
import xelagurd.socialdating.client.data.AccountManager
import xelagurd.socialdating.client.data.PreferencesRepository
import xelagurd.socialdating.client.data.fake.FakeData
import xelagurd.socialdating.client.data.local.repository.LocalUsersRepository
import xelagurd.socialdating.client.data.remote.repository.RemoteUsersRepository
import xelagurd.socialdating.client.data.remote.safeApiCall
import xelagurd.socialdating.client.ui.form.LoginFormData
import xelagurd.socialdating.client.ui.form.RegistrationFormData
import xelagurd.socialdating.client.ui.state.RegistrationUiState
import xelagurd.socialdating.client.ui.state.RequestStatus

@HiltViewModel
class RegistrationViewModel @Inject constructor(
    @param:ApplicationContext private val context: Context,
    private val remoteRepository: RemoteUsersRepository,
    private val localRepository: LocalUsersRepository,
    private val preferencesRepository: PreferencesRepository,
    private val accountManager: AccountManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(RegistrationUiState())
    val uiState = _uiState.asStateFlow()

    fun updateUiState(registrationFormData: RegistrationFormData) {
        _uiState.update {
            it.copy(formData = registrationFormData)
        }
    }

    fun register() {
        viewModelScope.launch {
            _uiState.update { it.copy(actionRequestStatus = RequestStatus.LOADING) }

            val registrationFormData = uiState.value.formData

            var (authResponse, status) = safeApiCall(context) {
                remoteRepository.registerUser(registrationFormData.toRegistrationDetails())
            }

            if (authResponse != null) {
                accountManager.saveCredentials(registrationFormData.toLoginFormData())

                localRepository.insertUser(authResponse.user)
                preferencesRepository.saveAccessToken(authResponse.accessToken)
                preferencesRepository.saveRefreshToken(authResponse.refreshToken)
                preferencesRepository.saveCurrentUserId(authResponse.user.id)
            }

            if (status is RequestStatus.ERROR) { // FixMe: remove after adding server hosting
                accountManager.saveCredentials(
                    LoginFormData(
                        FakeData.users[0].username,
                        FakeData.users[0].password
                    )
                )

                if (!localRepository.getUsers().first().map { it.id }
                        .contains(FakeData.users[0].id)) {
                    localRepository.insertUser(FakeData.users[0])
                }

                preferencesRepository.saveCurrentUserId(FakeData.users[0].id)

                status = RequestStatus.SUCCESS
            }

            _uiState.update { it.copy(actionRequestStatus = status) }
        }
    }
}