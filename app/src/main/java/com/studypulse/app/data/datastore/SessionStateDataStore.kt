package com.studypulse.app.data.datastore

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import com.studypulse.app.domain.model.SessionState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SessionStateDataStore @Inject constructor(
    private val dataStore: DataStore<Preferences>
) {
    private object Keys {
        val IS_STUDYING      = booleanPreferencesKey("is_studying")
        val LOCATION_ID      = stringPreferencesKey("location_id")
        val SESSION_START_MS = longPreferencesKey("session_start_ms")
    }

    fun getSessionState(): Flow<SessionState> = dataStore.data.map { prefs ->
        if (prefs[Keys.IS_STUDYING] == true) {
            SessionState.Studying(
                locationId = prefs[Keys.LOCATION_ID] ?: "CLASS_A",
                startTime  = prefs[Keys.SESSION_START_MS] ?: 0L
            )
        } else {
            SessionState.Idle
        }
    }

    suspend fun saveStudyingState(locationId: String, startMs: Long) {
        dataStore.edit { prefs ->
            prefs[Keys.IS_STUDYING]      = true
            prefs[Keys.LOCATION_ID]      = locationId
            prefs[Keys.SESSION_START_MS] = startMs
        }
    }

    suspend fun clearState() {
        dataStore.edit { prefs ->
            prefs.remove(Keys.IS_STUDYING)
            prefs.remove(Keys.LOCATION_ID)
            prefs.remove(Keys.SESSION_START_MS)
        }
    }
}
