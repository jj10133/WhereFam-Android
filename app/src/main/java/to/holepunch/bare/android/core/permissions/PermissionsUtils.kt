package to.holepunch.bare.android.core.permissions

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.core.app.ActivityCompat.shouldShowRequestPermissionRationale
import androidx.core.content.ContextCompat

@Composable
fun PermissionRequest() {
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

    LaunchedEffect(Unit) {
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

@Composable
fun PermissionDialog(
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {
    AlertDialog(
        modifier = Modifier.fillMaxWidth(),
        onDismissRequest = onDismiss,
        confirmButton = {
            Button(onClick = onConfirm) {
                Text(text = "Ok")
            }
        },
        title = {
            Text(
                text = "Location permissions are needed"
            )
        },
        text = {
            Text(text = "Please allow us to access your location to find your loved ones")
        }
    )
}