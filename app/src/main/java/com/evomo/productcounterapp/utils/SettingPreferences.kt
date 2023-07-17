package com.evomo.productcounterapp.utils

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class SettingPreferences private constructor(private val dataStore: DataStore<Preferences>) {
    private val TOKEN_KEY = stringPreferencesKey("token")
    private val NAME_KEY = stringPreferencesKey("name")
    private val USERNAME_KEY = stringPreferencesKey("username")
    private val UID_KEY = stringPreferencesKey("uid")
    private val EXPIRED_KEY = stringPreferencesKey("expired")

    fun getUserToken(): Flow<String> = dataStore.data.map { it[TOKEN_KEY] ?: "Not Set" }
    fun getName(): Flow<String> = dataStore.data.map { it[NAME_KEY] ?: "Not Set" }
    fun getUserName(): Flow<String> = dataStore.data.map { it[USERNAME_KEY] ?: "Not Set" }
    fun getUID(): Flow<String> = dataStore.data.map { it[UID_KEY] ?: "Not Set" }
    fun getExpired(): Flow<String> = dataStore.data.map { it[EXPIRED_KEY] ?: "Not Set" }

    suspend fun saveLoginSession(userToken: String, userName:String, userUsername: String, userUID: String, expiredAt: String) {
        dataStore.edit { preferences ->
            preferences[TOKEN_KEY] = userToken
            preferences[NAME_KEY] = userName
            preferences[USERNAME_KEY] = userUsername
            preferences[UID_KEY] = userUID
            preferences[EXPIRED_KEY] = expiredAt
        }
    }

    suspend fun deleteLoginSession() {
        dataStore.edit { preferences ->
            preferences.clear()
        }
    }

    companion object {
        @Volatile
        private var INSTANCE: SettingPreferences? = null

        fun getInstance(dataStore: DataStore<Preferences>): SettingPreferences {
            return INSTANCE ?: synchronized(this) {
                val instance = SettingPreferences(dataStore)
                INSTANCE = instance
                instance
            }
        }
    }
}