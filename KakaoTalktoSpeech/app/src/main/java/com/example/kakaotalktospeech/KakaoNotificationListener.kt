package com.example.kakaotalktospeech

import android.app.Notification
import android.app.RemoteInput
import android.content.Intent
import android.media.AudioAttributes
import android.media.AudioFocusRequest
import android.media.AudioManager
import android.os.*
import android.provider.MediaStore
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
    private lateinit var audioManager : AudioManager
    private var preAudio : Int = -1
    private lateinit var audioListener : AudioManager.OnAudioFocusChangeListener
    private lateinit var audioAttributes: AudioAttributes
    private lateinit var audioFocusRequest: AudioFocusRequest
    private lateinit var recentAct : Notification.Action
    private lateinit var recentSender : String

    override fun onCreate() {
        super.onCreate()
        Log.v("myTEST", "service oncreate")
        initTTS()
        initAudioFocus()
        audioManager = getSystemService(AUDIO_SERVICE) as AudioManager
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

    private fun initAudioFocus(){
        audioListener = object : AudioManager.OnAudioFocusChangeListener{
            override fun onAudioFocusChange(p0: Int) {
                when(p0){
                    AudioManager.AUDIOFOCUS_LOSS -> {
                        shutdownTTS()
                        Log.d("myTEST", "audio focus 소실, tts shutdown")
                    }
                }
            }
        }

        audioAttributes = AudioAttributes.Builder().setUsage(AudioAttributes.USAGE_MEDIA).setContentType(AudioAttributes.CONTENT_TYPE_SPEECH).build()
        audioFocusRequest = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            AudioFocusRequest.Builder(AudioManager.AUDIOFOCUS_GAIN).setAudioAttributes(audioAttributes).setAcceptsDelayedFocusGain(false).setOnAudioFocusChangeListener(audioListener).setWillPauseWhenDucked(true).build()
        } else {
            TODO("VERSION.SDK_INT < O")
        }
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
                abandonFocus()
            }
            override fun onError(p0: String?) {

            }
        }
        tts.setOnUtteranceProgressListener(speechListener)
        ttsList = tts.engines
    }

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
                abandonFocus()
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
            getFocusAndSpeak(i)
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
        abandonFocus()
        ttsQ.poll()
        speakQueue()
    }

    fun shutdownTTS(){
        tts.stop()
        abandonFocus()
        deleteQueue()
    }

    fun pauseTTS(){
        tts.stop()
        abandonFocus()
        isPause = true
    }

    fun restartTTS(){
        isPause = false
        speakQueue()
    }

    fun recentsender() : String {
        if(recentSender == null){
            return "test";
        }
        else {
            return recentSender
        }
    }

    fun reply(message : String){
        val sendIntent = Intent()
        var msg = Bundle()
        for(inputable in recentAct.remoteInputs)
            msg.putCharSequence(inputable.resultKey, message)
        RemoteInput.addResultsToIntent(recentAct.remoteInputs, sendIntent, msg)
        recentAct.actionIntent.send(this, 0, sendIntent)
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

                val wExt : Notification.WearableExtender = Notification.WearableExtender(sbn?.notification)
                for(act in wExt.actions){
                    if(act.remoteInputs != null && act.remoteInputs.size > 0){
                        if(act.title.toString().toLowerCase().contains("reply") || act.title.toString().toLowerCase().contains("답장")){
                            recentAct = act
                            recentSender = sender!!
                            SettingManager.testSender = recentSender
                            Log.e("myTEST", ""+recentSender)
                        }
                    }
                }

                if (sender != null && message != null && time != null && subText == null) {
                    // option (subText==null) exclude message from group chat
                    Log.d("myTEST", "KakaoNotificationListener - message is received.")
                    ContactsManager.putWhiteList(sender)

                    var text = "메시지가 도착했습니다."
                    text = (if(SettingManager.isReadingSender) "${sender}님으로부터 " else "")+text
                    text += if(SettingManager.isReadingText) "$message" else ""
                    text = (if(SettingManager.isReadingTime) ""+formatTime(time) else "")+text

                    if(tts != null) {
                        tts!!.setSpeechRate(SettingManager.ttsSpeed)
                        ttsQ.add(text)
                        if(!isPause) {
                            getFocusAndSpeak(text)
                        }
                    }
                }
            }
        }
    }

    private fun getFocusAndSpeak(text : String){
        if(preAudio == -1) {
            preAudio = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC)
        }
        audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, SettingManager.ttsVolume, 0)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val requestResult = audioManager.requestAudioFocus(audioFocusRequest)
            if(requestResult == AudioManager.AUDIOFOCUS_REQUEST_GRANTED){
                tts!!.speak(text, TextToSpeech.QUEUE_ADD, null, TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID)
            }
        }
    }

    private fun abandonFocus(){
        if(preAudio != -1) {
            audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, preAudio, 0)
            preAudio = -1
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            audioManager.abandonAudioFocusRequest(audioFocusRequest)
        }
    }

    private fun formatTime(time:Long) : String{
        val dateFormat= SimpleDateFormat("hh시 mm분, ")
        val res=dateFormat.format(time)
        return res
    }
}