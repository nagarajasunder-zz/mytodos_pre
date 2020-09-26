package com.geekydroid.mytodos

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager

class Notification_Creator() : Application() {

    companion object {
        val CHANNEL_ID = "MYTODOS"
    }

    override fun onCreate() {
        super.onCreate()
        create_channel()
    }

    private fun create_channel() {
        val channel =
            NotificationChannel(CHANNEL_ID, "THIS IS MYTODO", NotificationManager.IMPORTANCE_HIGH)
        val manager = getSystemService(
            NotificationManager::class.java
        )
        manager.createNotificationChannel(channel)
    }

}