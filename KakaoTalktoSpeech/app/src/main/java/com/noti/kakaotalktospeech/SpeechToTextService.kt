package com.noti.kakaotalktospeech

import android.app.Service
import android.content.Intent
import android.media.AudioManager
import android.os.Handler
import android.os.IBinder
import android.speech.RecognizerIntent
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import android.util.Log
import android.widget.Toast
import java.util.*

class SpeechToTextService : Service(){
    private var intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
    lateinit var tts: TextToSpeech
    lateinit var audioManager: AudioManager
    var stt: SpeechToText? = null

    override fun onBind(p0: Intent?): IBinder {
        throw UnsupportedOperationException("Not yet")
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {

        audioManager = getSystemService(AUDIO_SERVICE) as AudioManager
        initTTS()
        callEvent()
        return Service.START_STICKY
    }
    private fun initTTS(){
        tts = TextToSpeech(applicationContext){ status ->
            if(status != TextToSpeech.ERROR){
                tts?.setLanguage(Locale.KOREAN)
                tts?.setPitch(1.0F)
                tts?.setSpeechRate(1.0F)
            }
        }

    }
    private fun callEvent() {
        //Toast.makeText(applicationContext, "서비스 시작", Toast.LENGTH_SHORT).show()
        intent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, packageName)
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "ko-KR")
        showGuest()

    }

    private val mDelayHandler: Handler by lazy {
        Handler()
    }

    private fun waitGuest(){
        Log.d("myService", "WaitGuest "+ SettingManager.isSttWorking)
        if(SettingManager.isSttWorking)
            mDelayHandler.postDelayed(::runningGuest, 1000)
        else
            mDelayHandler.postDelayed(::showGuest, 1000)
    }
    private fun runningGuest(){
        Log.d("myService", "runningGuest")
        mDelayHandler.postDelayed(::waitGuest, 1000)
    }

    private fun showGuest(){
        Log.d("myService", "ShowGuest")
        stt = SpeechToText(intent , applicationContext, audioManager, tts)
        if(!SettingManager.isSttWorking)
            stt?.CallStt()
        if(SettingManager.isSttActivate)
            waitGuest() // 코드 실행뒤에 계속해서 반복하도록 작업한다.
    }
}