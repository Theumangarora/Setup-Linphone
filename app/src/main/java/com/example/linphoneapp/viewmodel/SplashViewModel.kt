package com.example.linphoneapp.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import com.example.linphoneapp.LinphoneManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class SplashViewModel @Inject constructor(
     val linphoneManager: LinphoneManager
)  : ViewModel() {

    val regState = linphoneManager.registrationState.asStateFlow()

    fun isLoggedIn(context: Context): Boolean {
        val prefs = context.getSharedPreferences("prefs", Context.MODE_PRIVATE)
        return prefs.getBoolean("logged_in", false)
    }

    fun data(context: Context) {
        val prefs = context.getSharedPreferences("prefs", Context.MODE_PRIVATE)
         val username = prefs.getString("username","")
         val pass = prefs.getString("password","")

    }
}