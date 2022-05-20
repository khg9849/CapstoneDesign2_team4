package com.example.kakaotalktospeech
import android.content.Context
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

class SpeechToText {

    var check:Boolean = true
    private val Sttintent: Intent
    private val Sttcontext: Context
    private lateinit var resultText: String

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

    lateinit var SendspeechRecognizer: SpeechRecognizer
    lateinit var SendrecognitionListener: RecognitionListener

    lateinit var OptionspeechRecognizer: SpeechRecognizer
    lateinit var OptionrecognitionListener: RecognitionListener

    fun CallStt(){
        setCalllistener()
        CallspeechRecognizer = SpeechRecognizer.createSpeechRecognizer(Sttcontext)
        CallspeechRecognizer.setRecognitionListener(CallrecognitionListener)
        CallspeechRecognizer.startListening(Sttintent)
    }

    fun startSttToControlOption(){
        initTTS()
        if(check) {
            setOptionlistener()
            OptionspeechRecognizer = SpeechRecognizer.createSpeechRecognizer(Sttcontext)
            OptionspeechRecognizer.setRecognitionListener(OptionrecognitionListener)
            OptionspeechRecognizer.startListening(Sttintent)
        }
    }
    fun startSttToSend(){
        sendMessagefromSTT()
        SendspeechRecognizer = SpeechRecognizer.createSpeechRecognizer(Sttcontext)
        SendspeechRecognizer.setRecognitionListener(SendrecognitionListener)
        SendspeechRecognizer.startListening(Sttintent)
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
                if(txt.equals("옵션")) {
                    /*val ttsBundle = Bundle()
                    if(currentTTS != null) {
                        ttsBundle.putFloat(
                            TextToSpeech.Engine.KEY_PARAM_VOLUME,
                            1.0F
                        )
                        currentTTS!!.setSpeechRate(SettingManager.ttsSpeed)
                        currentTTS!!.speak("옵션을 선택해주세요", TextToSpeech.QUEUE_ADD, ttsBundle, null)
                    }*/
                    startSttToControlOption()
                }
                else if(txt.equals("메시지 전송")){
                    startSttToSend()
                    //Toast.makeText(Sttcontext, "$txt 를 호출함", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onPartialResults(partialResults: Bundle?) {
            }
            override fun onEvent(eventType: Int, params: Bundle?) {
            }
        }

    }
    private fun setOptionlistener() {
        OptionrecognitionListener = object : RecognitionListener {
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
                }//말한 내용이 txt에 담김
                /*
                여기에 옵션 조절 코드 작성
                 */
                val varName="isRunning"
                val tempValue1=false
                val tempValue2=10
                val tempValue3=0.5f

                when(varName){
                    "IsRunning"->{
                        SettingManager.isRunning = tempValue1
                        sendUpdateWidgetIntent(MainActivity.context())
                        updateNotification(MainActivity.context())
                        updatePreferences()
                    }
                    /* TTS option */
                    "ttsVolume"->{
                        SettingManager.ttsVolume=tempValue2
                    }
                    "ttsSpeed"->{
                        SettingManager.ttsSpeed=tempValue3
                    }
                    "ttsEngine"->{
                        SettingManager.ttsEngine=0
                    }
                    /* TTS text option */
                    "isReadingSender"->{
                        SettingManager.isReadingSender=tempValue1
                    }
                    "isReadingText"->{
                        SettingManager.isReadingText=tempValue1
                    }
                    "isReadingTime"->{
                        SettingManager.isReadingTime=tempValue1
                    }
                }

            }

            override fun onPartialResults(partialResults: Bundle?) {
            }
            override fun onEvent(eventType: Int, params: Bundle?) {
            }
        }

    }
    private fun sendMessagefromSTT() {
        SendrecognitionListener = object : RecognitionListener {
            override fun onReadyForSpeech(params: Bundle?) {
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
            }

            override fun onResults(results: Bundle?) {
                //Toast.makeText(Sttcontext, "결과나옴", Toast.LENGTH_SHORT).show()
                var txt : String = ""
                var matches: ArrayList<String> = results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION) as ArrayList<String>
                val sendMSG = SendMessageActivity()
                for (i in 0 until matches.size) {
                    txt = txt + matches[i]
                }

                resultText = txt
                sendMSG.sendMessage(resultText)
                Toast.makeText(Sttcontext, "$resultText", Toast.LENGTH_SHORT).show()

            }

            override fun onPartialResults(partialResults: Bundle?) {
            }
            override fun onEvent(eventType: Int, params: Bundle?) {
            }
        }

    }

}