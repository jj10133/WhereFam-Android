package to.holepunch.bare.android.core.home

import android.content.Context
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.StateFlow
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put
import to.holepunch.bare.android.data.LocationData
import to.holepunch.bare.android.data.UserRepository
import to.holepunch.bare.android.data.ipc.IPCUtils.writeAsync
import to.holepunch.bare.android.data.local.GenericAction
import to.holepunch.bare.kit.IPC
import java.nio.ByteBuffer
import java.nio.charset.Charset

class HomeViewModel(context: Context, private val ipc: IPC, private val userRepository: UserRepository) : ViewModel() {
    private val fileDir = context.filesDir

//    val userLocation: MutableState<Location> = mutableStateOf(Location("gps"))

    val locationUpdates: StateFlow<List<LocationData>> = userRepository.locationUpdates

    suspend fun start() {
        val dynamicData = buildJsonObject { put("path", fileDir.path) }
        val message = GenericAction(action = "start", data = dynamicData)

        val jsonString = Json.encodeToString(message) + "\n"

        val byteBuffer = ByteBuffer.wrap(jsonString.toByteArray(Charset.forName("UTF-8")))
        ipc.writeAsync(byteBuffer)
    }
}
