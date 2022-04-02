package com.example.kakaotalktospeech

import android.app.Notification
import android.os.Bundle
import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import android.speech.tts.TextToSpeech
import android.util.Log
import android.widget.Toast
import java.text.SimpleDateFormat
import java.util.*

class KakaoNotificationListener : NotificationListenerService() {
    private var currentTTS: TextToSpeech? = null
    private var ttsList = arrayOf<TextToSpeech?>(null, null)
    override fun onCreate() {
        super.onCreate()
        initTTS()
    }
    private fun initTTS(){
        ttsList[0] = TextToSpeech(this, TextToSpeech.OnInitListener { i ->
            if (i == TextToSpeech.SUCCESS) {
                val result = currentTTS!!.setLanguage(Locale.KOREAN)
            }
            else{
                Log.e("myTest", "tts 연결 실패")
            }
        }, "com.google.android.tts")
        ttsList[1] = TextToSpeech(this, TextToSpeech.OnInitListener { i ->
            if (i == TextToSpeech.SUCCESS) {
                val result = currentTTS!!.setLanguage(Locale.KOREAN)
            }
            else{
                Log.e("myTest", "tts 연결 실패")
            }
        }, "com.samsung.android.tts")

        currentTTS = ttsList[0]
    }

    override fun onNotificationPosted(sbn: StatusBarNotification?) {
        super.onNotificationPosted(sbn)
        currentTTS = ttsList[SettingManager.ttsEngine]

        if(SettingManager.isRunning) {
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

                    var text = "메시지가 도착했습니다."
                    text = (if(SettingManager.isReadingSender) "${sender}님으로부터 " else "")+text
                    text += if(SettingManager.isReadingText) "$message" else ""
                    text = (if(SettingManager.isReadingTime) ""+formatTime(time) else "")+text

                    val ttsBundle = Bundle()
                    if(currentTTS != null) {
                        ttsBundle.putFloat(
                            TextToSpeech.Engine.KEY_PARAM_VOLUME,
                            SettingManager.ttsVolume
                        )
                        currentTTS!!.setSpeechRate(SettingManager.ttsSpeed)
                        currentTTS!!.speak(text, TextToSpeech.QUEUE_ADD, ttsBundle, null)
                        Toast.makeText(this,text, Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

    private fun formatTime(time:Long) : String{
        val dateFormat= SimpleDateFormat("hh시 mm분, ")
        val res=dateFormat.format(time)
        return res
    }
}