package com.example.finalpro.Data.Local

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.runBlocking
import javax.inject.Inject
import javax.inject.Singleton

val Context.dataStore: DataStore<Preferences> by preferencesDataStore("session")

@Singleton
class SessionManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val TOKEN_KEY   = stringPreferencesKey("jwt_token")
    private val EMAIL_KEY   = stringPreferencesKey("user_email")
    private val MONEDA_KEY  = stringPreferencesKey("moneda_pref")

    suspend fun saveSession(token: String, email: String) {
        context.dataStore.edit { prefs ->
            prefs[TOKEN_KEY]  = token
            prefs[EMAIL_KEY]  = email
        }
    }

    fun getToken(): String? = runBlocking {
        context.dataStore.data.first()[TOKEN_KEY]
    }

    fun getEmail(): Flow<String?> = context.dataStore.data.map { it[EMAIL_KEY] }

    suspend fun saveMoneda(moneda: String) {
        context.dataStore.edit { it[MONEDA_KEY] = moneda }
    }

    fun getMoneda(): Flow<String> = context.dataStore.data.map { it[MONEDA_KEY] ?: "COP" }

    suspend fun clearSession() {
        context.dataStore.edit { it.clear() }
    }

    fun isLoggedIn(): Boolean = getToken() != null
}