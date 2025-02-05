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
import org.mindrot.jbcrypt.BCrypt
import xelagurd.socialdating.client.data.AccountManager
import xelagurd.socialdating.client.data.PreferencesRepository
import xelagurd.socialdating.client.data.fake.FakeDataSource
import xelagurd.socialdating.client.data.local.repository.LocalUsersRepository
import xelagurd.socialdating.client.data.model.details.LoginDetails
import xelagurd.socialdating.client.data.remote.repository.RemoteUsersRepository
import xelagurd.socialdating.client.data.safeApiCall
import xelagurd.socialdating.client.ui.state.LoginUiState
import xelagurd.socialdating.client.ui.state.RequestStatus

@HiltViewModel
class LoginViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
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
            it.copy(formDetails = loginDetails)
        }
    }

    fun loginWithInput() {
        viewModelScope.launch {
            loginUser(
                loginDetails = uiState.value.formDetails,
                isLoginWithInput = true
            )
        }
    }

    private suspend fun loginUser(loginDetails: LoginDetails, isLoginWithInput: Boolean) {
        _uiState.update { it.copy(actionRequestStatus = RequestStatus.LOADING) }

        val encodedLoginDetails = loginDetails.copy(
            password = BCrypt.hashpw(loginDetails.password, BCrypt.gensalt())
        )

        val (user, status) = safeApiCall(context) {
            remoteRepository.loginUser(encodedLoginDetails)
        }

        if (user != null) {
            if (isLoginWithInput) {
                accountManager.saveCredentials(loginDetails)
            }

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