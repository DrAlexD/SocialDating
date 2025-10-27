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
    @param:ApplicationContext private val context: Context,
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

            var (authResponse, status) = safeApiCall(context) {
                remoteRepository.registerUser(registrationDetails)
            }

            if (authResponse != null) {
                accountManager.saveCredentials(registrationDetails.toLoginDetails())

                localRepository.insertUser(authResponse.user)
                preferencesRepository.saveAccessToken(authResponse.accessToken)
                preferencesRepository.saveRefreshToken(authResponse.refreshToken)
                preferencesRepository.saveCurrentUserId(authResponse.user.id)
            }

            if (status is RequestStatus.ERROR) { // FixMe: remove after implementing server
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

                status = RequestStatus.SUCCESS
            }

            _uiState.update { it.copy(actionRequestStatus = status) }
        }
    }
}