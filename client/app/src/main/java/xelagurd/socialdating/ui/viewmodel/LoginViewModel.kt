package xelagurd.socialdating.ui.viewmodel

import java.io.IOException
import java.lang.Exception
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
import retrofit2.HttpException
import xelagurd.socialdating.AccountManager
import xelagurd.socialdating.PreferencesRepository
import xelagurd.socialdating.R
import xelagurd.socialdating.data.fake.FakeDataSource
import xelagurd.socialdating.data.local.repository.LocalUsersRepository
import xelagurd.socialdating.data.model.details.LoginDetails
import xelagurd.socialdating.data.remote.repository.RemoteUsersRepository
import xelagurd.socialdating.ui.state.LoginUiState
import xelagurd.socialdating.ui.state.RequestStatus

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

    suspend fun loginUser(loginDetails: LoginDetails, isLoginWithInput: Boolean) {
        try {
            _uiState.update { it.copy(actionRequestStatus = RequestStatus.LOADING) }

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

                _uiState.update { it.copy(actionRequestStatus = RequestStatus.SUCCESS) }
            } else {
                _uiState.update {
                    it.copy(
                        actionRequestStatus = RequestStatus.FAILURE(
                            failureText = context.getString(R.string.failed_login)
                        )
                    )
                }
            }
        } catch (e: Exception) {
            when (e) {
                is IOException, is HttpException -> {
                    accountManager.saveCredentials(
                        LoginDetails(
                            FakeDataSource.users[0].username,
                            FakeDataSource.users[0].password
                        )
                    ) // TODO: remove after implementing server

                    if (!localRepository.getUsers().first().map { it.id }
                            .contains(FakeDataSource.users[0].id)) {
                        localRepository.insertUser(FakeDataSource.users[0]) // TODO: remove after implementing server
                    }

                    preferencesRepository.saveCurrentUserId(FakeDataSource.users[0].id) // TODO: remove after implementing server

                    _uiState.update { it.copy(actionRequestStatus = RequestStatus.SUCCESS) } // TODO: Change to ERROR after implementing server
                }

                else -> throw e
            }
        }
    }
}