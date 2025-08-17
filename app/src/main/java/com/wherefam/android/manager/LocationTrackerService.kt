package com.wherefam.android.manager

import android.R
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.wherefam.android.data.UserRepository
import com.wherefam.android.data.ipc.IPCUtils.writeAsync
import com.wherefam.android.data.local.DataStoreRepository
import com.wherefam.android.data.local.GenericAction
import kotlinx.coroutines.*
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put
import org.koin.android.ext.android.inject
import to.holepunch.bare.kit.IPC
import java.nio.ByteBuffer
import java.nio.charset.Charset

class LocationTrackerService : Service() {
    private val scope = CoroutineScope(
        SupervisorJob() + Dispatchers.IO
    )

    private val locationManager: LocationManager by inject()
    private val ipc: IPC by inject()
    private val dataStoreRepository: DataStoreRepository by inject()
    private val userRepository: UserRepository by inject()

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            Action.START.name -> start()
            Action.STOP.name -> stop()
        }

        return super.onStartCommand(intent, flags, startId)
    }

    private fun start() {
        val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager

        val notification = NotificationCompat
            .Builder(this, LOCATION_CHANNEL)
            .setSmallIcon(R.drawable.gallery_thumb)
            .setContentTitle("Background Location")

        startForeground(1, notification.build())

        scope.launch {
            locationManager.trackLocation().collect { location ->
                val latitude = location.latitude
                val longitude = location.longitude

                notificationManager.notify(
                    1,
                    notification.setContentText(
                        "Location: $latitude / $longitude"
                    ).build()
                )

                sendLocationUpdates(latitude, longitude)
            }
        }
    }

    private suspend fun sendLocationUpdates(latitude: Double, longitude: Double) {

        if (userRepository.currentPublicKey.value.isEmpty()) {
            userRepository.requestPublicKey()
        } else {
            val dynamicData = buildJsonObject {
                put("id", userRepository.currentPublicKey.value)
                put("name", dataStoreRepository.getUserName())
                put("latitude", latitude)
                put("longitude", longitude)
            }

            val message = GenericAction(action = "locationUpdate", data = dynamicData)

            val jsonString = Json.encodeToString(message) + "\n"

            val byteBuffer = ByteBuffer.wrap(jsonString.toByteArray(Charset.forName("UTF-8")))
            ipc.writeAsync(byteBuffer)
        }
    }

    private fun stop() {
        stopForeground(STOP_FOREGROUND_REMOVE)
        stopSelf()
    }

    override fun onDestroy() {
        super.onDestroy()
        scope.cancel()
    }

    enum class Action {
        START, STOP
    }

    companion object {
        const val LOCATION_CHANNEL = "location_channel"
    }

}