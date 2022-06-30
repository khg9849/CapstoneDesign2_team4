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
    private val notificationId=1
    private var isChanneled=false
    private val channel_id="channel_id"
    private val channel_name="channel_name"
    private val channel_description="channel_description"

    override fun onBind(intent: Intent): IBinder {
        TODO("Return the communication channel to the service.")
    }

    // Notification 채널 생성 및 등록
    private fun setNotificationChannel(){
        val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val mChannel = NotificationChannel(channel_id, channel_name, NotificationManager.IMPORTANCE_LOW)
            mChannel.description = channel_description
            mChannel.vibrationPattern = longArrayOf(0) // 노티바를 켤 때 진동을 끔
            mChannel.enableVibration(true)
            notificationManager.createNotificationChannel(mChannel)
        }
    }

    // 노티바 생성
    private fun makeNotification(isRunning:Boolean) {
        val builder = NotificationCompat.Builder(this, channel_id)
        builder.setPriority(NotificationCompat.PRIORITY_HIGH)
        builder.setSmallIcon(R.drawable.ic_megaphone)
        builder.setOngoing(true)

        if (isRunning) {
            builder.setContentText("앱이 실행되고 있습니다")
            builder.addAction(makeButtonInNotibar(NOTIBAR_UPDATE_STOP,"사용 중지"))
        }
        else{
            builder.setContentText("앱이 종료되었습니다")
            builder.addAction(makeButtonInNotibar(NOTIBAR_UPDATE_START,"사용 시작"))
        }

        val notibarManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        notibarManager.notify(notificationId,builder.build())
    }

    // 노티바의 노티리더 활성화 버튼 설정
    private fun makeButtonInNotibar(action:String, btnTitle:String): NotificationCompat.Action {
        val intent = Intent(baseContext, NotibarService::class.java)
        intent.setAction(action)
        val pendingIntent =
            PendingIntent.getService(baseContext, 1, intent, PendingIntent.FLAG_IMMUTABLE)
        val iconId=R.drawable.ic_megaphone
        return NotificationCompat.Action.Builder(iconId,btnTitle,pendingIntent).build()
    }


    // 노티바 액션 정의
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {

        if(isChanneled==false){
            setNotificationChannel()
            isChanneled=true
        }

        when(intent?.action){
            // 노티바를 켰을 경우 노티바 생성
            NOTIBAR_CREATE-> {
                val isRunning=intent?.getBooleanExtra("isRunning",false)
                makeNotification(isRunning)
            }
            // 노티바를 통해 노티리더를 활성화한 경우
            NOTIBAR_UPDATE_START-> {
                updateIsRunning(true)
                makeNotification(true)
            }
            // 노티바를 통해 노티리더를 끈 경우
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
}

