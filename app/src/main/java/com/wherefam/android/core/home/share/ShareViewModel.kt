package com.wherefam.android.core.home.share

import android.graphics.Bitmap
import android.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.core.graphics.createBitmap
import androidx.core.graphics.set
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.zxing.BarcodeFormat
import com.google.zxing.EncodeHintType
import com.google.zxing.qrcode.QRCodeWriter
import com.wherefam.android.data.UserRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ShareViewModel(private val userRepository: UserRepository) : ViewModel() {
    val publicKey: StateFlow<String> = userRepository.currentPublicKey

    private val _qrCodeBitmap = MutableStateFlow<ImageBitmap?>(null)
    val qrCodeBitmap = _qrCodeBitmap.asStateFlow()

    init {
        viewModelScope.launch {
            publicKey.collectLatest { key ->
                if (key.isNotEmpty()) {
                    generateAndSetQrCode(key)
                } else {
                    _qrCodeBitmap.value = null
                }
            }
        }
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