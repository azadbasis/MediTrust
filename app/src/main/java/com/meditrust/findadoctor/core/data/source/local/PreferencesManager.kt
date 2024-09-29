package com.meditrust.findadoctor.core.data.source.local

import android.content.Context
import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import timber.log.Timber
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton



@Singleton
class PreferencesManager @Inject constructor(@ApplicationContext context: Context) {

    private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "user_preferences")
    private val dataStore = context.dataStore

    val preferencesFlow: Flow<UserPreferences> = dataStore.data
        .catch { exception ->
            if (exception is IOException) {
                Timber.e(exception, "Error reading preferences")
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }
        .map { preferences ->
            UserPreferences(
                onBoardingCompleted = preferences[PreferencesKeys.ON_BOARDING_COMPLETED] ?: false,
                loginCompleted = preferences[PreferencesKeys.LOGIN_COMPLETED] ?: false
            )
        }

    suspend fun updateOnBoardingCompleted(completed: Boolean) {
        dataStore.edit { preferences ->
            preferences[PreferencesKeys.ON_BOARDING_COMPLETED] = completed
        }
    }

    suspend fun updateLoginCompleted(completed: Boolean) {
        dataStore.edit { preferences ->
            preferences[PreferencesKeys.LOGIN_COMPLETED] = completed
        }
    }

    private object PreferencesKeys {
        val ON_BOARDING_COMPLETED = booleanPreferencesKey("on_boarding_completed")
        val LOGIN_COMPLETED = booleanPreferencesKey("login_completed")
    }
}

data class UserPreferences(
    val onBoardingCompleted: Boolean,
    val loginCompleted: Boolean
)
