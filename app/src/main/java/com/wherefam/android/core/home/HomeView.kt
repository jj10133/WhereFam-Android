package com.wherefam.android.core.home

import android.R
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.wherefam.android.core.home.people.PeopleView
import com.wherefam.android.core.home.share.ShareIDView
import com.wherefam.android.manager.LocationManager
import com.wherefam.android.manager.LocationTrackerService
import org.koin.androidx.compose.koinViewModel
import org.koin.compose.koinInject
import org.maplibre.android.geometry.LatLng
import org.maplibre.android.location.modes.RenderMode
import org.maplibre.android.maps.Style
import org.ramani.compose.CameraPosition
import org.ramani.compose.LocationStyling
import org.ramani.compose.MapLibre
import org.ramani.compose.Symbol


@Composable
@ExperimentalMaterial3Api
fun HomeView(
    homeViewModel: HomeViewModel = koinViewModel(),
    locationManager: LocationManager = koinInject()
) {
    val context = LocalContext.current

    val cameraPosition = rememberSaveable { mutableStateOf(CameraPosition(zoom = 14.0)) }
    val renderMode = rememberSaveable { mutableIntStateOf(RenderMode.NORMAL) }

    var selectedOption by remember { mutableStateOf<MenuOption?>(null) }
    var bottomSheetVisible by remember { mutableStateOf(false) }
    var shouldShareLink by remember { mutableStateOf(false) }
    var shouldRateApp by remember { mutableStateOf(false) }
    var dialogInput by remember { mutableStateOf("") }

    val peers by homeViewModel.peers.collectAsState()

    LaunchedEffect(Unit) {
        locationManager.getLocation { latitude, longitude ->
            cameraPosition.value = CameraPosition(
                target = LatLng(latitude, longitude),
                zoom = 1.0
            )
        }
        homeViewModel.start()
    }

    LaunchedEffect(Unit) {
        val intent = Intent(context, LocationTrackerService::class.java).apply {
            action = LocationTrackerService.Action.START.name
        }
        context.startService(intent)
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
                onReferFriendSelected = {
                    shouldShareLink = true
                },
                onRateAppSelected = {
                    shouldRateApp = true
                },
                onSupportAppSelected = {

                }
            )
        },
        floatingActionButtonPosition = FabPosition.End,
    ) { innerPadding ->
        Box {
            Surface(
                modifier = Modifier.fillMaxSize(),
                color = MaterialTheme.colorScheme.background
            ) {
                MapLibre(
                    modifier = Modifier.fillMaxSize(),
//                    asset://style.json
                    styleBuilder = Style.Builder().fromUri("https://tiles.openfreemap.org/styles/liberty"),
                    cameraPosition = cameraPosition.value,
                    locationStyling = LocationStyling(
                        enablePulse = true,
                        pulseColor = Color.BLUE
                    ),
                    renderMode = renderMode.value
                ) {
                    peers.forEach { peer ->
                        if (peer.latitude != null && peer.longitude != null) {
                            Symbol(
                                center = LatLng(peer.latitude, peer.longitude),
                                size = 5F,
                                text = peer.name,
                                imageId = R.drawable.ic_menu_mylocation
                            )
                        }
                    }
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

            if (shouldRateApp) {
                RateView()
                shouldRateApp = false
            }
        }
    }
}

@Composable
fun ReferView() {
    val context = LocalContext.current
    val shareIntent = Intent().apply {
        action = Intent.ACTION_SEND
        putExtra(Intent.EXTRA_TEXT, "Check out: https://www.wherefam.com")
        type = "text/plain"
    }
    context.startActivity(Intent.createChooser(shareIntent, "Share URL"))
}

@Composable
fun RateView() {
    val context = LocalContext.current
    val appPackageName = context.packageName
    val intent = Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=$appPackageName"))
    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    context.startActivity(intent)
}


