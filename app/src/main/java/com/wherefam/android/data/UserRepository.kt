package com.wherefam.android.data

import kotlinx.coroutines.flow.StateFlow


interface UserRepository {
    val currentPublicKey: StateFlow<String>

    suspend fun requestPublicKey()
    suspend fun joinPeer(key: String)

    fun updatePublicKey(key: String)
}