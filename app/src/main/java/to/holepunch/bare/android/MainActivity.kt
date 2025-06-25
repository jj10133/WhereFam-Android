package to.holepunch.bare.android

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.getValue
import androidx.lifecycle.lifecycleScope
import androidx.navigation.compose.rememberNavController
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel
import to.holepunch.bare.android.core.home.HomeViewModel
import to.holepunch.bare.android.core.onboarding.SplashViewModel
import to.holepunch.bare.android.core.root.ContentView
import to.holepunch.bare.android.data.ipc.IPCMessageConsumer
import to.holepunch.bare.android.data.ipc.IPCProvider
import to.holepunch.bare.android.processing.GenericMessageProcessor
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

        lifecycleScope.launch {
            homeViewModel.fetchMaps()
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
    }
}
