package com.example.linphoneapp

import android.app.Application
import com.example.linphoneapp.BottomNav.notification.NotificationHelper
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class LinphoneApplication: Application(){
    override fun onCreate() {

        super.onCreate()
        NotificationHelper.createChannel(this)

    }
}
