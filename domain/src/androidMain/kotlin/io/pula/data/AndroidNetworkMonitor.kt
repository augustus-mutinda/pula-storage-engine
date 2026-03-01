package io.pula.data

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import io.pula.data.network.NetworkMonitor

class AndroidNetworkMonitor() : NetworkMonitor {
    override fun isAvailableForSync(): Boolean {
        val cm = (getPlatformContext() as Context).getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val network = cm.activeNetwork ?: return false
        val capabilities = cm.getNetworkCapabilities(network) ?: return false

        val hasInternet = capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
        val notRoaming = !capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_NOT_ROAMING)
        val notLowData = !capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_NOT_METERED)

        // You can customize your rules for low battery, roaming, etc.
        return hasInternet && notRoaming && notLowData
    }
}