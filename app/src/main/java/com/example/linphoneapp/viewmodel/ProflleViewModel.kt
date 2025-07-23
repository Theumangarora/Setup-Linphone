package com.example.linphoneapp.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import com.example.linphoneapp.LinphoneManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class ProflleViewModel @Inject constructor(
    private val linphoneManager: LinphoneManager
) : ViewModel()  {

    fun stop() =linphoneManager.stop()

     fun clearLoginSession(context: Context) {
        CoroutineScope(Dispatchers.IO).
        launch {
            val prefs = context.getSharedPreferences("prefs", Context.MODE_PRIVATE)
            prefs.edit().clear().apply()
        }
    }

}