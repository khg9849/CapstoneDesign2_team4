package com.example.kakaotalktospeech

import android.app.*
import android.content.Intent
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import com.example.kakaotalktospeech.ActionManager.Companion.NOTIFICATION_UPDATE_START
import com.example.kakaotalktospeech.ActionManager.Companion.NOTIFICATION_UPDATE_STOP
import com.example.kakaotalktospeech.ActionManager.Companion.sendUpdateWidgetIntent
import com.example.kakaotalktospeech.ActionManager.Companion.updatePreferences


class NotificationService : Service() {
    var notificationId=1
    var isChanneled=false
    val channel_id="channel_id"

    override fun onBind(intent: Intent): IBinder {
        TODO("Return the communication channel to the service.")
    }

    fun setNotificationChannel(){
        val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager

        // Create and register the NotificationChannel
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val mChannel = NotificationChannel(channel_id, "channel_name", NotificationManager.IMPORTANCE_DEFAULT)
            mChannel.description = "channel_description"
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

        SettingManager.isRunning=(intent?.action==NOTIFICATION_UPDATE_START)
        makeNotification()
        sendUpdateWidgetIntent(this)
        updatePreferences()

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
            builder.addAction(makeButtonInNotification(NOTIFICATION_UPDATE_STOP,"STOP"))
        }
        else{
            builder.setContentText("APP is not running")
            builder.addAction(makeButtonInNotification(NOTIFICATION_UPDATE_START,"START"))
        }

        val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(notificationId,builder.build())
    }

    fun makeButtonInNotification(action:String,btnTitle:String): NotificationCompat.Action {
        val intent = Intent(baseContext, NotificationService::class.java)
        intent.setAction(action)
        val pendingIntent =
            PendingIntent.getService(baseContext, 1, intent, 0)
        val iconId=android.R.drawable.ic_media_pause
        return NotificationCompat.Action.Builder(iconId,btnTitle,pendingIntent).build()
    }
}

