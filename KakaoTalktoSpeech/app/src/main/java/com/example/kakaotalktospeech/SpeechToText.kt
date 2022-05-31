package com.example.kakaotalktospeech
import android.content.Context
import android.content.Intent
import android.media.AudioManager
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.speech.RecognitionListener
import android.speech.SpeechRecognizer
import android.speech.tts.TextToSpeech
import android.util.Log
import androidx.appcompat.app.AppCompatActivity

class SpeechToText : AppCompatActivity {

    var check:Boolean = true
    private val activationKeyword : String = "테스트"
    private val messageKeyword : String = "답장"
    private val allonKeyword : String = "전부 켜 줘"
    private val alloffKeyword : String = "전부 꺼 줘"
    private val speedKeyword : String = "속도"
    private val volumeKeyword : String = "볼륨"
    private val stopKeyword : String = "멈춰"
    private val restartKeyword : String = "다시"

    private val useful = UsefulActivity()

    private var doActivate : Int = 0;

    private val Sttintent: Intent
    private val Sttcontext: Context
    private val audioManager: AudioManager

    private var sttCounter : Int = 3

    private var myTTS: TextToSpeech? = null

    constructor(intent: Intent, context: Context, audioManager:AudioManager, tts:TextToSpeech){
        this.Sttintent = intent
        this.Sttcontext = context
        this.audioManager = audioManager
        this.myTTS = tts;
    }

    lateinit var CallspeechRecognizer: SpeechRecognizer
    lateinit var CallrecognitionListener: RecognitionListener

    lateinit var ActivatespeechRecognizer: SpeechRecognizer
    lateinit var ActivaterecognitionListener: RecognitionListener

    private val mDelayHandler: Handler by lazy {
        Handler()
    }
    private fun callActivateSTT(){
        ActivatespeechRecognizer.startListening(Sttintent)
    }
    private fun reply(){
        SettingManager.usefulActivityInstance?.myTestFunc()
    }
    private fun speakTTS(txt: String){
        myTTS?.speak("$txt", TextToSpeech.QUEUE_ADD, null, null)
    }
    fun CallStt(){
        Log.d("myTEST", "call stt 함수 호출")

        setCalllistener()
        setActivatelistener()

        CallspeechRecognizer = SpeechRecognizer.createSpeechRecognizer(Sttcontext)
        CallspeechRecognizer.setRecognitionListener(CallrecognitionListener)

        ActivatespeechRecognizer = SpeechRecognizer.createSpeechRecognizer(Sttcontext)
        ActivatespeechRecognizer.setRecognitionListener(ActivaterecognitionListener)

        CallspeechRecognizer.startListening(Sttintent)

    }

