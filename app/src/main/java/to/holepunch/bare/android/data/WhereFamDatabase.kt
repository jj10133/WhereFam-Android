package to.holepunch.bare.android.data

import androidx.room.Database
import androidx.room.RoomDatabase
import to.holepunch.bare.android.data.local.Peer

@Database(
    entities = [Peer::class],
    version = 1,
    exportSchema = true
)
abstract class WhereFamDatabase : RoomDatabase() {
    abstract val peerRepository: PeerRepository
}