package com.example.kakaotalktospeech

import android.app.Activity
import android.content.Context
import android.speech.tts.TextToSpeech
import android.util.Log
import android.widget.Toast
import java.util.*

class TTS_Module(val ctx: Context) : TextToSpeech.OnInitListener {
    private var TTS: TextToSpeech? = null
    private var text:String?=null
    private var locale: Locale = Locale.KOREAN
    private var pitch: Float =1.0.toFloat()
    private var speed: Float =1.0.toFloat()


    fun destroy(){
        if (TTS != null) {
            TTS!!.stop();
            TTS!!.shutdown();
        }
    }

    fun speech(text:String){
        TTS=TextToSpeech(ctx,this)
        TTS!!.setPitch(pitch)
        TTS!!.setSpeechRate(speed)
        TTS!!.setLanguage(locale)
        this.text=text
        Toast.makeText(ctx,text, Toast.LENGTH_SHORT).show()
    }

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            Log.d("myTEST", "onInit - Success")
            TTS?.speak(text, TextToSpeech.QUEUE_FLUSH, null)
        } else {
            Log.d("myTEST", "onInit - Failed")
        }
    }

}