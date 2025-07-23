package com.example.linphoneapp.service

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.example.linphoneapp.LinphoneManager
import com.example.linphoneapp.R
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class LinphoneService : Service() {
    @Inject
    lateinit var linphoneManager: LinphoneManager

    override fun onCreate() {
        super.onCreate()
        startForegroundService()
        linphoneManager.init()
    }

    override fun onDestroy() {
        linphoneManager.stop()
        super.onDestroy()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int) :Int{
        when (intent?.action) {
            "ACCEPT_CALL" -> {
                linphoneManager.accept()
            }
            "REJECT_CALL" -> {
                linphoneManager.hangUp()
            }
        }
        return START_STICKY
    }
    override fun onBind(intent: Intent?): IBinder? = null


    @SuppressLint("ForegroundServiceType")
    private fun startForegroundService() {
        val channelId = "voip_channel"
        val notifManager = getSystemService(NotificationManager::class.java)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val chan =
                NotificationChannel(channelId, "VoIP Service", NotificationManager.IMPORTANCE_LOW)
            notifManager.createNotificationChannel(chan)
        }
        val notif = NotificationCompat.Builder(this, channelId)
            .setContentTitle("VoIP Service")
            .setContentText("Linphone running")
            .setSmallIcon(R.drawable.ic_launcher_background)
            .build()
        startForeground(1, notif)
    }
}