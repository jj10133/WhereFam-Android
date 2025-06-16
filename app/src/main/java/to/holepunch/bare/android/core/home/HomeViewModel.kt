package to.holepunch.bare.android.core.home

import android.content.Context
import android.location.Location
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put
import to.holepunch.bare.android.data.GenericAction
import to.holepunch.bare.android.data.ipc.IPCUtils.writeAsync
import to.holepunch.bare.android.processing.UpdateState
import to.holepunch.bare.kit.IPC
import java.nio.ByteBuffer
import java.nio.charset.Charset

class HomeViewModel(context: Context, private val ipc: IPC) : ViewModel(),
    UpdateState {
    private val fileDir = context.filesDir

    val publicKey: MutableState<String> = mutableStateOf("")

    val styleUrl: MutableState<String> = mutableStateOf("")

    val userLocation: MutableState<Location> = mutableStateOf(Location("gps"))

    suspend fun start() {
        val dynamicData = buildJsonObject { put("path", fileDir.path) }
        val message = GenericAction(action = "start", data = dynamicData)

        val jsonString = Json.encodeToString(message) + "\n"

        val byteBuffer = ByteBuffer.wrap(jsonString.toByteArray(Charset.forName("UTF-8")))
        ipc.writeAsync(byteBuffer)
    }

    suspend fun fetchMaps() {
        val dynamicData = buildJsonObject { put("path", fileDir.path) }
        val message = GenericAction(action = "fetchMaps", data = dynamicData)

        val jsonString = Json.encodeToString(message) + "\n"

        val byteBuffer = ByteBuffer.wrap(jsonString.toByteArray(Charset.forName("UTF-8")))
        ipc.writeAsync(byteBuffer)
    }

    suspend fun requestPublicKey() {
        val dynamicData = buildJsonObject {}
        val message = GenericAction(action = "requestPublicKey", data = dynamicData)

        val jsonString = Json.encodeToString(message) + "\n"

        val byteBuffer = ByteBuffer.wrap(jsonString.toByteArray(Charset.forName("UTF-8")))
        ipc.writeAsync(byteBuffer)
    }

    override fun setPublicKey(key: String) {
        publicKey.value = key
    }
}
