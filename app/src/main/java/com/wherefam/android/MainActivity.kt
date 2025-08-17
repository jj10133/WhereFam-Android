package com.wherefam.android

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.getValue
import androidx.lifecycle.lifecycleScope
import androidx.navigation.compose.rememberNavController
import com.wherefam.android.core.home.HomeViewModel
import com.wherefam.android.core.onboarding.SplashViewModel
import com.wherefam.android.core.root.ContentView
import com.wherefam.android.data.ipc.IPCMessageConsumer
import com.wherefam.android.data.ipc.IPCProvider
import com.wherefam.android.manager.LocationTrackerService
import com.wherefam.android.processing.GenericMessageProcessor
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel
import to.holepunch.bare.kit.IPC
import to.holepunch.bare.kit.Worklet

class MainActivity : ComponentActivity() {

    private var worklet: Worklet? = null
    private var ipc: IPC? = null
    private val messageProcessor: GenericMessageProcessor by inject()
    private var ipcMessageConsumer: IPCMessageConsumer? = null
    private val homeViewModel: HomeViewModel by viewModel()
    private val splashViewModel: SplashViewModel by viewModel()

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        actionBar?.hide()

        worklet = Worklet(null)

        try {
            worklet!!.start("/app.bundle", assets.open("app.bundle"), null)
            ipc = IPC(worklet)
            IPCProvider.ipc = ipc

            ipcMessageConsumer = IPCMessageConsumer(ipc!!, messageProcessor)
            ipcMessageConsumer?.lifecycleScope = lifecycleScope
            ipcMessageConsumer?.startConsuming()
        } catch (e: Exception) {
            throw RuntimeException(e)
        }

        val channel = NotificationChannel(
            LocationTrackerService.Companion.LOCATION_CHANNEL,
            "Location",
            NotificationManager.IMPORTANCE_LOW
        )

        val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)

        lifecycleScope.launch {
            withContext(Dispatchers.Main) {
                setContent {
                    val screen by splashViewModel.startDestination
                    val navController = rememberNavController()

                    ContentView(navController, screen)
                }
            }
        }
    }

    public override fun onPause() {
        super.onPause()
        worklet!!.suspend()
    }

    public override fun onResume() {
        super.onResume()
        worklet!!.resume()
    }

    public override fun onDestroy() {
        super.onDestroy()
        worklet!!.terminate()
        worklet = null
        Intent(applicationContext, LocationTrackerService::class.java).apply {
            action = LocationTrackerService.Action.STOP.name
            startService(this)
        }
    }
}
