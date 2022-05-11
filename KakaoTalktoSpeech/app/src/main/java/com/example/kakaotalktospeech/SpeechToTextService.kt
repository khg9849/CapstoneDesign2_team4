package com.example.kakaotalktospeech

import android.app.Service
import android.content.Intent
import android.os.Handler
import android.os.IBinder
import android.speech.RecognizerIntent
import android.widget.Toast

class SpeechToTextService : Service(){

    private var intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
    override fun onBind(p0: Intent?): IBinder {
        throw UnsupportedOperationException("Not yet")
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        callEvent()
        return Service.START_STICKY
    }

    private fun callEvent() {
        Toast.makeText(applicationContext, "서비스 시작", Toast.LENGTH_SHORT).show()
        intent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, packageName)
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "ko-KR")
        showGuest()

    }

    private val mDelayHandler: Handler by lazy {
        Handler()
    }

    private fun waitGuest(){
        mDelayHandler.postDelayed(::showGuest, 3000)
    }

    private fun showGuest(){
        val stt = SpeechToText(intent , applicationContext)
        if(SettingManager.isSTTRunning) {
            stt.CallStt()
            waitGuest() // 코드 실행뒤에 계속해서 반복하도록 작업한다.
        }
    }

}