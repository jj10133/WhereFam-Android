package to.holepunch.bare.android.data

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonElement

@Serializable
data class GenericAction(val action: String, val data: JsonElement? = null)