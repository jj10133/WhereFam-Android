package to.holepunch.bare.android.data_access.ipc

import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.suspendCancellableCoroutine
import to.holepunch.bare.kit.IPC
import java.nio.ByteBuffer
import java.nio.charset.Charset
import kotlin.coroutines.resume

object IPCUtils {
    fun IPC.readStream(): Flow<String> = callbackFlow {
        val utf8Charset = Charset.forName("UTF-8")
        val readableCallback = IPC.PollCallback {
            try {
                var buffer: ByteBuffer?
                while (true) {
                    buffer = this@readStream.read()
                    if (buffer == null) break
                    val message = utf8Charset.decode(buffer).toString()
                    trySend(message).isSuccess
                }
            } catch (e: Exception) {
                close(e)
            }
        }

        readable(readableCallback)

        awaitClose {
            readable(null)
        }
    }

    suspend fun IPC.writeAsync(data: ByteBuffer): Boolean = suspendCancellableCoroutine { continuation ->
        val written = this.write(data)
        if (written == data.limit()) {
            continuation.resume(true)
            return@suspendCancellableCoroutine
        }

        val writableCallback = IPC.PollCallback {
            val writtenAgain = this.write(data)
            if (writtenAgain == data.limit()) {
                this.writable(null) // Clear the callback
                continuation.resume(true)
            }
        }

        this.writable(writableCallback)

        continuation.invokeOnCancellation {
            this.writable(null)
        }
    }
}


