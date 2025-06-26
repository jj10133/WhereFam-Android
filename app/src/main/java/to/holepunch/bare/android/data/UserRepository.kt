package to.holepunch.bare.android.data

import kotlinx.coroutines.flow.StateFlow


data class User(val id: String, val name: String? = null, val publicKey: String? = null)
data class LocationData(val id: String, val name: String, val latitude: Double, val longitude: Double)

interface UserRepository {
    val currentPublicKey: StateFlow<String>

    suspend fun requestPublicKey()

    fun updatePublicKey(key: String)
}