    private fun muteRecognition(mute:Boolean){
        Log.d("myTEST", "muteRecog")
        if(audioManager != null){
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
                var flag : Int
                if(mute){
                    flag = AudioManager.ADJUST_MUTE
                }
                else{
                    flag = AudioManager.ADJUST_UNMUTE
                }

                audioManager.adjustStreamVolume(AudioManager.STREAM_NOTIFICATION, flag, 0)
                audioManager.adjustStreamVolume(AudioManager.STREAM_ALARM, flag, 0)

            }
            else{
                audioManager.setStreamMute(AudioManager.STREAM_NOTIFICATION, mute)
                audioManager.setStreamMute(AudioManager.STREAM_ALARM, mute)
            }
        }
    }

    private fun setCalllistener() {
        CallrecognitionListener = object : RecognitionListener {
            override fun onReadyForSpeech(params: Bundle?) {
                Log.d("myTEST", "onReady")
                muteRecognition(false)
            }
            override fun onBeginningOfSpeech() {
                Log.d("myTEST", "onBeginning")
            }
            override fun onRmsChanged(rmsdB: Float) {
            }
            override fun onBufferReceived(buffer: ByteArray?) {
            }
            override fun onEndOfSpeech() {
            }

            override fun onError(error: Int) {
                lateinit var message : String
                when (error) {
                    SpeechRecognizer.ERROR_AUDIO ->
                        message = "오디오 에러"
                    SpeechRecognizer.ERROR_CLIENT ->
                        message = "클라이언트 에러"
                    SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS ->
                        message = "퍼미션 없음"
                    SpeechRecognizer.ERROR_NETWORK ->
                        message = "네트워크 에러"
                    SpeechRecognizer.ERROR_NETWORK_TIMEOUT ->
                        message = "네트워크 타임아웃"
                    SpeechRecognizer.ERROR_NO_MATCH ->
                        message = "찾을 수 없음"
                    SpeechRecognizer.ERROR_RECOGNIZER_BUSY ->
                        message = "RECOGNIZER가 바쁨"
                    SpeechRecognizer.ERROR_SERVER ->
                        message = "서버가 이상함"
                    SpeechRecognizer.ERROR_SPEECH_TIMEOUT ->
                        message = "말하는 시간초과"
                    else ->
                        message = "알 수 없는 오류"
                }

                Log.d("myTEST", "$message onError")
            }

            override fun onResults(results: Bundle?) {
                Log.d("myTEST", "onResults")
                var txt : String = ""

                var matches: ArrayList<String> = results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION) as ArrayList<String>
                for (i in 0 until matches.size) {
                    txt = txt + matches[i]
                }


                if(txt.isNotEmpty()){
                    if(txt.contains(activationKeyword)){
                        sttCounter = 5
                        //useful.stopTTSforSTT()
                        Log.d("myTEST", "응답 활성화")
                        SettingManager.usefulActivityInstance?.stopTTSforSTT()
                        speakTTS("네 말씀하세요")
                        muteRecognition(false)
                        SettingManager.isSttWorking = true
                        callActivateSTT()
                        //mDelayHandler.postDelayed(::callActivateSTT, 500)

                    }
                }
            }

            override fun onPartialResults(partialResults: Bundle?) {}
            override fun onEvent(eventType: Int, params: Bundle?) {}
        }

    }
    private fun setActivatelistener() {
        ActivaterecognitionListener = object : RecognitionListener {
            override fun onReadyForSpeech(params: Bundle?) {
                Log.d("myTEST", "onReady Activate")
                muteRecognition(false)
            }

            override fun onBeginningOfSpeech() {
                Log.d("myTEST", "onBeginning Activate")
            }

            override fun onRmsChanged(rmsdB: Float) {
            }

            override fun onBufferReceived(buffer: ByteArray?) {
            }

            override fun onEndOfSpeech() {
            }

            override fun onError(error: Int) {
                lateinit var message: String
                when (error) {
                    SpeechRecognizer.ERROR_AUDIO ->
                        message = "오디오 에러"
                    SpeechRecognizer.ERROR_CLIENT ->
                        message = "클라이언트 에러"
                    SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS ->
                        message = "퍼미션 없음"
                    SpeechRecognizer.ERROR_NETWORK ->
                        message = "네트워크 에러"
                    SpeechRecognizer.ERROR_NETWORK_TIMEOUT ->
                        message = "네트워크 타임아웃"
                    SpeechRecognizer.ERROR_NO_MATCH ->
                        message = "찾을 수 없음"
                    SpeechRecognizer.ERROR_RECOGNIZER_BUSY ->
                        message = "RECOGNIZER가 바쁨"
                    SpeechRecognizer.ERROR_SERVER ->
                        message = "서버가 이상함"
                    SpeechRecognizer.ERROR_SPEECH_TIMEOUT ->
                        message = "말하는 시간초과"
                    else ->
                        message = "알 수 없는 오류"
                }

                Log.d("myTEST", "$message onError")
                SettingManager.isSttWorking = false
            }

            override fun onResults(results: Bundle?) {
                Log.d("myTEST", "onResults Activate")
                var txt: String = ""

                var matches: ArrayList<String> =
                    results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION) as ArrayList<String>
                for (i in 0 until matches.size) {
                    txt = txt + matches[i]

                    if (txt != null) {
                        muteRecognition(true)
                        if (doActivate == 0) {
                            if (txt.isNotEmpty()) {
                                if (txt.contains(messageKeyword)) { // 메시지 답장 기능
                                    doActivate = 1
                                    sttCounter = 5
                                    speakTTS("뭐라고 보낼까요?")
                                    mDelayHandler.postDelayed(::callActivateSTT, 500)
                                    //ActivatespeechRecognizer.startListening(Sttintent)
                                    Log.d("myReply", "메시지 답장 활성화")
                                } else if (txt.contains(allonKeyword) || txt.contains(alloffKeyword)) { // tts 전체 on/off
                                    if (txt.contains(allonKeyword)) { //전체 옵션 on/off기능
                                        speakTTS("노티를 다시 킬게요")
                                        SettingManager.isRunning = true
                                        //여기에 전체옵션 on/off추가
                                    } else {
                                        speakTTS("노티를 끌게요")
                                        SettingManager.isRunning = false
                                    //여기도
                                    }
                                    SettingManager.isSttWorking = false
                                    Log.d("myTEST", "노티 on/off 조절 활성화")
                                } else if (txt.contains(speedKeyword)) { // tts 속도 조절 기능
                                    doActivate = 2
                                    sttCounter = 5
                                    speakTTS("속도를 얼마로 조절할까요?")
                                    mDelayHandler.postDelayed(::callActivateSTT, 500)
                                    //ActivatespeechRecognizer.startListening(Sttintent)
                                    Log.d("myTEST", "속도 조절 활성화")
                                } else if (txt.contains(volumeKeyword)) { // tts 볼륨 조절 기능
                                    doActivate = 3
                                    sttCounter = 5
                                    speakTTS("볼륨을 얼마로 조정할까요??")
                                    ActivatespeechRecognizer.startListening(Sttintent)
                                    Log.d("myTEST", "볼륨 조절 활성화")
                                } else if (txt.contains(stopKeyword)) { // tts 정지 기능
                                    SettingManager.isSttWorking = false
                                } else if (txt.contains(restartKeyword)) { // 방금 온 메시지 다시 재생하는 기능
                                    SettingManager.usefulActivityInstance?.restartTTSforSTT()
                                    SettingManager.isSttWorking = false
                                } else { // 인식이 안됐을 때
                                    if (sttCounter < 0) {
                                        sttCounter = 5
                                    } else {
                                        sttCounter--
                                    }
                                    SettingManager.isSttWorking = false
                                }
                                muteRecognition(false)
                            }
                        } else {
                            when (doActivate) {
                                1 -> {
                                    Log.v("myReply", ""+SettingManager.testSender)
                                    speakTTS(txt + "이라고 보낼게요")
                                    SettingManager.testMessage = txt
                                    mDelayHandler.postDelayed(::reply, 500)
                                    SettingManager.isSttWorking=false
                                    doActivate = 4
                                    //메시지 답장기능
                                }
                                2 -> {
                                    //속도 조절
                                    if (txt.contains("최대")) {
                                        speakTTS("속도를 최대로 올릴게요")

                                    } else if (txt.contains("최소")) {

                                        speakTTS("속도를 최소로 낮출게요")

                                    } else {

                                    }
                                    doActivate = 0
                                    SettingManager.isSttWorking = false
                                }
                                3 -> {
                                    //볼륨 조절
                                    if (txt.contains("최대로")) {
                                        speakTTS("볼륨을 최대로 올릴게요")
                                    } else if (txt.contains("최소로")) {

                                        speakTTS("볼륨을 최소로 낮출게요")
                                    } else {

                                    }
                                    doActivate = 0
                                    SettingManager.isSttWorking=false
                                }
                                4->{
                                    if(txt.equals("그래")) {

                                        //useful.myTestFunc()
                                    }
                                    doActivate = 0
                                }
                            }
                        }
                    }
                }
            }
                override fun onPartialResults(partialResults: Bundle?) {}
                override fun onEvent(eventType: Int, params: Bundle?) {}
        }
    }

}