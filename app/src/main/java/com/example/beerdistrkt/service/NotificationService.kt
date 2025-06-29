package com.example.beerdistrkt.service

import android.Manifest
import android.app.Notification
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.content.pm.PackageManager
import android.media.MediaPlayer
import android.os.Build
import android.os.IBinder
import android.provider.Settings
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.beerdistrkt.BuildConfig
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
        startForeground(1234, permanentNotification())
        return START_STICKY
    }

    private fun permanentNotification(): Notification = NotificationCompat.Builder(
        this,
        getString(R.string.service_channel_id),
    )
        .setSmallIcon(R.drawable.logo)
        .setContentText("service for notifications")
        .build()

    override fun onCreate() {
        super.onCreate()

        val messageRef = FirebaseDatabase.getInstance().getReference(BuildConfig.FLAVOR)
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
                        val msg = text.substring(text.indexOf("|") + 1)
                        displayNotification(msg)
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

        val flag = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
            PendingIntent.FLAG_IMMUTABLE
        else
            PendingIntent.FLAG_CANCEL_CURRENT

        val pendingIntent = PendingIntent.getActivity(this, 123, intent, flag)

        val notification = NotificationCompat.Builder(
            this,
            getString(R.string.notif_channel_id)
        )
            .setSmallIcon(R.drawable.ic_comment_icon)
            .setContentTitle(getString(R.string.notif_title))
            .setContentText(text)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setCategory(NotificationCompat.CATEGORY_MESSAGE)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()

        if (checkSelfPermission(PERMISSION_RM) == PackageManager.PERMISSION_GRANTED) {
            notificationManager.notify(1, notification)
        } else {
//            TODO("REQUEST PERMISSION")
        }
    }

    interface NotificationInterface {
        fun getComments()
    }

//    private var nInstance: NotificationInterface? = null

    companion object {
        const val TAG = "notification_Service"
        @RequiresApi(Build.VERSION_CODES.UPSIDE_DOWN_CAKE)
        const val PERMISSION_RM = Manifest.permission.FOREGROUND_SERVICE_REMOTE_MESSAGING

        var myNotificationInterface: NotificationInterface? = null
    }
}