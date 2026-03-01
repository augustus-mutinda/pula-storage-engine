package io.pula.data.network


interface NetworkMonitor {
    /**
     * Returns true if the network conditions are suitable for a sync attempt.
     * Could include checks for connectivity, battery, metered connection, etc.
     */
    fun isAvailableForSync(): Boolean
}