package to.holepunch.bare.android.manager

import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.IBinder
import androidx.core.app.NotificationCompat
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel

class LocationTrackerService : Service() {
    private val scope = CoroutineScope(
        SupervisorJob() + Dispatchers.IO
    )

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
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val notification = NotificationCompat
            .Builder(this, LOCATION_CHANNEL)
            .setContentTitle("Background Location")

        startForeground(1, notification.build())
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