package to.holepunch.bare.android.manager

import android.annotation.SuppressLint
import android.content.Context
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Looper
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.launch
import java.util.concurrent.Executors

@SuppressLint("MissingPermission")
class LocationManager(
    context: Context
) {

    private val androidLocationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager


    fun getLocation(
        onSuccess: (latitude: Double, longitude: Double) -> Unit,
        onFailure: () -> Unit = {}
    ){
        val executor = Executors.newSingleThreadExecutor()
        androidLocationManager.getCurrentLocation(
            LocationManager.GPS_PROVIDER,
            null,
            executor
        ) { location ->
            if (location != null) {
                onSuccess(location.latitude, location.longitude)
            } else {
                onFailure()
            }
        }
    }

    fun trackLocation(): Flow<Location> {
        return callbackFlow {
            val locationListener = LocationListener { location ->
                launch {
                    send(location)
                }
            }

            val updateInterval = 5000L
            val minDistance = 10f

            androidLocationManager.requestLocationUpdates(
                LocationManager.GPS_PROVIDER,
                updateInterval,
                minDistance,
                locationListener,
                Looper.getMainLooper()
            )

            awaitClose {
                androidLocationManager.removeUpdates(locationListener)
            }
        }
    }
}