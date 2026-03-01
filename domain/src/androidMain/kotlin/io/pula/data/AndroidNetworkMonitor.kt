package io.pula.data

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import io.pula.data.network.NetworkMonitor

class AndroidNetworkMonitor(
    private val context: Context
) : NetworkMonitor {

    override fun isLikelyAvailable(): Boolean {
        val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val network = cm.activeNetwork ?: return false
        val capabilities = cm.getNetworkCapabilities(network) ?: return false
        return capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
    }
}