package com.wherefam.android.data.local

import android.content.Context
import android.graphics.Bitmap
import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.core.IOException
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream

val Context.userPreferencesDataStore: DataStore<Preferences> by preferencesDataStore(name = "user_info")

class DataStoreRepository(private val context: Context) {
    private companion object {
        private val USER_NAME = stringPreferencesKey("user_name")
        private const val USER_IMAGE_FILE_NAME = "user_profile_image.png"
        private val ONBOARDING_COMPLETED = booleanPreferencesKey("onboarding_completed")
    }

    suspend fun saveUserImage(bitmap: Bitmap?) {
        withContext(Dispatchers.IO) {
            try {
                val file = File(context.filesDir, USER_IMAGE_FILE_NAME)

                if (bitmap != null) {
                    FileOutputStream(file).use { out ->
                        bitmap.compress(Bitmap.CompressFormat.PNG, 100, out)
                    }
                    Log.d("PrefUtils", "Image saved to internal storage: ${file.absolutePath}")
                }
            } catch (e: Exception) {
                Log.e("PrefUtils", "Error saving image to internal storage: ${e.message}", e)
            }
        }
    }

    suspend fun saveUserName(value: String) {
        context.userPreferencesDataStore.edit { it[USER_NAME] = value }
    }

    suspend fun saveOnboardingState(completed: Boolean) {
        context.userPreferencesDataStore.edit { preferences ->
            preferences[ONBOARDING_COMPLETED] = completed
        }
    }

    fun readOnBoardingState(): Flow<Boolean> {
        return context.userPreferencesDataStore.data
            .catch { exception ->
                if (exception is IOException) {
                    emit(emptyPreferences())
                } else {
                    throw exception
                }
            }
            .map { preferences ->
                val onBoardingState = preferences[ONBOARDING_COMPLETED] ?: false
                onBoardingState
            }
    }

    suspend fun getUserName(): String {
        return context.userPreferencesDataStore.data.first()[USER_NAME] ?: ""
    }
}
