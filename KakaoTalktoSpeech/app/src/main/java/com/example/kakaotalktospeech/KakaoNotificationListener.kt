package com.example.kakaotalktospeech

import android.app.Notification
import android.content.Intent
import android.os.IBinder
import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import android.speech.tts.TextToSpeech
import android.util.Log

class KakaoNotificationListener : NotificationListenerService() {


    override fun onNotificationPosted(sbn: StatusBarNotification?) {

        super.onNotificationPosted(sbn)
        val packageName= sbn?.packageName
        if(packageName.equals("com.kakao.talk")){

            val extras = sbn?.notification?.extras
            val sender= extras?.getString(Notification.EXTRA_TITLE)
            val message = extras?.getCharSequence(Notification.EXTRA_TEXT)
            val subText = extras?.getCharSequence(Notification.EXTRA_SUB_TEXT)

            if (sender!=null && message!=null&&subText==null){
                // theses messages are not from group chat(subText==null)
                Log.d("myTEST", "kakaotalk message is received.")
               // Log.d("myTEST", " - sender: $sender")
              //  Log.d("myTEST", " - message: $message")
              //  Log.d("myTEST", " - subText: $subText")

                val showIntent= Intent()
                showIntent.setAction("com.broadcast.notification")
                showIntent.putExtra("sender",sender)
                showIntent.putExtra("message",message)
                sendBroadcast(showIntent)
            }
        }
    }


}