package to.holepunch.bare.android

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import to.holepunch.bare.android.core.home.HomeViewModel
import to.holepunch.bare.android.core.root.ContentView
import to.holepunch.bare.android.data_access.ipc.IPCMessageConsumer
import to.holepunch.bare.android.manager.LocationManager
import to.holepunch.bare.android.processing.GenericMessageProcessor
import to.holepunch.bare.kit.IPC
import to.holepunch.bare.kit.Worklet
import java.io.File
import java.io.FileOutputStream

class MainActivity : ComponentActivity() {

    private var worklet: Worklet? = null
    private var ipc: IPC? = null
    private lateinit var messageProcessor: GenericMessageProcessor
    private var ipcMessageConsumer: IPCMessageConsumer? = null
    private lateinit var homeViewModel: HomeViewModel
    private lateinit var locationManager: LocationManager


    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        worklet = Worklet(null)

        try {
            worklet!!.start("/app.bundle", assets.open("app.bundle"), null)
            ipc = IPC(worklet)
            locationManager = LocationManager(this)
            homeViewModel = HomeViewModel(this, ipc!!)
            messageProcessor = GenericMessageProcessor(homeViewModel)
            ipcMessageConsumer = IPCMessageConsumer(ipc!!, messageProcessor)
            ipcMessageConsumer?.lifecycleScope = lifecycleScope
            ipcMessageConsumer?.startConsuming()
        } catch (e: Exception) {
            throw RuntimeException(e)
        }

        lifecycleScope.launch(Dispatchers.IO) {
            // Fetch maps first
            homeViewModel.fetchMaps()

            // Start operation should be executed after fetchMaps()
            homeViewModel.start()

            // Launch the copyStyleFileToInternalStorage concurrently with the start operation
            val copyStyleJob = launch { copyStyleFileToInternalStorage() }

            // Wait for copyStyleFileToInternalStorage to complete (since we want it to finish before UI update)
            copyStyleJob.join()
            withContext(Dispatchers.Main) {
                setContent { ContentView(homeViewModel) }
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
    }

    private fun copyStyleFileToInternalStorage() {
        val styleFile = File(filesDir, "style.json")

        if (!styleFile.exists()) {
            lifecycleScope.launch {
                try {
                    assets.open("style.json").use { inputStream ->
                        FileOutputStream(styleFile).use { outputStream ->
                            inputStream.copyTo(outputStream)
                        }
                    }
                    withContext(Dispatchers.Main) {
                        Log.d("MainActivity", "style.json copied to internal storage")
                    }
                } catch (e: Exception) {
                    withContext(Dispatchers.Main) {
                        Log.e("MainActivity", "Error copying style.json: ${e.message}")
                    }
                }
            }
        }
    }
}
