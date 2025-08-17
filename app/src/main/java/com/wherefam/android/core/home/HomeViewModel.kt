package com.wherefam.android.core.home

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.wherefam.android.data.PeerRepository
import com.wherefam.android.data.ipc.IPCUtils.writeAsync
import com.wherefam.android.data.local.GenericAction
import com.wherefam.android.data.local.Peer
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put
import to.holepunch.bare.kit.IPC
import java.nio.ByteBuffer
import java.nio.charset.Charset

class HomeViewModel(
    context: Context,
    private val ipc: IPC,
    private val peerRepository: PeerRepository
) : ViewModel() {
    private val fileDir = context.filesDir

//    val userLocation: MutableState<Location> = mutableStateOf(Location("gps"))

    val peers: StateFlow<List<Peer>> = peerRepository.getAllPeers()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )


    suspend fun start() {
        val dynamicData = buildJsonObject { put("path", fileDir.path) }
        val message = GenericAction(action = "start", data = dynamicData)

        val jsonString = Json.encodeToString(message) + "\n"

        val byteBuffer = ByteBuffer.wrap(jsonString.toByteArray(Charset.forName("UTF-8")))
        ipc.writeAsync(byteBuffer)
    }
}
