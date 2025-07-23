package com.example.linphoneapp.BottomNav.notification

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.content.ContextCompat
import com.example.linphoneapp.MainActivity
import com.example.linphoneapp.service.LinphoneService

class NotificationActionReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {

        if (intent.action == Intent.ACTION_BOOT_COMPLETED) {
            val serviceIntent = Intent(context, LinphoneService::class.java)
            ContextCompat.startForegroundService(context, serviceIntent)
        }
        when (intent.action) {
            "ACTION_ACCEPT_CALL" -> {
                val serviceIntent = Intent(context, LinphoneService::class.java).apply {
                    action = "ACCEPT_CALL"
                }
                ContextCompat.startForegroundService(context, serviceIntent)

                val launchIntent = Intent(context, MainActivity::class.java).apply {
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
                    putExtra("navigateTo", "incoming")
                }
                context.startActivity(launchIntent)

                NotificationHelper.cancel(context)
            }

            "ACTION_REJECT_CALL" -> {
                val serviceIntent = Intent(context, LinphoneService::class.java).apply {
                    action = "REJECT_CALL"
                }
                ContextCompat.startForegroundService(context, serviceIntent)
                NotificationHelper.cancel(context)
            }
        }
    }
}
