package to.holepunch.bare.android.processing

interface MessageProcessor {
    suspend fun processMessage(message: String)
}