package com.example.kakaotalktospeech

import android.app.*
import android.content.Intent
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.example.kakaotalktospeech.ActionManager.Companion.NOTIBAR_CREATE
import com.example.kakaotalktospeech.ActionManager.Companion.NOTIBAR_UPDATE_START
import com.example.kakaotalktospeech.ActionManager.Companion.NOTIBAR_UPDATE_STOP
import com.example.kakaotalktospeech.ActionManager.Companion.sendUpdateWidgetIntent
import com.example.kakaotalktospeech.ActionManager.Companion.updateIsRunning
import com.example.kakaotalktospeech.ActionManager.Companion.updatePreferences


class NotibarService : Service() {
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
            val mChannel = NotificationChannel(channel_id, "channel_name", NotificationManager.IMPORTANCE_LOW)
            mChannel.description = "channel_description"
            mChannel.vibrationPattern = longArrayOf(0) // turn off vibration
            mChannel.enableVibration(true)
            notificationManager.createNotificationChannel(mChannel)
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {

        if(isChanneled==false){
            setNotificationChannel()
            isChanneled=true
        }

        when(intent?.action){
            NOTIBAR_CREATE-> {
                val isRunning=intent?.getBooleanExtra("isRunning",false)
                makeNotification(isRunning)
            }
            NOTIBAR_UPDATE_START-> {
                updateIsRunning(true)
                makeNotification(true)
            }
            NOTIBAR_UPDATE_STOP-> {
                updateIsRunning(false)
                makeNotification(false)
            }
        }

        sendUpdateWidgetIntent(this)
        updatePreferences()

        return super.onStartCommand(intent, flags, startId)
    }


    override fun onDestroy() {
        val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.cancel(notificationId)
        super.onDestroy()
    }

    fun makeNotification(isRunning:Boolean) {
        val builder = NotificationCompat.Builder(this, channel_id)
        builder.setPriority(NotificationCompat.PRIORITY_HIGH)
        builder.setSmallIcon(R.drawable.ic_launcher_foreground)
        builder.setOngoing(true)

        builder.setContentTitle("KakaoTalkToSpeech")
        if (isRunning) {
            builder.setContentText("APP is running")
            builder.addAction(makeButtonInNotibar(NOTIBAR_UPDATE_STOP,"STOP"))
        }
        else{
            builder.setContentText("APP is not running")
            builder.addAction(makeButtonInNotibar(NOTIBAR_UPDATE_START,"START"))
        }

        val notibarManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        notibarManager.notify(notificationId,builder.build())
    }

    fun makeButtonInNotibar(action:String, btnTitle:String): NotificationCompat.Action {
        val intent = Intent(baseContext, NotibarService::class.java)
        intent.setAction(action)
        val pendingIntent =
            PendingIntent.getService(baseContext, 1, intent, PendingIntent.FLAG_IMMUTABLE)
        val iconId=android.R.drawable.ic_media_pause
        return NotificationCompat.Action.Builder(iconId,btnTitle,pendingIntent).build()
    }
}

