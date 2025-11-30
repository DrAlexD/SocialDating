package xelagurd.socialdating.client.data

import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.map
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey

@Singleton
class PreferencesRepository @Inject constructor(
    private val dataStore: DataStore<Preferences>
) {
    val currentUserId = dataStore.data.map { it[CURRENT_USER_ID] ?: CURRENT_USER_ID_DEFAULT }
    val accessToken = dataStore.data.map { it[ACCESS_TOKEN] ?: ACCESS_TOKEN_DEFAULT }
    val refreshToken = dataStore.data.map { it[REFRESH_TOKEN] ?: REFRESH_TOKEN_DEFAULT }
    val isOfflineMode = dataStore.data.map { it[IS_OFFLINE_MODE] ?: IS_OFFLINE_MODE_DEFAULT }

    suspend fun saveCurrentUserId(currentUserId: Int) {
        dataStore.edit { it[CURRENT_USER_ID] = currentUserId }
    }

    suspend fun saveAccessToken(accessToken: String) {
        dataStore.edit { it[ACCESS_TOKEN] = accessToken }
    }

    suspend fun saveRefreshToken(refreshToken: String) {
        dataStore.edit { it[REFRESH_TOKEN] = refreshToken }
    }

    suspend fun saveIsOfflineMode(isOfflineMode: Boolean) { // FixMe: remove after adding server hosting
        dataStore.edit { it[IS_OFFLINE_MODE] = isOfflineMode }
    }

    suspend fun clearPreferences() {
        dataStore.edit {
            it[CURRENT_USER_ID] = CURRENT_USER_ID_DEFAULT
            it[ACCESS_TOKEN] = ACCESS_TOKEN_DEFAULT
            it[REFRESH_TOKEN] = REFRESH_TOKEN_DEFAULT
            it[IS_OFFLINE_MODE] = IS_OFFLINE_MODE_DEFAULT
        }
    }

    companion object Defaults {
        private val CURRENT_USER_ID = intPreferencesKey("current_user_id")
        private val ACCESS_TOKEN = stringPreferencesKey("access_token")
        private val REFRESH_TOKEN = stringPreferencesKey("refresh_token")
        private val IS_OFFLINE_MODE = booleanPreferencesKey("is_offline_mode")
        const val CURRENT_USER_ID_DEFAULT = -1
        const val ACCESS_TOKEN_DEFAULT = ""
        const val REFRESH_TOKEN_DEFAULT = ""
        const val IS_OFFLINE_MODE_DEFAULT = false
    }
}