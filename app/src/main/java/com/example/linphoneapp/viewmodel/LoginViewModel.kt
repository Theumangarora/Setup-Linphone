package com.example.linphoneapp.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import com.example.linphoneapp.LinphoneManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val linphoneManager: LinphoneManager
) : ViewModel() {
    val regState = linphoneManager.registrationState.asStateFlow()
    fun register(u: String, p: String) {
        linphoneManager.registerAccount(u,p,"34.196.51.80")
    }

    fun saveCredentials(context: Context, username: String, password: String) {
        val prefs = context.getSharedPreferences("prefs", Context.MODE_PRIVATE)
        prefs.edit()
            .putString("username", username)
            .putString("password", password)
            .putString("domain", "your domain")
            .putBoolean("logged_in", true)
            .apply()
    }
}
