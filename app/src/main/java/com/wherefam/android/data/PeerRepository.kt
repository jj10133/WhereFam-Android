package com.wherefam.android.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import androidx.room.Upsert
import com.wherefam.android.data.local.Peer
import kotlinx.coroutines.flow.Flow

@Dao
interface PeerRepository {
    @Upsert
    suspend fun upsert(peer: Peer)

    @Delete
    suspend fun delete(peer: Peer)

    @Query("SELECT * FROM Peer")
    fun getAllPeers(): Flow<List<Peer>>
}