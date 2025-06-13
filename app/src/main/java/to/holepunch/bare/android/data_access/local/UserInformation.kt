package to.holepunch.bare.android.data_access.local

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Base64
import android.util.Log
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import java.io.ByteArrayOutputStream
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class UserInfomation(private val context: Context) {

    val Context.userPreferencesDataStore: DataStore<Preferences> by
            preferencesDataStore(name = "user_info")

    companion object {
        private val USER_NAME = stringPreferencesKey("user_name")
        private val USER_IMAGE_DATA_KEY = stringPreferencesKey("user_image_data")
    }

    val userImageDataFlow: Flow<String?> =
            context.userPreferencesDataStore.data.map { preferences ->
                preferences[USER_IMAGE_DATA_KEY]
            }

    val userName =
            context.userPreferencesDataStore.data.map { perferences -> perferences[USER_NAME] }

    suspend fun saveUserImage(bitmap: Bitmap?) {
        context.userPreferencesDataStore.edit { preferences ->
            if (bitmap != null) {
                val outputStream = ByteArrayOutputStream()
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
                val base64String = Base64.encodeToString(outputStream.toByteArray(), Base64.DEFAULT)
                preferences[USER_IMAGE_DATA_KEY] = base64String
                Log.d("UserPreferencesRepo", "Image saved to DataStore (Base64)")
            } else {
                preferences.remove(USER_IMAGE_DATA_KEY)
                Log.d("UserPreferencesRepo", "Image removed from DataStore")
            }
        }
    }

    fun decodeBase64ToBitmap(base64String: String): ImageBitmap? {
        return try {
            val decodedBytes = Base64.decode(base64String, Base64.DEFAULT)
            BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.size)?.asImageBitmap()
        } catch (e: IllegalArgumentException) {
            Log.e("UserPreferencesRepo", "Error decoding base64 image: ${e.message}")
            null
        }
    }
}
