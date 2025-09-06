package com.janad.zerodrop.data

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore(name = "user_prefs")

class UserPreferences(private val context: Context) {

    companion object {
        val TOKEN_KEY = stringPreferencesKey("auth_token")
        val USERNAME_KEY = stringPreferencesKey("username")
    }

    // Save login info (username + token)
    suspend fun saveLoginInfo(username: String, token: String) {
        context.dataStore.edit { prefs ->
            prefs[USERNAME_KEY] = username
            prefs[TOKEN_KEY] = token
        }
    }

    // Get saved username
    //You can implement it later
    val usernameFlow: Flow<String?> = context.dataStore.data.map { prefs ->
        prefs[USERNAME_KEY]
    }

    // Get saved token
    val tokenFlow: Flow<String?> = context.dataStore.data.map { prefs ->
        prefs[TOKEN_KEY]
    }
    suspend fun getToken(): String? {
        return tokenFlow.first()
    }

    // Clear login info (logout)
    suspend fun clearLoginInfo() {
        context.dataStore.edit { prefs ->
            prefs.clear()
        }
    }
}
