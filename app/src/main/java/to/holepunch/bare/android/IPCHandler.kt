package to.holepunch.bare.android

import android.content.Context
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonElement
import to.holepunch.bare.kit.IPC
import to.holepunch.bare.kit.Worklet
import java.nio.ByteBuffer
import java.nio.charset.Charset
import kotlin.coroutines.resume

class IPCHandler(private val context: Context){
    private var worklet: Worklet? = null
    private var ipc: IPC? = null

    fun initialize() {
        worklet = Worklet(null)
        try {
            worklet?.let {
                it.start("/app.bundle", context.assets.open("app.bundle"), null)
                ipc = IPC(worklet)
            }
                ?: throw RuntimeException("Worklet is null")
        } catch (e: Exception) {
            throw RuntimeException(e)
        }
    }

    suspend fun listenForMessages() {
        ipc?.let { ipc ->
            ipc.readStream().collect { message ->
                processIncomingMessage(message)
            }
        }
    }

    private fun processIncomingMessage(data: ByteBuffer) {
        val jsonString = String(data.array(), Charset.forName("UTF-8"))
        println(jsonString)
    }

    fun onPause() {
        worklet?.suspend()
    }

    fun onResume() {
        worklet?.resume()
    }

    fun onDestroy() {
        worklet?.terminate()
        worklet = null
    }
}

fun IPC.readStream(): Flow<ByteBuffer> = callbackFlow {
    val initialMessage = this@readStream.read()
    if (initialMessage != null) {
        trySend(initialMessage)
    }

    this@readStream.readable {
        launch(Dispatchers.IO) {
            try {
                while (true) {
                    val message = this@readStream.read()
                    if(message != null) {
                        trySend(message)
                    }
                }
            } catch (e: Exception) {
                close(e)
            }
        }
    }

    awaitClose {
        this@readStream.readable(null)
    }
}

suspend fun IPC.writeAsync(data: ByteBuffer): Boolean {
    return suspendCancellableCoroutine { continuation ->
        val success = this.write(data)
        if (success) {
            continuation.resume(true)
        } else {
            this.writable {
                val success1 = this.write(data)
                if (success1) {
                    this.writable(null)
                    continuation.resume(true)
                }
            }
        }
    }
}

