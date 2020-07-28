package com.example.beerdistrkt.service

import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.media.MediaPlayer
import android.os.IBinder
import android.provider.Settings
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.beerdistrkt.MainActivity
import com.example.beerdistrkt.R
import com.example.beerdistrkt.storage.SharedPreferenceDataSource
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class NotificationService : Service() {

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        return START_STICKY
    }

    override fun onCreate() {
        super.onCreate()

        Log.d("msg", "onCreate Servic")
        myNotificationInterface?.getComments()

        val messageRef =
            FirebaseDatabase.getInstance().getReference(getString(R.string.location_en))
        messageRef.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(error: DatabaseError) {}

            override fun onDataChange(snapshot: DataSnapshot) {
                Log.d("msg", "inServise data-snap ${snapshot.value}")

                SharedPreferenceDataSource.initialize(applicationContext)
                val lastValue = SharedPreferenceDataSource.getInstance().getLastMsgDate()

                if (snapshot.value != null) {
                    val text: String = snapshot.value.toString()
                    if (lastValue != text && lastValue.isNotEmpty()) {
                        if (myNotificationInterface == null) {
                            Log.d("msg", "interf is null")
//                                myNotificationInterface = MainActivity
                        } else {
                            myNotificationInterface?.getComments()
                        }
                        val player = MediaPlayer.create(
                            applicationContext,
                            Settings.System.DEFAULT_NOTIFICATION_URI
                        )
                        player.start()
                        displayNotification(text)
//                        MainActivity.NEED_COMENTS_UPDATE = true
                    }
                    SharedPreferenceDataSource.getInstance().saveLastMsgDate(text)
                }

            }

        })

    }

    fun displayNotification(text: String) {
        val notificationManager = NotificationManagerCompat.from(this)

        val intent = Intent(this, MainActivity::class.java)
//        intent.putExtra("ID", msg.notificationID)
        val pendingIntent =
            PendingIntent.getActivity(this, 123, intent, PendingIntent.FLAG_CANCEL_CURRENT)

        val notification = NotificationCompat.Builder(
            this,
            getString(R.string.channel_id)
        )
            .setSmallIcon(R.drawable.ic_comment_icon)
            .setContentTitle(getString(R.string.notif_title))
            .setContentText(text)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setCategory(NotificationCompat.CATEGORY_MESSAGE)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()

        notificationManager.notify(1, notification)
    }

    interface NotificationInterface {
        fun getComments()
    }

//    private var nInstance: NotificationInterface? = null

    companion object {
        const val TAG = "notif_Service"

        var myNotificationInterface: NotificationInterface? = null
    }
}