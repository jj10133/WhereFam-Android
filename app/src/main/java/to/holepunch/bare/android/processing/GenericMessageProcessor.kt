package to.holepunch.bare.android.processing

import android.util.Log
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import to.holepunch.bare.android.data.GenericAction

class GenericMessageProcessor(private val userRepository: UserRepository) : MessageProcessor {
    override fun processMessage(message: String) {
        try {
            val incomingMessage = Json.decodeFromString<GenericAction>(message)
            when (incomingMessage.action) {

                "publicKeyResponse" -> {
                    val publicKey = incomingMessage.data?.jsonObject["publicKey"]?.jsonPrimitive?.content
                    publicKey?.let { key ->
                        userRepository.updatePublicKey(key)
                    }
                        ?: Log.w("GenericProcessor", "Missing 'value' for action 'requestPublicKey'")
                }

                "locationUpdate" -> {

                }

                else -> Log.w("GenericProcessor", "Unknown action: ${incomingMessage.action}")

            }
        } catch (e: Exception) {
            Log.e("GenericProcessor", "Error processing message: ${e.message}")
        }
    }
}

//private fun handleLocationUpdate(incomingMessage: IncomingMessage) {
//    val locationData = incomingMessage.data.jsonObject
//    val id = locationData["id"]?.jsonPrimitive?.content
//    val name = locationData["name"]?.jsonPrimitive?.content
//    val latitude = locationData["latitude"]?.jsonPrimitive?.double
//    val longitude = locationData["longitude"]?.jsonPrimitive?.double
//
//    if (id != null && name != null && latitude != null && longitude != null) {
//        println("Location Update: ID=$id, Name=$name, Latitude=$latitude, Longitude=$longitude")
//        // Process location update (e.g., save it in the database)
//    } else {
//        println("Invalid location update data.")
//    }
//}