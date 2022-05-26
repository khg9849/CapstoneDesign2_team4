package com.example.kakaotalktospeech
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.SpeechRecognizer
import android.speech.tts.TextToSpeech
import android.util.Log
import android.widget.Toast
import com.example.kakaotalktospeech.ActionManager.Companion.sendUpdateWidgetIntent
import com.example.kakaotalktospeech.ActionManager.Companion.updateNotification
import com.example.kakaotalktospeech.ActionManager.Companion.updatePreferences
import java.util.*
import kotlin.collections.ArrayList
import android.content.Intent as Intent
import android.media.AudioManager as AudioManager

class SpeechToText {

    var check:Boolean = true
    private val activationKeyword : String = "테스트"
    private val messageKeyword : String = "답장"
    private val allonKeyword : String = "전부 켜 줘"
    private val alloffKeyword : String = "전부 꺼 줘"
    private val speedKeyword : String = "속도"
    private val volumeKeyword : String = "볼륨"
    private val optionKeyword : String = "옵션"
    private val stopKeyword : String = "멈춰"
    private val restartKeyword : String = "다시"

    private var doActivate : Int = 0;

    private val Sttintent: Intent
    private val Sttcontext: Context
    private val audioManager: AudioManager
    private lateinit var resultText: String


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

    private fun speakTTS(txt: String){
        myTTS?.speak("$txt", TextToSpeech.QUEUE_ADD, null, null)
    }
    fun CallStt(){
        Log.e("myTEST", "call stt 함수 호출")

        setCalllistener()
        setActivatelistener()

        CallspeechRecognizer = SpeechRecognizer.createSpeechRecognizer(Sttcontext)
        CallspeechRecognizer.setRecognitionListener(CallrecognitionListener)

        ActivatespeechRecognizer = SpeechRecognizer.createSpeechRecognizer(Sttcontext)
        ActivatespeechRecognizer.setRecognitionListener(ActivaterecognitionListener)

        CallspeechRecognizer.startListening(Sttintent)

    }

    private fun muteRecognition(mute:Boolean){
        Log.e("myTEST", "muteRecog")
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
                Log.e("myTEST", "onReady")
                muteRecognition(false)
            }
            override fun onBeginningOfSpeech() {
                Log.e("myTEST", "onBeginning")
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

                Log.e("myTEST", "$message onError")
            }

            override fun onResults(results: Bundle?) {
                Log.e("myTEST", "onResults")
                var txt : String = ""

                var matches: ArrayList<String> = results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION) as ArrayList<String>
                for (i in 0 until matches.size) {
                    txt = txt + matches[i]
                }


                if(txt.isNotEmpty()){
                    if(txt.contains(activationKeyword)){
                        sttCounter = 5
                        Log.e("myTEST", "응답 활성화")
                        speakTTS("네 말씀하세요")
                        muteRecognition(false)
                        ActivatespeechRecognizer.startListening(Sttintent)
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
                Log.e("myTEST", "onReady Activate")
                muteRecognition(false)
            }

            override fun onBeginningOfSpeech() {
                Log.e("myTEST", "onBeginning Activate")
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

                Log.e("myTEST", "$message onError")
            }

            override fun onResults(results: Bundle?) {
                Log.e("myTEST", "onResults Activate")
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
                                    ActivatespeechRecognizer.startListening(Sttintent)
                                    Log.e("myTEST", "메시지 답장 활성화")
                                } else if (txt.contains(allonKeyword) || txt.contains(alloffKeyword)) { // tts 전체 on/off
                                    if (txt.contains(allonKeyword)) { //전체 옵션 on/off기능
                                        speakTTS("노티를 다시 킬게요")
                                        //여기에 전체옵션 on/off추가
                                    } else {
                                        speakTTS("노티를 끌게요")
                                        //여기도
                                    }
                                    Log.e("myTEST", "옵션 조절 활성화")
                                } else if (txt.contains(speedKeyword)) { // tts 속도 조절 기능
                                    doActivate = 2
                                    sttCounter = 5
                                    speakTTS("속도를 얼마로 조절할까요?")
                                    ActivatespeechRecognizer.startListening(Sttintent)
                                    Log.e("myTEST", "속도 조절 활성화")
                                } else if (txt.contains(volumeKeyword)) { // tts 볼륨 조절 기능
                                    doActivate = 3
                                    sttCounter = 5
                                    speakTTS("볼륨을 얼마로 조정할까요??")
                                    ActivatespeechRecognizer.startListening(Sttintent)
                                    Log.e("myTEST", "볼륨 조절 활성화")
                                } else if (txt.contains(stopKeyword)) { // tts 정지 기능
                                } else if (txt.contains(restartKeyword)) { // 방금 온 메시지 다시 재생하는 기능
                                } else { // 인식이 안됐을 때
                                    if (sttCounter < 0) {
                                        sttCounter = 5
                                    } else {
                                        sttCounter--
                                    }
                                }
                                muteRecognition(false)
                            }
                        } else {
                            when (doActivate) {
                                1 -> {
                                    speakTTS(txt + "라고 보낼게요")
                                    doActivate = 0
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