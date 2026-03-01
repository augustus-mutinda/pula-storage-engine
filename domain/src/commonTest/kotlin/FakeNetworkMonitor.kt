import io.pula.data.network.NetworkMonitor

class FakeNetworkMonitor(
    private var available: Boolean
) : NetworkMonitor {

    override fun isAvailableForSync(): Boolean = available

    fun setAvailable(value: Boolean) {
        available = value
    }
}

class FlakyNetworkMonitor(
    private val failAfterCalls: Int
) : NetworkMonitor {

    private var callCount = 0

    override fun isAvailableForSync(): Boolean {
        callCount++
        return callCount <= failAfterCalls
    }
}

