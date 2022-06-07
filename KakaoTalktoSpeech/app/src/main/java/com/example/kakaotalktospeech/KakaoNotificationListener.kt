package com.example.kakaotalktospeech

import android.app.Notification
import android.app.RemoteInput
import android.content.*
import android.media.AudioAttributes
import android.media.AudioFocusRequest
import android.media.AudioManager
import android.media.session.MediaSession
import android.media.session.PlaybackState
import android.os.*
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
    private lateinit var ttsList : List<TextToSpeech.EngineInfo>
    private var ttsQ: LinkedList<String> = LinkedList()
    private var isPause : Boolean = false
    private lateinit var audioManager : AudioManager
    private var preAudio : Int = -1
    private lateinit var audioListener : AudioManager.OnAudioFocusChangeListener
    private lateinit var audioAttributes: AudioAttributes
    private lateinit var audioFocusRequest: AudioFocusRequest
    private lateinit var recentAct : Notification.Action
    private var recentSender : String? = null
    private lateinit var curAct : Notification.Action
    private var curSender : String? = null
    private val binder : IBinder = MyServiceBinder()

    /*
    class mediaBroadcastReceiver : BroadcastReceiver(){
        override fun onReceive(p0: Context?, p1: Intent?) {
            Log.e("myTEST", ""+p1?.action)
        }
    }

    private lateinit var myReceiver : mediaBroadcastReceiver
    private lateinit var mySession : MediaSession*/
    override fun onCreate() {
        super.onCreate()
        initTTS()
        initAudioFocus()
        audioManager = getSystemService(AUDIO_SERVICE) as AudioManager
/*
        mySession = MediaSession(applicationContext, "test")

        var token = mySession.sessionToken
        mySession.setCallback(object : MediaSession.Callback(){
            override fun onMediaButtonEvent(mediaButtonIntent: Intent): Boolean {
                Log.e("myTEST", "test")
                return super.onMediaButtonEvent(mediaButtonIntent)
            }

            override fun onPlay() {
                Log.e("myTEST", "test")
                super.onPlay()
            }

            override fun onPause() {
                Log.e("myTEST", "test")
                super.onPause()
            }
        })
        mySession.setFlags(MediaSession.FLAG_HANDLES_MEDIA_BUTTONS)

        val state = PlaybackState.Builder()
            .setActions(PlaybackState.ACTION_PLAY)
            .setState(PlaybackState.STATE_STOPPED, PlaybackState.PLAYBACK_POSITION_UNKNOWN, 0f)
            .build()
        mySession.setPlaybackState(state)
        mySession.setActive(true)*/
    }
    /*
    var cn = ComponentName("com.example.kakaotalktospeech", "com.example.kakaotoaltospeech.KakaoNotificationListener")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            mySession.setMediaButtonBroadcastReceiver(cn)
        }
     */

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.d("myTEST", "service onstartcommand")

        return super.onStartCommand(intent, flags, startId)
    }

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
            AudioFocusRequest.Builder(AudioManager.AUDIOFOCUS_GAIN_TRANSIENT).setAudioAttributes(audioAttributes).setAcceptsDelayedFocusGain(false).setOnAudioFocusChangeListener(audioListener).setWillPauseWhenDucked(true).build()
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
                if(ttsQ.isEmpty()) {
                    abandonFocus()
                }
            }
            override fun onError(p0: String?) {

            }
        }
        tts.setOnUtteranceProgressListener(speechListener)
        ttsList = tts.engines
        SettingManager.ttsEngineList = ttsList
        for(ttsCount in 0 until ttsList.size){
            if(tts.defaultEngine == ttsList[ttsCount].name){
                SettingManager.ttsEngine = ttsCount
                break
            }
        }
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
                if(ttsQ.isEmpty()) {
                    abandonFocus()
                }
            }
            override fun onError(p0: String?) {

            }
        }
        tts.setOnUtteranceProgressListener(speechListener)


        Timer().schedule(2000){
            getFocusAndSpeak("테스트 음성입니다")
        }
        if(SettingManager.ttsQueueDelete){
            deleteQueue()
        }
        else {
            speakQueue()
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

    fun recentsender() : String? {
        curSender = recentSender
        curAct = recentAct
        return curSender
    }

    fun reply(message : String){
        val sendIntent = Intent()
        var msg = Bundle()
        for(inputable in curAct.remoteInputs)
            msg.putCharSequence(inputable.resultKey, message)
        RemoteInput.addResultsToIntent(curAct.remoteInputs, sendIntent, msg)
        curAct.actionIntent.send(this, 0, sendIntent)
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

                //갠톡만 받아지게 만드는 조건
                if (sender != null && message != null && time != null && subText == null) {
                    // option (subText==null) exclude message from group chat
                    Log.d("myTEST", "KakaoNotificationListener - message is received.")

                    //sender가 처음이라면 친구목록에 새로 넣고, 이미있다면 횟수를 1 증가
                    ContactsManager.putWhiteList(sender)
                    if(SettingManager.listActivityInstance != null){
                        SettingManager.listActivityInstance!!.recreate()
                    }

                    //sender의 연락 수신이 허용됬을때만 수신처리
                    if(ContactsManager.checkWhiteList(sender)){

                        // stt용으로 notification의 action을 받아오는 과정
                        var actions = sbn?.notification?.actions
                        if( actions != null) {
                            for (act in actions) {
                                if (act.remoteInputs != null && act.remoteInputs.size > 0) {
                                    if (act.title.toString().toLowerCase().contains("reply") ||
                                        act.title.toString().toLowerCase().contains("답장")) {
                                        recentAct = act
                                        recentSender = sender!!
                                    }
                                }
                            }
                        }

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
                    else{
                        Log.d("myTest", "don't receive")
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