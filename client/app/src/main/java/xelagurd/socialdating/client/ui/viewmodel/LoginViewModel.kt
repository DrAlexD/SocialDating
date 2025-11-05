package xelagurd.socialdating.client.ui.viewmodel

import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import android.content.Context
import android.util.Log
import androidx.credentials.GetCredentialResponse
import androidx.credentials.PasswordCredential
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
import xelagurd.socialdating.client.ui.state.LoginUiState
import xelagurd.socialdating.client.ui.state.RequestStatus

@HiltViewModel
class LoginViewModel @Inject constructor(
    @param:ApplicationContext private val context: Context,
    private val remoteRepository: RemoteUsersRepository,
    private val localRepository: LocalUsersRepository,
    private val preferencesRepository: PreferencesRepository,
    private val accountManager: AccountManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState = _uiState.asStateFlow()

    init {
        tryToFindCredentialsAndLogin()
    }

    private fun tryToFindCredentialsAndLogin() {
        viewModelScope.launch {
            accountManager.findCredentials()?.let { loginWithCredentials(it) }
        }
    }

    private suspend fun loginWithCredentials(credentialResponse: GetCredentialResponse) {
        when (val credential = credentialResponse.credential) {
            is PasswordCredential -> {
                val username = credential.id
                val password = credential.password

                loginUser(
                    loginFormData = LoginFormData(username, password),
                    isLoginWithInput = false
                )
            }

            else -> {
                Log.e(AccountManager::class.simpleName, "Unexpected type of credential")
            }
        }
    }

    fun updateUiState(loginFormData: LoginFormData) {
        _uiState.update {
            it.copy(formData = loginFormData)
        }
    }

    fun loginWithInput() {
        viewModelScope.launch {
            loginUser(
                loginFormData = uiState.value.formData,
                isLoginWithInput = true
            )
        }
    }

    private suspend fun loginUser(loginFormData: LoginFormData, isLoginWithInput: Boolean) {
        _uiState.update { it.copy(actionRequestStatus = RequestStatus.LOADING) }

        var (authResponse, status) = safeApiCall(context) {
            remoteRepository.loginUser(loginFormData.toLoginDetails())
        }

        if (authResponse != null) {
            if (isLoginWithInput) {
                accountManager.saveCredentials(loginFormData)
            }

            localRepository.insertUser(authResponse.user)
            preferencesRepository.saveAccessToken(authResponse.accessToken)
            preferencesRepository.saveRefreshToken(authResponse.refreshToken)
            preferencesRepository.saveCurrentUserId(authResponse.user.id)
        }

        if (status is RequestStatus.ERROR) { // FixMe: remove after adding server hosting
            accountManager.saveCredentials(LoginFormData(FakeData.mainUser.username, "password1"))

            if (!localRepository.getUsers().first().map { it.id }.contains(FakeData.mainUser.id)) {
                localRepository.insertUser(FakeData.mainUser)
            }

            preferencesRepository.saveCurrentUserId(FakeData.mainUser.id)

            status = RequestStatus.SUCCESS
        }

        _uiState.update { it.copy(actionRequestStatus = status) }
    }
}