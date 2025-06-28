package to.holepunch.bare.android.core.home

import android.content.Intent
import android.graphics.Color
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import org.koin.androidx.compose.koinViewModel
import org.koin.compose.koinInject
import org.maplibre.android.geometry.LatLng
import org.maplibre.android.location.modes.RenderMode
import org.maplibre.android.maps.Style
import org.ramani.compose.CameraPosition
import org.ramani.compose.LocationStyling
import org.ramani.compose.MapLibre
import org.ramani.compose.Symbol
import to.holepunch.bare.android.core.home.people.PeopleView
import to.holepunch.bare.android.core.home.share.ShareIDView
import to.holepunch.bare.android.manager.LocationManager


@Composable
@ExperimentalMaterial3Api
fun HomeView(
    homeViewModel: HomeViewModel = koinViewModel(),
    locationManager: LocationManager = koinInject()
) {

    val cameraPosition = rememberSaveable { mutableStateOf(CameraPosition(zoom = 14.0)) }
    val renderMode = rememberSaveable { mutableIntStateOf(RenderMode.NORMAL) }

    var selectedOption by remember { mutableStateOf<MenuOption?>(null) }
    var bottomSheetVisible by remember { mutableStateOf(false) }
    var shouldShareLink by remember { mutableStateOf(false) }
    var dialogInput by remember { mutableStateOf("") }

    val locationUpdates by homeViewModel.locationUpdates.collectAsState()

    LaunchedEffect(Unit) {
        locationManager.getLocation { latitude, longitude ->
            cameraPosition.value = CameraPosition(
                target = LatLng(latitude, longitude),
                zoom = 1.0
            )
        }
        homeViewModel.start()
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
                styleBuilder = Style.Builder().fromUri("asset://style.json"),
                cameraPosition = cameraPosition.value,
                locationStyling = LocationStyling(
                    enablePulse = true,
                    pulseColor = Color.BLUE
                ),
                renderMode = renderMode.value
            ) {
                locationUpdates.forEach { locationData ->
                    Symbol(
                        center = LatLng(locationData.latitude, locationData.longitude),
                        text = locationData.name,
                    )
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
                                }

                                MenuOption.ShareID -> ShareIDView()
                                MenuOption.ProvideFeedback -> ProvideFeedbackView()
                                else -> {}
                            }
                        }
                    },
                    containerColor = MaterialTheme.colorScheme.surfaceContainer,
                    onDismissRequest = { bottomSheetVisible = false }
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


