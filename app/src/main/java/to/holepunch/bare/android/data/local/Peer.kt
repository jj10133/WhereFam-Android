package to.holepunch.bare.android.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Peer(
    @PrimaryKey
    val id: String,
    val name: String?,
    val latitude: Double?,
    val longitude: Double?
)
