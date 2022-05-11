package com.example.kakaotalktospeech

import android.app.Notification
import android.content.Intent
import android.os.Binder
import android.os.Bundle
import android.os.Handler
import android.os.IBinder
import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import android.util.Log
import android.widget.Toast
import java.text.SimpleDateFormat
import java.util.*
import kotlin.concurrent.schedule

class KakaoNotificationListener : NotificationListenerService() {
    private lateinit var tts: TextToSpeech
    lateinit var ttsList : List<TextToSpeech.EngineInfo>
    private var ttsQ: LinkedList<String> = LinkedList()
    private var isPause : Boolean = false

    override fun onCreate() {
        super.onCreate()
        Log.v("myTEST", "service oncreate")
        initTTS()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.d("myTEST", "service onstartcommand")

        return super.onStartCommand(intent, flags, startId)
    }

    val binder : IBinder = MyServiceBinder()

    override fun onBind(intent: Intent?): IBinder? {
        val action = intent!!.action
        if (action != "android.service.notification.NotificationListenerService"){
            Log.v("myTEST", "service binder")
            return binder
        }
        else{
            return super.onBind(intent)
        }
    }

    inner class MyServiceBinder : Binder() {
        fun getService(): KakaoNotificationListener = this@KakaoNotificationListener
    }

    private fun initTTS(){
        tts = TextToSpeech(this, TextToSpeech.OnInitListener { i ->
            if(i == TextToSpeech.SUCCESS){
                val result = tts!!.setLanguage(Locale.KOREAN)
            }
            else{
                Log.e("myTEST", "tts 생성실패")
            }
        })

        val speechListener = object : UtteranceProgressListener(){
            override fun onStart(p0: String?) {
                Toast.makeText(this@KakaoNotificationListener,p0, Toast.LENGTH_SHORT).show()
            }
            override fun onDone(p0: String?) {
                ttsQ.poll()
            }
            override fun onError(p0: String?) {

            }
        }
        tts.setOnUtteranceProgressListener(speechListener)
        ttsList = tts.engines
    }

    val ttsBundle = Bundle()
    fun changeTTS() {
        tts.stop()
        tts = TextToSpeech(this, TextToSpeech.OnInitListener { i ->
            if(i == TextToSpeech.SUCCESS){
                val result = tts!!.setLanguage(Locale.KOREAN)
            }
            else{
                Log.e("myTEST", "tts 생성실패")
            }
        }, ttsList[SettingManager.ttsEngine].name)

        val speechListener = object : UtteranceProgressListener(){
            override fun onStart(p0: String?) {
                Toast.makeText(this@KakaoNotificationListener,p0, Toast.LENGTH_SHORT).show()
            }
            override fun onDone(p0: String?) {
                ttsQ.poll()
            }
            override fun onError(p0: String?) {

            }
        }
        tts.setOnUtteranceProgressListener(speechListener)

        if(SettingManager.ttsQueueDelete){
            deleteQueue()
        }
        else {
            Timer().schedule(5000) {
                speakQueue()
            }
        }
    }

    private fun speakQueue(){
        for(i in ttsQ){
            ttsBundle.putFloat(
                TextToSpeech.Engine.KEY_PARAM_VOLUME,
                SettingManager.ttsVolume
            )
            tts!!.setSpeechRate(SettingManager.ttsSpeed)
            tts!!.speak(i, TextToSpeech.QUEUE_ADD, ttsBundle, TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID)
        }
    }

    private fun deleteQueue(){
        var strTemp : String? = ttsQ.poll()
        while(strTemp != null){
            strTemp = ttsQ.poll()
        }
    }

    fun stopTTS(){
        tts.stop()
        ttsQ.poll()
        speakQueue()
    }

    fun shutdownTTS(){
        tts.stop()
        deleteQueue()
    }

    fun pauseTTS(){
        tts.stop()
        isPause = true
    }

    fun restartTTS(){
        isPause = false
        speakQueue()
    }

    override fun onNotificationPosted(sbn: StatusBarNotification?) {
        super.onNotificationPosted(sbn)

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

                    if(tts != null) {
                        ttsBundle.putFloat(
                            TextToSpeech.Engine.KEY_PARAM_VOLUME,
                            SettingManager.ttsVolume
                        )
                        tts!!.setSpeechRate(SettingManager.ttsSpeed)
                        ttsQ.add(text)
                        if(!isPause) {
                            tts!!.speak(text, TextToSpeech.QUEUE_ADD, ttsBundle, TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID)
                        }
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