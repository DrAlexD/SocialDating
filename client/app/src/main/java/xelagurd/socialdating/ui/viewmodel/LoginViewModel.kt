package xelagurd.socialdating.ui.viewmodel

import java.io.IOException
import javax.inject.Inject
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import android.util.Log
import androidx.credentials.GetCredentialResponse
import androidx.credentials.PasswordCredential
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import org.mindrot.jbcrypt.BCrypt
import xelagurd.socialdating.AccountManager
import xelagurd.socialdating.PreferencesRepository
import xelagurd.socialdating.data.fake.FAKE_SERVER_LATENCY
import xelagurd.socialdating.data.local.repository.LocalUsersRepository
import xelagurd.socialdating.data.model.additional.LoginDetails
import xelagurd.socialdating.data.remote.repository.RemoteUsersRepository
import xelagurd.socialdating.ui.state.LoginUiState
import xelagurd.socialdating.ui.state.RequestStatus

@HiltViewModel
class LoginViewModel @Inject constructor(
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

    fun tryToFindCredentialsAndLogin() {
        viewModelScope.launch {
            accountManager.findCredentials()?.let { loginWithCredentials(it) }
        }
    }

    suspend fun loginWithCredentials(credentialResponse: GetCredentialResponse) {
        when (val credential = credentialResponse.credential) {
            is PasswordCredential -> {
                val username = credential.id
                val password = credential.password

                loginUser(
                    loginDetails = LoginDetails(username, password),
                    isLoginWithInput = false
                )
            }

            else -> {
                Log.e(AccountManager::class.simpleName, "Unexpected type of credential")
            }
        }
    }

    fun updateUiState(loginDetails: LoginDetails) {
        _uiState.update {
            it.copy(loginDetails = loginDetails)
        }
    }

    fun loginWithInput() {
        viewModelScope.launch {
            loginUser(
                loginDetails = uiState.value.loginDetails,
                isLoginWithInput = true
            )
        }
    }

    suspend fun loginUser(loginDetails: LoginDetails, isLoginWithInput: Boolean) {
        try {
            _uiState.update { it.copy(requestStatus = RequestStatus.LOADING) }

            delay(FAKE_SERVER_LATENCY) // FixMe: remove after implementing server

            val user = remoteRepository.loginUser(
                loginDetails.copy(
                    password = BCrypt.hashpw(loginDetails.password, BCrypt.gensalt())
                )
            )

            if (user != null) {
                if (isLoginWithInput) {
                    accountManager.saveCredentials(loginDetails)
                }

                localRepository.insertUser(user)
                preferencesRepository.saveCurrentUserId(user.id)

                _uiState.update { it.copy(requestStatus = RequestStatus.SUCCESS) }
            } else {
                _uiState.update { it.copy(requestStatus = RequestStatus.FAILED) }
            }
        } catch (_: IOException) {
            _uiState.update { it.copy(requestStatus = RequestStatus.ERROR) }
        }
    }
}