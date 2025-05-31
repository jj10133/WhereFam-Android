package to.holepunch.bare.android.core.home

import android.content.Intent
import android.graphics.Color
import android.location.Location
import android.location.LocationManager
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import org.koin.androidx.compose.koinViewModel
import org.maplibre.android.location.modes.CameraMode
import org.maplibre.android.location.modes.RenderMode
import org.maplibre.android.maps.Style
import org.ramani.compose.CameraPosition
import org.ramani.compose.LocationStyling
import org.ramani.compose.MapLibre
import to.holepunch.bare.android.core.permissions.PermissionRequest


@Composable
@ExperimentalMaterial3Api
fun HomeView(homeViewModel: HomeViewModel = koinViewModel()) {

    val cameraPosition = rememberSaveable { mutableStateOf(CameraPosition(zoom = 14.0)) }
    val userLocation = rememberSaveable { mutableStateOf(Location(LocationManager.GPS_PROVIDER)) }
    val cameraMode = rememberSaveable { mutableIntStateOf(CameraMode.TRACKING) }
    val renderMode = rememberSaveable { mutableIntStateOf(RenderMode.NORMAL) }

    var selectedOption by remember { mutableStateOf<MenuOption?>(null) }
    var bottomSheetVisible by remember { mutableStateOf(false) }
    var shouldShareLink by remember { mutableStateOf(false) }
    var showDialog by remember { mutableStateOf(false) }
    var dialogInput by remember { mutableStateOf("") }

    var styleUrl by homeViewModel.styleUrl


    PermissionRequest()

    LaunchedEffect(Unit) {
        delay(2000)
        homeViewModel.getMapLink()
    }

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
        Box(modifier = Modifier.fillMaxSize()) {
            MapLibre(
                modifier = Modifier.fillMaxSize(),
                styleBuilder = Style.Builder().fromUri(styleUrl),
                cameraPosition = cameraPosition.value,
                locationStyling = LocationStyling(
                    enablePulse = true,
                    pulseColor = Color.BLUE
                ),
                userLocation = userLocation,
                cameraMode = cameraMode,
                renderMode = renderMode.value
            )

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

                                MenuOption.ShareID -> ShareIDView(homeViewModel)
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


