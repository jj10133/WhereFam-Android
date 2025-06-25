package to.holepunch.bare.android.core.home

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Color
import android.location.Location
import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.core.graphics.createBitmap
import androidx.core.graphics.set
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.zxing.BarcodeFormat
import com.google.zxing.EncodeHintType
import com.google.zxing.qrcode.QRCodeWriter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put
import to.holepunch.bare.android.data.GenericAction
import to.holepunch.bare.android.data.ipc.IPCUtils.writeAsync
import to.holepunch.bare.android.processing.UserRepository
import to.holepunch.bare.kit.IPC
import java.nio.ByteBuffer
import java.nio.charset.Charset

class HomeViewModel(context: Context, private val ipc: IPC, private val userRepository: UserRepository) : ViewModel() {
    private val fileDir = context.filesDir


    val publicKey: StateFlow<String> = userRepository.currentPublicKey

    private val _qrCodeBitmap = MutableStateFlow<ImageBitmap?>(null)
    val qrCodeBitmap = _qrCodeBitmap.asStateFlow()

    val userLocation: MutableState<Location> = mutableStateOf(Location("gps"))

    init {
        viewModelScope.launch {
            publicKey.collectLatest { key ->
                Log.d("key", "Got the key $key")
                if (key.isNotEmpty()) {
                    generateAndSetQrCode(key)
                } else {
                    _qrCodeBitmap.value = null
                }
            }
        }
    }

    suspend fun start() {
        val dynamicData = buildJsonObject { put("path", fileDir.path) }
        val message = GenericAction(action = "start", data = dynamicData)

        val jsonString = Json.encodeToString(message) + "\n"

        val byteBuffer = ByteBuffer.wrap(jsonString.toByteArray(Charset.forName("UTF-8")))
        ipc.writeAsync(byteBuffer)
    }

    suspend fun fetchMaps() {
        val dynamicData = buildJsonObject { put("path", fileDir.path) }
        val message = GenericAction(action = "fetchMaps", data = dynamicData)

        val jsonString = Json.encodeToString(message) + "\n"

        val byteBuffer = ByteBuffer.wrap(jsonString.toByteArray(Charset.forName("UTF-8")))
        ipc.writeAsync(byteBuffer)
    }

    suspend fun requestPublicKey() {
        userRepository.requestPublicKey()
    }

    private fun generateAndSetQrCode(shareID: String) {
        viewModelScope.launch {
            val generatedBitmap = withContext(Dispatchers.Default) {
                val size = 512
                val hints = hashMapOf<EncodeHintType, Int>().also {
                    it[EncodeHintType.MARGIN] = 1
                }

                val bits = QRCodeWriter().encode(shareID, BarcodeFormat.QR_CODE, size, size, hints)
                val bitmap = createBitmap(size, size, Bitmap.Config.RGB_565).also {
                    for (x in 0 until size) {
                        for (y in 0 until size) {
                            it[x, y] = if (bits[x, y]) Color.BLACK else Color.WHITE
                        }
                    }
                }

                bitmap.asImageBitmap()
            }

            _qrCodeBitmap.value = generatedBitmap

        }
    }
}
