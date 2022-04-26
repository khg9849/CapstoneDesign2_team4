package com.example.kakaotalktospeech

import android.app.*
import android.content.Intent
import android.os.Build
import android.os.IBinder
import android.util.Log
import android.widget.Toast
import androidx.core.app.NotificationCompat


class NotificationService : Service() {
    var notificationId=1

    var isChanneled=false
    val channel_id = "channel_id"
    val channel_name = "channel_name"
    val channel_description = "channel_description"
    val channel_importance = NotificationManager.IMPORTANCE_DEFAULT

    override fun onBind(intent: Intent): IBinder {
        TODO("Return the communication channel to the service.")
    }

    fun setNotificationChannel(){
        val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // Create the NotificationChannel
            val mChannel = NotificationChannel(channel_id, channel_name, channel_importance)
            mChannel.description = channel_description
            // Register the channel with the system;
            notificationManager.createNotificationChannel(mChannel)
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.d("myTEST", "onStartCommand()")

        if(isChanneled==false){
            setNotificationChannel()
            makeNotification()
            isChanneled=true
        }

        when(intent?.action){
            "Start"->{
                Toast.makeText(this,"Start is Clicked",Toast.LENGTH_SHORT).show()
                SettingManager.isRunning=true
                makeNotification()
            }
            "Stop"->{
                Toast.makeText(this,"Stop is Clicked",Toast.LENGTH_SHORT).show()
                SettingManager.isRunning=false
                makeNotification()
            }
        }
        return super.onStartCommand(intent, flags, startId)
    }

    override fun onDestroy() {
        val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.cancel(notificationId)
        super.onDestroy()
    }

    fun makeNotification() {

        val builder = NotificationCompat.Builder(this, channel_id)
        builder.setPriority(NotificationCompat.PRIORITY_HIGH)
        builder.setSmallIcon(R.drawable.ic_launcher_foreground)
        builder.setOngoing(true)

        builder.setContentTitle("KakaoTalkToSpeech")
        if (SettingManager.isRunning) {
            builder.setContentText("APP is running")
            builder.addAction(makeButtonInNotification("Stop"))
        }
        else{
            builder.setContentText("APP is not running")
            builder.addAction(makeButtonInNotification("Start"))
        }

        val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(notificationId,builder.build())
    }

    fun makeButtonInNotification(action:String): NotificationCompat.Action {
        val intent = Intent(baseContext, NotificationService::class.java)
        intent.setAction(action)
        val pendingIntent =
            PendingIntent.getService(baseContext, 1, intent, 0)
        val iconId=android.R.drawable.ic_media_pause
        val btnTitle=action
        return NotificationCompat.Action.Builder(iconId,btnTitle,pendingIntent).build()
    }


}

