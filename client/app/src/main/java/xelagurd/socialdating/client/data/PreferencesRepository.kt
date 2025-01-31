package xelagurd.socialdating.client.data

import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.map
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey

@Singleton
class PreferencesRepository @Inject constructor(
    private val dataStore: DataStore<Preferences>
) {
    val currentUserId = dataStore.data.map { it[CURRENT_USER_ID] ?: -1 }

    suspend fun saveCurrentUserId(currentUserId: Int) {
        dataStore.edit { it[CURRENT_USER_ID] = currentUserId }
    }

    private companion object {
        val CURRENT_USER_ID = intPreferencesKey("current_user_id")
    }
}