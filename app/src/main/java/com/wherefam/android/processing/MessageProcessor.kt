package com.wherefam.android.processing

interface MessageProcessor {
    suspend fun processMessage(message: String)
}