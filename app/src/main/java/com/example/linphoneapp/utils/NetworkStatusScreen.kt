package com.example.telemomobileapp.Utils

import android.widget.Toast
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.*
import androidx.compose.ui.platform.LocalContext

@Composable
fun NetworkStatusScreen() {
    val context = LocalContext.current
    val networkStatusState = NetworkStatusTracker.networkStatus.collectAsState()
    val networkStatus = networkStatusState.value
    // Start network monitoring when the composable is composed
    DisposableEffect(context) {
        NetworkStatusTracker.startNetworkMonitoring(context)
        onDispose {
            // Cleanup is handled automatically by ConnectivityManager when the callback is unregistered
        }
    }

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        when (networkStatus) {
            NetworkStatus.Connected -> {
               // Toast.makeText(context,"Internet Connected", Toast.LENGTH_SHORT).show()
            }
            NetworkStatus.Disconnected -> {
                Toast.makeText(context,"No Internet Connection", Toast.LENGTH_SHORT).show()

            }
            NetworkStatus.Unstable -> {
                Toast.makeText(context,"Unstable Internet Connection", Toast.LENGTH_SHORT).show()

            }
        }
    }
}