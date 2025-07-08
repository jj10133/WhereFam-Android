package to.holepunch.bare.android.data

import kotlinx.coroutines.flow.StateFlow


data class LocationData(val id: String, val name: String, val latitude: Double, val longitude: Double)

interface UserRepository {
    val currentPublicKey: StateFlow<String>
    val locationUpdates: StateFlow<List<LocationData>>

    suspend fun requestPublicKey()
    suspend fun joinPeer(key: String)

    fun updatePublicKey(key: String)

    fun addLocationUpdate(location: LocationData)
}