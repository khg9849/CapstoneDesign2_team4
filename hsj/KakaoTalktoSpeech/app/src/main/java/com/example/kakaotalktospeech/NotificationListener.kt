package com.example.kakaotalktospeech

import android.app.Notification
import android.os.Bundle
import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import android.speech.tts.TextToSpeech
import android.util.Log
import java.util.*

class NotificationListener : NotificationListenerService(){
    private var mTTS:TextToSpeech? = null

    override fun onCreate() {
        super.onCreate()

        mTTS = TextToSpeech(this, TextToSpeech.OnInitListener { i ->
            if (i == TextToSpeech.SUCCESS) {
                val result = mTTS!!.setLanguage(Locale.KOREAN)
                mTTS!!.setSpeechRate(OptionActivity.ttsSpeed)
                // engine
            }
            else{
                Log.e("태그", "tts 연결 실패")
            }
        })
    }
    override fun onNotificationPosted(sbn: StatusBarNotification?) {
        super.onNotificationPosted(sbn)

        if(MainActivity.isUse) {
            val packageName: String = sbn?.packageName ?: ""
            var messageString: String = ""
            if (packageName == "com.kakao.talk") {
                val extras = sbn?.notification?.extras
                if (OptionActivity.isSender) { // 발신자 읽기
                    val extraTitle: String = extras?.get(Notification.EXTRA_TITLE).toString()
                    if (extraTitle != null) {
                        messageString += extraTitle
                    }
                }
                if (OptionActivity.isText) { // 내용 읽기
                    val extraText: String = extras?.get(Notification.EXTRA_TEXT).toString()
                    if (messageString != null) {
                        messageString += extraText
                    }
                }
                if (OptionActivity.isTime) { // 시간 읽기
                    messageString += Date(sbn?.notification?.`when`!!)
                }
            }
            if (messageString != "") {
                var tts_bundle = Bundle()
                tts_bundle.putFloat(TextToSpeech.Engine.KEY_PARAM_VOLUME, OptionActivity.ttsSound)
                mTTS!!.setSpeechRate(OptionActivity.ttsSpeed)
                mTTS!!.speak(messageString, TextToSpeech.QUEUE_FLUSH, tts_bundle, null)
            }
        }
    }
}