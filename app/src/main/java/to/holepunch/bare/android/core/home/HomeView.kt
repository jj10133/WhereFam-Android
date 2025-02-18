package to.holepunch.bare.android.core.home

import android.Manifest
import android.content.Intent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FabPosition
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.mapbox.maps.extension.compose.MapEffect
import com.mapbox.maps.extension.compose.MapboxMap
import com.mapbox.maps.extension.compose.animation.viewport.rememberMapViewportState
import com.mapbox.maps.plugin.PuckBearing
import com.mapbox.maps.plugin.locationcomponent.createDefault2DPuck
import com.mapbox.maps.plugin.locationcomponent.location
import to.holepunch.bare.android.core.permissions.PermissionRequest


@Composable
@ExperimentalMaterial3Api
fun HomeView() {
    val permissions = arrayOf(
        Manifest.permission.ACCESS_COARSE_LOCATION,
        Manifest.permission.ACCESS_FINE_LOCATION
    )

    val mapViewportState = rememberMapViewportState()
    var selectedOption by remember { mutableStateOf<MenuOption?>(null) }
    var bottomSheetVisible by remember { mutableStateOf(false) }
    var shouldShareLink by remember { mutableStateOf(false) }
    var showDialog by remember { mutableStateOf(false) }
    var dialogInput by remember { mutableStateOf("") }

    PermissionRequest(permissions)


    Scaffold(
        floatingActionButton = {
            Menu(
                onPeopleSelected = {
                    selectedOption = MenuOption.People
                    bottomSheetVisible = true
                },
                onShareIDSelected = {
                    selectedOption = MenuOption.ShareID
                    bottomSheetVisible = true
                },
                onProvideFeedbackSelected = {
                    selectedOption = MenuOption.ProvideFeedback
                    bottomSheetVisible = true
                },
                onReferFriendSelected = {
                    shouldShareLink = true
                }
            )
        },
        floatingActionButtonPosition = FabPosition.End,
    ) { innerPadding ->
        MapboxMap(
            Modifier
                .fillMaxSize()
                .padding(innerPadding),
            mapViewportState = mapViewportState,
            scaleBar = {},

            ) {
            MapEffect(Unit) { mapView ->
                mapView.location.updateSettings {
                    locationPuck = createDefault2DPuck(withBearing = true)
                    enabled = true
                    puckBearing = PuckBearing.COURSE
                    puckBearingEnabled = true
                }
                mapViewportState.transitionToFollowPuckState()
            }
        }

        if (bottomSheetVisible) {
            ModalBottomSheet(
                sheetState = rememberModalBottomSheetState(),
                content = {
                    Box(modifier = Modifier.fillMaxSize()) {
                        when (selectedOption) {
                            MenuOption.People -> {
                                PeopleView()

                                Icon(
                                    imageVector = Icons.Default.Add,
                                    contentDescription = "Add",
                                    modifier = Modifier
                                        .padding(horizontal = 16.dp)
                                        .size(30.dp)
                                        .align(Alignment.TopEnd)
                                        .clickable {
                                            showDialog = true
                                        }
                                )
                            }

                            MenuOption.ShareID -> ShareIDView()
                            MenuOption.ProvideFeedback -> ProvideFeedbackView()
                            else -> {}
                        }
                    }
                },
                onDismissRequest = { bottomSheetVisible = false }
            )
        }

        if (showDialog) {
            AddPeopleDialog(
                onConfirm = {
                    showDialog = false
                },
                onDismiss = {
                    showDialog = false
                }
            )
        }

        if (shouldShareLink) {
            ReferView()
            shouldShareLink = false
        }
    }
}

@Composable
fun ReferView() {
    val context = LocalContext.current
    val shareIntent = Intent().apply {
        action = Intent.ACTION_SEND
        putExtra(Intent.EXTRA_TEXT, "Check out this URL: https://www.example.com")
        type = "text/plain"
    }
    context.startActivity(Intent.createChooser(shareIntent, "Share URL"))
}


