package xelagurd.socialdating.client.ui.viewmodel

import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import xelagurd.socialdating.client.data.AccountManager
import xelagurd.socialdating.client.data.PreferencesRepository
import xelagurd.socialdating.client.data.local.repository.LocalUsersRepository
import xelagurd.socialdating.client.data.remote.ApiUtils.safeApiCall
import xelagurd.socialdating.client.data.remote.repository.RemoteUsersRepository
import xelagurd.socialdating.client.ui.form.RegistrationFormData
import xelagurd.socialdating.client.ui.state.RegistrationUiState
import xelagurd.socialdating.client.ui.state.RequestStatus

@HiltViewModel
class RegistrationViewModel @Inject constructor(
    @param:ApplicationContext private val context: Context,
    private val accountManager: AccountManager,
    private val preferencesRepository: PreferencesRepository,
    private val remoteUsersRepository: RemoteUsersRepository,
    private val localUsersRepository: LocalUsersRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(RegistrationUiState())
    val uiState = _uiState.asStateFlow()

    fun updateUiState(registrationFormData: RegistrationFormData) =
        _uiState.update {
            it.copy(formData = registrationFormData)
        }

    fun register() {
        viewModelScope.launch {
            _uiState.update { it.copy(actionRequestStatus = RequestStatus.LOADING) }

            val registrationFormData = uiState.value.formData

            val (authResponse, status) = safeApiCall(context) {
                remoteUsersRepository.registerUser(registrationFormData.toRegistrationDetails())
            }

            if (authResponse != null) {
                accountManager.saveCredentials(registrationFormData.toLoginFormData())

                localUsersRepository.insertUser(authResponse.user)
                preferencesRepository.saveAccessToken(authResponse.accessToken)
                preferencesRepository.saveRefreshToken(authResponse.refreshToken)
                preferencesRepository.saveCurrentUserId(authResponse.user.id)
            }

            _uiState.update { it.copy(actionRequestStatus = status) }
        }
    }
}