package com.example.kakaotalktospeech
import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.SpeechRecognizer
import android.speech.tts.TextToSpeech
import android.util.Log
import android.widget.Toast
import java.util.*
import kotlin.collections.ArrayList
import android.content.Intent as Intent

class SpeechToText {

    var check:Boolean = true;
    private val Sttintent: Intent
    private val Sttcontext: Context

    private var currentTTS: TextToSpeech? = null
    private fun initTTS(){
        currentTTS = TextToSpeech(Sttcontext, TextToSpeech.OnInitListener { i ->
            if (i == TextToSpeech.SUCCESS) {
                val result = currentTTS!!.setLanguage(Locale.KOREAN)
            }
            else{
                Log.e("myTest", "tts 연결 실패")
            }
        }, "com.google.android.tts")

    }

    constructor(intent: Intent, context: Context){
        this.Sttintent = intent
        this.Sttcontext = context
    }

    lateinit var CallspeechRecognizer: SpeechRecognizer
    lateinit var CallrecognitionListener: RecognitionListener

    lateinit var speechRecognizer: SpeechRecognizer
    lateinit var recognitionListener: RecognitionListener

    fun CallStt(){

        setCalllistener()
        CallspeechRecognizer = SpeechRecognizer.createSpeechRecognizer(Sttcontext)
        CallspeechRecognizer.setRecognitionListener(CallrecognitionListener)
        CallspeechRecognizer.startListening(Sttintent)
    }

    private fun startStt(){
        initTTS()
        if(check) {
            setlistener()
            speechRecognizer = SpeechRecognizer.createSpeechRecognizer(Sttcontext)
            speechRecognizer.setRecognitionListener(recognitionListener)
            speechRecognizer.startListening(Sttintent)
        }
    }


    private fun setCalllistener() {
        CallrecognitionListener = object : RecognitionListener {
            override fun onReadyForSpeech(params: Bundle?) {
                //Toast.makeText(Sttcontext, "음성호출 인식 중", Toast.LENGTH_SHORT).show()
            }

            override fun onBeginningOfSpeech() {
            }
            override fun onRmsChanged(rmsdB: Float) {
            }
            override fun onBufferReceived(buffer: ByteArray?) {
            }
            override fun onEndOfSpeech() {
            }

            override fun onError(error: Int) {
                var message: String
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
                //Toast.makeText(Sttcontext, "에러 발생 $message", Toast.LENGTH_SHORT).show()
            }

            override fun onResults(results: Bundle?) {
                var txt : String = ""
                var matches: ArrayList<String> = results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION) as ArrayList<String>
                for (i in 0 until matches.size) {
                    txt = txt + matches[i]
                }
                if(txt.equals("대답")) {
                    val ttsBundle = Bundle()
                    if(currentTTS != null) {
                        ttsBundle.putFloat(
                            TextToSpeech.Engine.KEY_PARAM_VOLUME,
                            SettingManager.ttsVolume
                        )
                        currentTTS!!.setSpeechRate(SettingManager.ttsSpeed)
                        currentTTS!!.speak("부르셨나요", TextToSpeech.QUEUE_ADD, ttsBundle, null)
                    }
                    startStt()
                }
                else{
                    //Toast.makeText(Sttcontext, "$txt 를 호출함", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onPartialResults(partialResults: Bundle?) {
            }
            override fun onEvent(eventType: Int, params: Bundle?) {
            }
        }

    }
    private fun setlistener() {
        recognitionListener = object : RecognitionListener {
            override fun onReadyForSpeech(params: Bundle?) {
                Toast.makeText(Sttcontext, "원하는 기능을 말해주세요.", Toast.LENGTH_SHORT).show()
            }

            override fun onBeginningOfSpeech() {
                check = false
            }
            override fun onRmsChanged(rmsdB: Float) {
            }
            override fun onBufferReceived(buffer: ByteArray?) {
            }
            override fun onEndOfSpeech() {
                check = true
            }

            override fun onError(error: Int) {
                var message: String
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
                Toast.makeText(Sttcontext, "에러 발생 $message", Toast.LENGTH_SHORT).show()
            }

            override fun onResults(results: Bundle?) {
                //Toast.makeText(Sttcontext, "결과나옴", Toast.LENGTH_SHORT).show()
                var txt : String = ""
                var matches: ArrayList<String> = results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION) as ArrayList<String>
                for (i in 0 until matches.size) {
                    txt = txt + matches[i]
                }
                Toast.makeText(Sttcontext, "$txt", Toast.LENGTH_SHORT).show()
                if(txt.equals("시간 꺼 줘")){
                    Toast.makeText(Sttcontext, "시간 읽어주기를 끕니다.", Toast.LENGTH_SHORT).show()
                    SettingManager.isReadingTime = false
                }
            }

            override fun onPartialResults(partialResults: Bundle?) {
            }
            override fun onEvent(eventType: Int, params: Bundle?) {
            }
        }

    }

}