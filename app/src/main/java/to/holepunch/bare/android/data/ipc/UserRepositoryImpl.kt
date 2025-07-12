package to.holepunch.bare.android.data.ipc

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
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

class UserRepositoryImpl(private val ipc: IPC) : UserRepository {

    private val _currentPublicKey = MutableStateFlow("")
    override val currentPublicKey: StateFlow<String> = _currentPublicKey.asStateFlow()

    private val _locationUpdatesMap = MutableStateFlow<Map<String, LocationData>>(emptyMap())
    override val locationUpdates: StateFlow<List<LocationData>> = _locationUpdatesMap
        .map { it.values.toList() }
        .stateIn(
            scope = CoroutineScope(Dispatchers.Default),
            started = SharingStarted.Lazily,
            initialValue = emptyList()
        )

    override suspend fun requestPublicKey() {
        val dynamicData = buildJsonObject {}
        val message = GenericAction(action = "requestPublicKey", data = dynamicData)
        val jsonString = Json.Default.encodeToString(message) + "\n"
        val byteBuffer = ByteBuffer.wrap(jsonString.toByteArray(Charset.forName("UTF-8")))
        ipc.writeAsync(byteBuffer)
    }

    override suspend fun joinPeer(key: String) {
        val dynamicData = buildJsonObject {
            put("peerPublicKey", key)
        }
        val message = GenericAction(action = "joinPeer", data = dynamicData)
        val jsonString = Json.Default.encodeToString(message) + "\n"
        val byteBuffer = ByteBuffer.wrap(jsonString.toByteArray(Charset.forName("UTF-8")))
        ipc.writeAsync(byteBuffer)
    }


    override fun updatePublicKey(key: String) {
        _currentPublicKey.value = key
    }

    override fun addLocationUpdate(location: LocationData) {
       _locationUpdatesMap.update { currentMap ->
           currentMap + (location.id to location)
       }
    }
}