package xelagurd.socialdating.client.data

import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.map
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey

@Singleton
class PreferencesRepository @Inject constructor(
    private val dataStore: DataStore<Preferences>
) {
    val currentUserId = dataStore.data.map { it[CURRENT_USER_ID] ?: -1 }
    val accessToken = dataStore.data.map { it[ACCESS_TOKEN] ?: "" }
    val refreshToken = dataStore.data.map { it[REFRESH_TOKEN] ?: "" }

    suspend fun saveCurrentUserId(currentUserId: Int) {
        dataStore.edit { it[CURRENT_USER_ID] = currentUserId }
    }

    suspend fun saveAccessToken(accessToken: String) {
        dataStore.edit { it[ACCESS_TOKEN] = accessToken }
    }

    suspend fun saveRefreshToken(refreshToken: String) {
        dataStore.edit { it[REFRESH_TOKEN] = refreshToken }
    }

    suspend fun clearTokens() {
        dataStore.edit {
            it.remove(ACCESS_TOKEN)
            it.remove(REFRESH_TOKEN)
        }
    }

    private companion object {
        val CURRENT_USER_ID = intPreferencesKey("current_user_id")
        val ACCESS_TOKEN = stringPreferencesKey("access_token")
        val REFRESH_TOKEN = stringPreferencesKey("refresh_token")
    }
}