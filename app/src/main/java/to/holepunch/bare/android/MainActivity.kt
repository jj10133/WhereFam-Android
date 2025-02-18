package to.holepunch.bare.android

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import to.holepunch.bare.android.core.root.ContentView
import to.holepunch.bare.kit.IPC
import to.holepunch.bare.kit.Worklet
import java.nio.ByteBuffer
import java.nio.charset.Charset
import kotlin.coroutines.resume

class MainActivity : ComponentActivity() {
    private lateinit var ipcHandler: IPCHandler

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ipcHandler = IPCHandler(this)
        ipcHandler.initialize()

        lifecycleScope.launch(Dispatchers.IO) {
            ipcHandler.listenForMessages()
        }

        setContent { MainScreen() }
    }

    public override fun onPause() {
        super.onPause()
        ipcHandler.onPause()
    }

    public override fun onResume() {
        super.onResume()
        ipcHandler.onResume()
    }

    public override fun onDestroy() {
        super.onDestroy()
        ipcHandler.onDestroy()
    }
}

@Composable
fun MainScreen() {
    ContentView()
}

