package to.holepunch.bare.android.core.onboarding

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.app.ActivityCompat.shouldShowRequestPermissionRationale
import androidx.core.content.ContextCompat
import to.holepunch.bare.android.core.permissions.PermissionDialog

@Composable
fun FourthPageView() {

    val permissions = arrayOf(
        Manifest.permission.ACCESS_COARSE_LOCATION,
        Manifest.permission.ACCESS_FINE_LOCATION
    )

    val context = LocalContext.current
    val activity = context as? Activity

    var permissionDialog by remember { mutableStateOf(false) }
    var launchAppSettings by remember { mutableStateOf(false) }

    val permissionsResultActivityLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions(),
        onResult = { result ->
            permissions.forEach { permission ->
                if (result[permission] == false) {
                    if (activity != null && !shouldShowRequestPermissionRationale(activity, permission)) {
                        launchAppSettings = true
                    }
                    permissionDialog = true
                }
            }
        }
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFFFB77F))
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {

        Image(
            imageVector = Icons.Filled.LocationOn,
            contentDescription = "Location Icon",
            colorFilter = ColorFilter.tint(Color.White),
            modifier = Modifier
                .size(200.dp)
                .padding(bottom = 32.dp)
        )

        Text(
            text = "Would you like to find family and friends nearby?",
            fontSize = 28.sp,
            fontWeight = FontWeight.SemiBold,
            textAlign = TextAlign.Center,
            color = Color.White,
            modifier = Modifier.padding(8.dp)
        )

        Text(
            text = "Start sharing your location now",
            textAlign = TextAlign.Center,
            color = Color.White,
            modifier = Modifier
                .size(width = 140.dp, height = 50.dp)
                .padding(8.dp)
        )

        Spacer(modifier = Modifier.height(48.dp))

        Button(
            onClick = {
                permissions.forEach { permission ->
                    val isGranted = ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED

                    if (!isGranted) {
                        if (activity != null && shouldShowRequestPermissionRationale(activity, permission)) {
                            permissionDialog = true
                        }
                        else {
                            permissionsResultActivityLauncher.launch(permissions)
                        }
                    }
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color.White,
                contentColor = CustomOrange
            ),
            shape = RoundedCornerShape(10.dp)
        ) {
            Text(
                text = "Allow Location Access",
                fontSize = 22.sp,
                modifier = Modifier.padding(vertical = 8.dp)
            )
        }

        if (permissionDialog) {
            PermissionDialog(
                onDismiss = { permissionDialog = false },
                onConfirm = {
                    permissionDialog = false

                    if (launchAppSettings) {
                        Intent(
                            Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                            Uri.fromParts("package", "to.holepunch.bare.android", null)
                        ).also { intent ->
                            context.startActivity(intent)
                        }
                        launchAppSettings = false
                    } else {
                        permissionsResultActivityLauncher.launch(permissions)
                    }
                }
            )
        }
    }
}