package com.wherefam.android.processing

import android.util.Log
import com.wherefam.android.data.PeerRepository
import com.wherefam.android.data.UserRepository
import com.wherefam.android.data.local.GenericAction
import com.wherefam.android.data.local.Peer
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.double
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive

class GenericMessageProcessor(private val userRepository: UserRepository, private val peerRepository: PeerRepository) :
    MessageProcessor {
    override suspend fun processMessage(message: String) {
        val individualMessage = message.split("\n").filter { it.isNotBlank() }
        for (msg in individualMessage) {
            try {
                val incomingMessage = Json.decodeFromString<GenericAction>(msg)
                when (incomingMessage.action) {

                    "publicKeyResponse" -> {
                        val publicKey = incomingMessage.data?.jsonObject["publicKey"]?.jsonPrimitive?.content
                        publicKey?.let { key ->
                            userRepository.updatePublicKey(key)
                        }
                            ?: Log.w("GenericProcessor", "Missing 'value' for action 'requestPublicKey'")
                    }

                    "locationUpdate" -> {
                        handleLocationUpdate(incomingMessage)
                    }

                    else -> Log.w("GenericProcessor", "Unknown action: ${incomingMessage.action}")

                }
            } catch (e: Exception) {
                Log.e("GenericProcessor", "Error processing message: ${e.message}")
            }
        }
    }

    private suspend fun handleLocationUpdate(incomingMessage: GenericAction) {
        val locationDataJson = incomingMessage.data?.jsonObject
        if (locationDataJson != null) {
            val id = locationDataJson["id"]?.jsonPrimitive?.content
            val name = locationDataJson["name"]?.jsonPrimitive?.content
            val latitude = locationDataJson["latitude"]?.jsonPrimitive?.double
            val longitude = locationDataJson["longitude"]?.jsonPrimitive?.double

            if (id != null && name != null && latitude != null && longitude != null) {
                peerRepository.upsert(Peer(id, name, latitude, longitude))
            } else {
                Log.w("GenericProcessor", "Invalid location update data: Missing required fields.")
            }
        } else {
            Log.w("GenericProcessor", "Location update message has no data.")
        }
    }
}