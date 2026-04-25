package xelagurd.socialdating.client.data

import javax.inject.Inject
import javax.inject.Singleton
import android.content.Context
import androidx.credentials.CreatePasswordRequest
import androidx.credentials.CredentialManager
import androidx.credentials.GetCredentialRequest
import androidx.credentials.GetCredentialResponse
import androidx.credentials.GetPasswordOption
import androidx.credentials.exceptions.CreateCredentialException
import androidx.credentials.exceptions.GetCredentialException
import dagger.hilt.android.qualifiers.ApplicationContext
import xelagurd.socialdating.client.ui.form.LoginFormData

@Singleton
class AccountManager @Inject constructor(
    @param:ApplicationContext private val context: Context,
    private val credentialManager: CredentialManager
) {

    suspend fun findCredentials(): GetCredentialResponse? {
        try {
            val getPasswordOption = GetPasswordOption()
            val request = GetCredentialRequest(
                credentialOptions = listOf(getPasswordOption),
                preferImmediatelyAvailableCredentials = true
            )

            return credentialManager.getCredential(context, request)
        } catch (_: GetCredentialException) {
        }

        return null
    }

    suspend fun saveCredentials(loginFormData: LoginFormData) {
        try {
            val request = CreatePasswordRequest(
                id = loginFormData.username,
                password = loginFormData.password
            )

            credentialManager.createCredential(context, request)
        } catch (_: CreateCredentialException) {
        }
    }
}