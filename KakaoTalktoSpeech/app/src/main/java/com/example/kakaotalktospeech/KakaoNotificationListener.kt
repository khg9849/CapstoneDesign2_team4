package com.example.kakaotalktospeech

import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import android.util.Log

class KakaoNotificationListener : NotificationListenerService() {


    override fun onNotificationPosted(sbn: StatusBarNotification?) {
        super.onNotificationPosted(sbn)
        val packageName= sbn?.packageName
        if(packageName.equals("com.kakao.talk")){
            Log.d("myTEST", "kakaotalk message is received.")
        }
    }

}