package com.example.linphoneapp.viewmodel

import androidx.lifecycle.ViewModel
import com.example.linphoneapp.LinphoneManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import javax.inject.Inject

@HiltViewModel
class CallViewModel @Inject constructor(
    private val linphoneManager: LinphoneManager
) : ViewModel() {

    val incomingCalls = linphoneManager.incomingCall
    val connect : MutableSharedFlow<Boolean> = linphoneManager.connected

//    fun init() {
//        linphoneManager.init()
//    }

    fun call(sip: String) = linphoneManager.makeCall(sip)
    fun hangUp() = linphoneManager.hangUp()

    fun accept() = linphoneManager.accept()


}