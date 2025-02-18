package to.holepunch.bare.android

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonElement

@Serializable
data class IncomingMessage(
    val actions: String,
    val data: JsonElement
)