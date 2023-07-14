package com.example.beerdistrkt

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build

class ApeniApp : Application() {

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                getString(R.string.notif_channel_id),
                getString(R.string.notif_channel_name),
                NotificationManager.IMPORTANCE_HIGH
            )
            channel.description = getString(R.string.channel_description)

            val serviceIndicationChannel = NotificationChannel(
                getString(R.string.service_channel_id),
                getString(R.string.foreground_service_name),
                NotificationManager.IMPORTANCE_LOW
            )

            getSystemService(NotificationManager::class.java).createNotificationChannels(
                listOf(
                    serviceIndicationChannel,
                    channel
                )
            )
        }
    }

}