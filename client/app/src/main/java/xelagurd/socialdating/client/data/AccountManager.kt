package xelagurd.socialdating.client.data

import javax.inject.Inject
import javax.inject.Singleton
import android.content.Context
import android.util.Log
import androidx.credentials.CreatePasswordRequest
import androidx.credentials.CredentialManager
import androidx.credentials.GetCredentialRequest
import androidx.credentials.GetCredentialResponse
import androidx.credentials.GetPasswordOption
import androidx.credentials.exceptions.CreateCredentialCancellationException
import androidx.credentials.exceptions.CreateCredentialException
import androidx.credentials.exceptions.GetCredentialCancellationException
import androidx.credentials.exceptions.GetCredentialException
import androidx.credentials.exceptions.NoCredentialException
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
        } catch (_: GetCredentialCancellationException) {
            Log.i(AccountManager::class.simpleName, "Canceled credentials check")
        } catch (_: NoCredentialException) {
            Log.i(AccountManager::class.simpleName, "No credentials available")
        } catch (e: GetCredentialException) {
            Log.e(AccountManager::class.simpleName, "Exception during credentials check", e)
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
        } catch (_: CreateCredentialCancellationException) {
            Log.i(AccountManager::class.simpleName, "Canceled credentials creation")
        } catch (e: CreateCredentialException) {
            Log.e(AccountManager::class.simpleName, "Exception during credentials creation", e)
        }
    }
}