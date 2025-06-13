package to.holepunch.bare.android.core.home

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.core.graphics.createBitmap
import androidx.core.graphics.set
import com.google.zxing.BarcodeFormat
import com.google.zxing.EncodeHintType
import com.google.zxing.qrcode.QRCodeWriter
import to.holepunch.bare.android.R

@Composable
fun ShareIDView(homeViewModel: HomeViewModel) {
    var qrCodeBitmap by remember { mutableStateOf<ImageBitmap?>(null) }
    var publicKey by homeViewModel.publicKey
    val context = LocalContext.current

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
        verticalArrangement = Arrangement.Center,
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        CustomToolbar(
            title = "Share Your ID",
            onShareClick = { sharePublicKey(context, publicKey) }
        )

        Spacer(modifier = Modifier.height(16.dp))

        qrCodeBitmap?.let {
            Image(bitmap = it, contentDescription = "Share QR Code")
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text("Or copy public ID", style = MaterialTheme.typography.bodyMedium)

        if (publicKey.isNotEmpty()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                BasicTextField(
                    value = publicKey,
                    onValueChange = {},
                    enabled = false,
                    singleLine = true,
                    modifier = Modifier
                        .weight(1f)
                        .padding(8.dp)
                        .border(1.dp, MaterialTheme.colorScheme.onSurface)
                        .background(MaterialTheme.colorScheme.background)
                        .padding(8.dp),
                )

                Spacer(modifier = Modifier.width(8.dp))

                IconButton(onClick = {
                    copyToClipboard(context, publicKey)
                }) {
                    Icon(
                        painter = painterResource(id = R.drawable.baseline_content_copy_24),
                        contentDescription = "Copy Public Key"
                    )
                }
            }
        }
    }
}

@Composable
fun CustomToolbar(
    title: String,
    onShareClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        Spacer(modifier = Modifier.weight(0.2f))

        Text(
            text = title,
            style = MaterialTheme.typography.titleLarge,
            textAlign = TextAlign.Center,
            modifier = Modifier.weight(1f)
        )


        IconButton(onClick = onShareClick) {
            Icon(imageVector = Icons.Default.Share, contentDescription = "Share Public Key")
        }
    }
}

fun sharePublicKey(context: Context, publicKey: String) {
    val intent = Intent(Intent.ACTION_SEND).apply {
        type = "text/plain"
        putExtra(Intent.EXTRA_TEXT, publicKey)
    }
    context.startActivity(Intent.createChooser(intent, "Share Public Key"))
}

fun copyToClipboard(context: Context, publicKey: String) {
    val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as android.content.ClipboardManager
    val clip = android.content.ClipData.newPlainText("Public Key", publicKey)
    clipboard.setPrimaryClip(clip)
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