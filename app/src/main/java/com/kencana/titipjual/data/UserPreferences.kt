package com.kencana.titipjual.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore("app_preferences")

class UserPreferences @Inject constructor(@ApplicationContext context: Context) {

    val appContext = context.applicationContext

    private object Keys {
        val IS_LOGGED_IN = booleanPreferencesKey("key_is_login")
        val USER_ID = stringPreferencesKey("key_id_user")
        val TOKEN = stringPreferencesKey("key_access_token")
        val REFRESH_TOKEN = stringPreferencesKey("key_refresh_token")
    }

    val isLoggedIn: Flow<Boolean>
        get() = appContext.dataStore.data.map { prefs ->
            prefs[Keys.IS_LOGGED_IN] ?: false
        }

    val accessToken: Flow<String?>
        get() = appContext.dataStore.data.map { prefs ->
            prefs[Keys.TOKEN]
        }

    val refreshToken: Flow<String?>
        get() = appContext.dataStore.data.map { prefs ->
            prefs[Keys.REFRESH_TOKEN]
        }

    val userId: Flow<String?>
        get() = appContext.dataStore.data.map { prefs ->
            prefs[Keys.USER_ID]
        }

    suspend fun saveUserInfo(userId: String, authToken: String) {
        appContext.dataStore.edit { prefs ->
            prefs[Keys.IS_LOGGED_IN] = true
            prefs[Keys.TOKEN] = authToken
            prefs[Keys.USER_ID] = userId
        }
    }

    suspend fun saveAccessTokens(accessToken: String, refreshToken: String) {
        appContext.dataStore.edit { prefs ->
            prefs[Keys.TOKEN] = accessToken
            prefs[Keys.REFRESH_TOKEN] = refreshToken
        }
    }

    suspend fun clear() {
        appContext.dataStore.edit { preferences ->
            preferences.clear()
        }
    }
}
