package io.pula.data.network

interface NetworkMonitor {
    fun isLikelyAvailable(): Boolean
}