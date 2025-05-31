package to.holepunch.bare.android.core.home

import android.graphics.Bitmap
import android.graphics.Color
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.core.graphics.createBitmap
import androidx.core.graphics.set
import com.google.zxing.BarcodeFormat
import com.google.zxing.EncodeHintType
import com.google.zxing.qrcode.QRCodeWriter

@Composable
fun ShareIDView(homeViewModel: HomeViewModel) {
    var qrCodeBitmap by remember { mutableStateOf<ImageBitmap?>(null) }
    var publicKey by homeViewModel.publicKey

    LaunchedEffect(Unit) {
        if (publicKey.isEmpty()) {
            homeViewModel.requestPublicKey()
        }
    }

    LaunchedEffect(publicKey) {
        if (publicKey.isNotEmpty()) {
            qrCodeBitmap = generateQrCode(publicKey)
        }
    }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        qrCodeBitmap?.let {
            Image(bitmap = it, contentDescription = "Share QR Code")
        }
    }
}

fun generateQrCode(shareID: String): ImageBitmap {
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

    return bitmap.asImageBitmap()
}