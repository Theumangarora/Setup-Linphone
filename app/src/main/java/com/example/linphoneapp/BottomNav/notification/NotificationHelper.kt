package com.example.linphoneapp.BottomNav.notification

import android.Manifest
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.os.Build
import androidx.annotation.RequiresPermission
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.linphoneapp.R

object NotificationHelper {
    const val CHANNEL_ID = "calls"
    const val NOTIFICATION_ID = 1

    fun createChannel(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Incoming Calls",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Notifications for incoming calls"
                lockscreenVisibility = Notification.VISIBILITY_PUBLIC
            }
            val mgr = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            mgr.createNotificationChannel(channel)
        }
    }

    @RequiresPermission(Manifest.permission.POST_NOTIFICATIONS)
    fun showIncomingCall(
        context: Context,
        caller: String,
        acceptIntent: PendingIntent,
        rejectIntent: PendingIntent
    ) {
        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setContentTitle("Incoming Call")
            .setContentText(caller)
            .setSmallIcon(R.drawable.ic_launcher_background) // your icon
            .setPriority(NotificationCompat.PRIORITY_MAX)
            .setCategory(NotificationCompat.CATEGORY_CALL)
            .setOngoing(true)
            .setAutoCancel(false)
            .addAction(R.drawable.ic_launcher_background, "Reject", rejectIntent)
            .addAction(R.drawable.ic_launcher_background, "Accept", acceptIntent)
            .setFullScreenIntent(acceptIntent, true)
            .build()

        val mgr = NotificationManagerCompat.from(context)
        mgr.notify(NOTIFICATION_ID, notification)
    }

    fun cancel(context: Context) {
        val mgr = NotificationManagerCompat.from(context)
        mgr.cancel(NOTIFICATION_ID)
    }
}