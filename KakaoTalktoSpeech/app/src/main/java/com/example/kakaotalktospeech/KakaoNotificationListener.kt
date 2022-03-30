package com.example.kakaotalktospeech

import android.app.Notification
import android.os.Bundle
import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import android.speech.tts.TextToSpeech
import android.util.Log
import java.text.SimpleDateFormat
import java.util.*

class KakaoNotificationListener : NotificationListenerService() {
    private var mTTS: TextToSpeech? = null

    override fun onCreate() {
        super.onCreate()

        mTTS = TextToSpeech(this, TextToSpeech.OnInitListener { i ->
            if (i == TextToSpeech.SUCCESS) {
                val result = mTTS!!.setLanguage(Locale.KOREAN)
            }
            else{
                Log.e("myTest", "tts 연결 실패")
            }
        })
    }

    override fun onNotificationPosted(sbn: StatusBarNotification?) {
        super.onNotificationPosted(sbn)

        if(SettingManager.switchOn) {
            val packageName = sbn?.packageName
            if (packageName.equals("com.kakao.talk")) {
                val extras = sbn?.notification?.extras
                val sender = extras?.getString(Notification.EXTRA_TITLE)
                val message = extras?.getCharSequence(Notification.EXTRA_TEXT)
                val time = sbn?.notification?.`when`
                val subText = extras?.getCharSequence(Notification.EXTRA_SUB_TEXT)

                if (sender != null && message != null && time != null && subText == null) {
                    // option (subText==null) exclude message from group chat
                    Log.d("myTEST", "KakaoNotificationListener - message is received.")

                    var text = ""
                    text += if(SettingManager.readSender) "${sender}님으로부터 " else ""
                    text += if(SettingManager.readText) "$message" else ""
                    text += if(SettingManager.readTime) ""+Date(time) else ""
                    //text += if(SettingManager.readTime) ""+getTime() else ""

                    val ttsBundle = Bundle()
                    if(mTTS != null) {
                        ttsBundle.putFloat(
                            TextToSpeech.Engine.KEY_PARAM_VOLUME,
                            SettingManager.volume
                        )
                        mTTS!!.setSpeechRate(SettingManager.speed)
                        mTTS!!.speak(text, TextToSpeech.QUEUE_FLUSH, ttsBundle, null)
                    }
                }
            }
        }
    }

    private fun getTime() : String{
        val now=System.currentTimeMillis()
        val date= Date(now)
        val dateFormat= SimpleDateFormat("hh시 mm분")
        val time=dateFormat.format(date)
        return time
    }
}