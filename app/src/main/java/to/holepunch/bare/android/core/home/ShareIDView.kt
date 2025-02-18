package to.holepunch.bare.android.core.home

import android.graphics.Bitmap
import android.graphics.Color
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import com.google.zxing.BarcodeFormat
import com.google.zxing.EncodeHintType
import com.google.zxing.qrcode.QRCodeWriter

@Composable
fun ShareIDView() {
    Text("Share ID Content")
}

fun generateQrCode(shareID: String): ImageBitmap {
    val size = 512
    val hints = hashMapOf<EncodeHintType, Int>().also {
        it[EncodeHintType.MARGIN] = 1
    }

    val bits = QRCodeWriter().encode(shareID, BarcodeFormat.QR_CODE, size, size, hints)
    val bitmap = Bitmap.createBitmap(size, size, Bitmap.Config.RGB_565).also {
        for (x in 0 until size) {
            for (y in 0 until size) {
                it.setPixel(x, y, if (bits[x, y]) Color.BLACK else Color.WHITE )
            }
        }
    }

    return bitmap.asImageBitmap()
}