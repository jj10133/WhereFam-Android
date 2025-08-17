package com.wherefam.android.data.ipc

import com.wherefam.android.data.UserRepository
import com.wherefam.android.data.ipc.IPCUtils.writeAsync
import com.wherefam.android.data.local.GenericAction
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put
import to.holepunch.bare.kit.IPC
import java.nio.ByteBuffer
import java.nio.charset.Charset

class UserRepositoryImpl(private val ipc: IPC) : UserRepository {

    private val _currentPublicKey = MutableStateFlow("")
    override val currentPublicKey: StateFlow<String> = _currentPublicKey.asStateFlow()

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
}