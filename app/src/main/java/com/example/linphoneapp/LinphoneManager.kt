package com.example.linphoneapp

import android.Manifest
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.annotation.RequiresPermission
import com.example.linphoneapp.BottomNav.notification.NotificationActionReceiver
import com.example.linphoneapp.BottomNav.notification.NotificationHelper
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.linphone.core.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LinphoneManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    lateinit var core: Core
    private lateinit var coreListener: CoreListenerStub
    val registrationState = MutableStateFlow<RegistrationState?>(null)
    val incomingCall = MutableSharedFlow<Call>(replay = 1,
        extraBufferCapacity = 1)
    val connected = MutableSharedFlow<Boolean>(replay = 1,
        extraBufferCapacity = 1)
    private val _recentCalls = MutableStateFlow<List<CallLog>>(emptyList())
    val recentCalls: StateFlow<List<CallLog>> = _recentCalls.asStateFlow()
    fun init() {
        if (!::core.isInitialized) {
            Factory.instance().setDebugMode(true, "Linphone")
            core = Factory.instance().createCore(null, null, context)
            setupListener()
            core.addListener(coreListener)
            core.start()
        }
    }

    fun registerAccount(username: String, password: String, domain: String) {
        init()
        refreshCallLogs()
        Log.d("TAG>>", "registerAccount: " + username)
        // Create auth info
        val authInfo = Factory.instance().createAuthInfo(
            username,
            null,
            password,
            null,
            null,
            domain,
            null
        )
        core.addAuthInfo(authInfo)

        // Configure account params
        val accountParams = core.createAccountParams()
        val identity = Factory.instance().createAddress("sip:$username@$domain")
        accountParams.identityAddress = identity

        val serverAddr = Factory.instance().createAddress("sip:$domain")
        serverAddr!!.transport = TransportType.Tcp
        accountParams.serverAddress = serverAddr

        accountParams.isRegisterEnabled = true

        val account = core.createAccount(accountParams)

        core.addAuthInfo(authInfo)
        core.addAccount(account)

        core.defaultAccount = account
        core.addListener(coreListener)
        core.start()

        // Finally we need the Core to be started for the registration to happen (it could have been started before)
        core.start()

    }

    private fun setupListener() {
        coreListener = object : CoreListenerStub() {
            override fun onRegistrationStateChanged(
                core: Core,
                proxyConfig: ProxyConfig,
                state: RegistrationState?,
                message: String
            ) {
                registrationState.value = state
                Log.d("TAG>>", "registerAccount: " + state)

            }

            @RequiresPermission(Manifest.permission.POST_NOTIFICATIONS)
            override fun onCallStateChanged(
                core: Core,
                call: Call,
                state: Call.State?,
                message: String
            ) {
                when (state) {
                    Call.State.Updating ->{
                        Log.d("TAG>>", "onCallStateChanged: updateing "+state)
                    }
                    Call.State.OutgoingInit -> {
                        refreshCallLogs()
                        Log.d("TAG>>", "onCallStateChanged: OutgoingInit "+state)

                        // First state an outgoing call will go through
                    }

                    Call.State.OutgoingProgress -> {
                        Log.d("TAG>>", "onCallStateChanged: OutgoingProgress "+state)

                        // Right after outgoing init


                    }

                    Call.State.OutgoingRinging -> {
                        Log.d("TAG>>", "onCallStateChanged: OutgoingRinging "+state)

                        // This state will be reached upon reception of the 180 RINGING
                    }

                    Call.State.Connected -> {

                        connected.tryEmit(true)

                        Log.d("TAG>>", "onCallStateChanged: connected"+state)


                        // When the 200 OK has been received
                    }

                    Call.State.StreamsRunning -> {
                        Log.d("TAG>>", "onCallStateChanged: StreamsRunning"+state)

                        // This state indicates the call is active.
                        // You may reach this state multiple times, for example after a pause/resume
                        // or after the ICE negotiation completes
                        // Wait for the call to be connected before allowing a call update


                        // For video to work, we need two TextureViews:
                        // one for the remote video and one for the local preview

                    }

                    Call.State.Paused -> {
                        // When you put a call in pause, it will became Paused

                    }

                    Call.State.PausedByRemote -> {
                        // When the remote end of the call pauses it, it will be PausedByRemote
                    }



                    Call.State.UpdatedByRemote -> {
                        // When the remote requests a call update
                    }
                    Call.State.Released -> {
                        refreshCallLogs()

                        // Call state will be released shortly after the End state
                    }
                    Call.State.IncomingReceived -> {
                        incomingCall.tryEmit(call)
                        Log.d("TAG>>", "onCallStateChanged: " + incomingCall.tryEmit(call))

                        val acceptIntent = PendingIntent.getBroadcast(
                            context,
                            0,
                            Intent(context, NotificationActionReceiver::class.java).apply {
                                action = "ACTION_ACCEPT_CALL"
                                putExtra("caller", call.remoteAddress.toString())
                            },
                            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                        )

                        val rejectIntent = PendingIntent.getBroadcast(
                            context,
                            1,
                            Intent(context, NotificationActionReceiver::class.java).apply {
                                action = "ACTION_REJECT_CALL"
                                putExtra("caller", call.remoteAddress.toString())
                            },
                            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                        )
                        NotificationHelper.showIncomingCall(context, call.remoteAddress.username.toString(),acceptIntent,rejectIntent)
                    }

                    else -> {
                        Log.d("TAG>>", "onCallStateChanged: else"+state)
                        refreshCallLogs()

                    }
                }
            }
        }
    }

    fun stop() {
        if (::core.isInitialized) core.stop()
    }

    fun makeCall(number: String) {

        Log.d("TAG>>", "makeCall: "+number)

        core.let { core ->
            // Interpret the SIP URL (for GSM call)
            val address = core.interpretUrl(number)
            // Create call parameters
            val params = core.createCallParams(null)
            // Add custom headers
            params?.let { core.inviteAddressWithParams(address!!, it) }

        }

    }

    fun hangUp() {
        core.currentCall?.terminate()
    }


    fun accept() {
        core.currentCall?.accept()
    }




    private fun refreshCallLogs() {
        // Wait a little to make sure the call log is updated in core
        CoroutineScope(Dispatchers.Main).launch {
            delay(500)  // 500ms delay
            _recentCalls.value = core.callLogs.toList()
        }
    }

}