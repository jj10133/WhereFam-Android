package to.holepunch.bare.android.data.ipc

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.buildJsonObject
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

    private val _locationUpdates = MutableStateFlow<List<LocationData>>(emptyList())
    override val locationUpdates: StateFlow<List<LocationData>> = _locationUpdates.asStateFlow()

    override suspend fun requestPublicKey() {
        val dynamicData = buildJsonObject {}
        val message = GenericAction(action = "requestPublicKey", data = dynamicData)
        val jsonString = Json.Default.encodeToString(message) + "\n"
        val byteBuffer = ByteBuffer.wrap(jsonString.toByteArray(Charset.forName("UTF-8")))
        ipc.writeAsync(byteBuffer)
    }

    override fun updatePublicKey(key: String) {
        _currentPublicKey.value = key
    }

    override fun addLocationUpdate(location: LocationData) {
        _locationUpdates.update { _locationUpdates.value + location }
    }
}