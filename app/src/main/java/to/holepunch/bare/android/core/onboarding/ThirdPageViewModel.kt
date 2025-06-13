package to.holepunch.bare.android.core.onboarding

import android.app.Application
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import to.holepunch.bare.android.data_access.local.PrefUtils

class ThirdPageViewModel(application: Application, private val prefUtils: PrefUtils) : AndroidViewModel(application) {

    var userImage: ImageBitmap? by mutableStateOf(null)
        private set

    fun loadImageFromUri(uri: Uri?) {
        if (uri == null) {
            userImage = null
            viewModelScope.launch { prefUtils.saveUserImage(null) }
            return
        }

        viewModelScope.launch(Dispatchers.IO) {
            val contentResolver = getApplication<Application>().contentResolver
            try {
                val bitmap: Bitmap? = ImageDecoder.decodeBitmap(ImageDecoder.createSource(contentResolver, uri))

                withContext(Dispatchers.Main) {
                    userImage = bitmap?.asImageBitmap()
                    Log.d("ThirdPageViewModel", "Image loaded from URI and saved")
                }

                prefUtils.saveUserImage(bitmap)
            } catch (e: Exception) {
                Log.e("ThirdPageViewModel", "Error loading image from URI: ${e.message}", e)
                userImage = null
                viewModelScope.launch { prefUtils.saveUserImage(null) }
            }
        }
    }
}
