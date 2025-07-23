package com.example.linphoneapp

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable

import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import androidx.navigation.compose.rememberNavController

import com.example.linphoneapp.viewmodel.CallViewModel
import dagger.hilt.android.AndroidEntryPoint

import androidx.hilt.navigation.compose.hiltViewModel

import com.example.telemomobileapp.Navigation.VoipApp

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {

            PermissionRequestScreen()

            val navController = rememberNavController()
            val callViewModel: CallViewModel = hiltViewModel()

            LaunchedEffect(Unit) {
                callViewModel.incomingCalls.collect { call ->
                    Log.d("TAG>>", "onCreatecall: "+call.remoteAddress.username)

                    val caller = call.remoteAddress.username ?: "Unknown"
                    navController.navigate("incoming/$caller")
                }
            }


            VoipApp(navController,callViewModel)


        }
    }


}

@Composable
private fun PermissionRequestScreen() {
    val context = LocalContext.current

    val permissions = listOf(
        Manifest.permission.RECORD_AUDIO,
        Manifest.permission.READ_CONTACTS,
        Manifest.permission.POST_NOTIFICATIONS, // for Android 13+
        Manifest.permission.CALL_PHONE // optional, if you’re initiating calls
    )

    val permissionLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { result ->
            result.forEach { (perm, granted) ->
                Log.d("PERMISSION", "$perm -> granted: $granted")
            }
        }

    LaunchedEffect(Unit) {
        if (!context.hasAllPermissions( permissions)) {
            permissionLauncher.launch(permissions.toTypedArray())
        }
    }

    // Your app’s UI here
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text("Welcome to the app! Permissions requested on launch.")
    }}

fun Context.hasAllPermissions( permissions: List<String>): Boolean {
    return permissions.all {
        ContextCompat.checkSelfPermission(this, it) == PackageManager.PERMISSION_GRANTED
    }
    }
