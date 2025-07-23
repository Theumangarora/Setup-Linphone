package com.example.telemomobileapp.Utils

import android.Manifest
import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import androidx.annotation.RequiresPermission
import androidx.core.content.ContextCompat
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow


enum class NetworkStatus {
    Connected,
    Disconnected,
    Unstable
}

object NetworkStatusTracker {
    private val _networkStatus = MutableStateFlow(NetworkStatus.Connected)
    val networkStatus: StateFlow<NetworkStatus> = _networkStatus.asStateFlow()

    @RequiresPermission(Manifest.permission.ACCESS_NETWORK_STATE)
    fun startNetworkMonitoring(context: Context) {
        val connectivityManager = ContextCompat.getSystemService(context, ConnectivityManager::class.java)
        val networkCallback = object : ConnectivityManager.NetworkCallback() {
            override fun onAvailable(network: Network) {
                _networkStatus.value = NetworkStatus.Connected
            }

            override fun onLost(network: Network) {
                _networkStatus.value = NetworkStatus.Disconnected
            }

            override fun onCapabilitiesChanged(network: Network, networkCapabilities: NetworkCapabilities) {
                val isValidated = networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED)
                val hasInternet = networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
                _networkStatus.value = when {
                    isValidated && hasInternet -> NetworkStatus.Connected
                    hasInternet -> NetworkStatus.Unstable
                    else -> NetworkStatus.Disconnected
                }
            }
        }

        val request = NetworkRequest.Builder()
            .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
            .build()

        connectivityManager?.registerNetworkCallback(request, networkCallback)

        // Initial network status check
        val activeNetwork = connectivityManager?.activeNetwork
        val capabilities = connectivityManager?.getNetworkCapabilities(activeNetwork)
        _networkStatus.value = when {
            capabilities?.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) == true &&
                    capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED) -> NetworkStatus.Connected
            capabilities?.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) == true -> NetworkStatus.Unstable
            else -> NetworkStatus.Disconnected
        }
    }

    fun stopNetworkMonitoring(context: Context) {
        val connectivityManager = ContextCompat.getSystemService(context, ConnectivityManager::class.java)
        // Unregistering is handled in the composable's DisposableEffect, so this is a placeholder for manual cleanup if needed
    }
}