package to.holepunch.bare.android.data_access.ipc

import android.util.Log
import androidx.lifecycle.LifecycleCoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import to.holepunch.bare.android.data_access.ipc.IPCUtils.readStream
import to.holepunch.bare.android.processing.MessageProcessor
import to.holepunch.bare.kit.IPC

class IPCMessageConsumer(
    private val ipc: IPC,
    private val messageProcessor: MessageProcessor
) {
    var lifecycleScope: LifecycleCoroutineScope? = null

    fun startConsuming() {
        lifecycleScope?.launch(Dispatchers.IO) {
            ipc.readStream().collectLatest { data ->
                messageProcessor.processMessage(data)
            }
        } ?: Log.e("IPCMessageConsumer", "lifecycleScope is null, cannot start consuming.")
    }
}