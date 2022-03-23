package com.example.kakaotalktospeech

import android.app.Activity
import android.speech.tts.TextToSpeech
import android.util.Log
import java.util.*

class TTS_Module(val ctx: Activity) : TextToSpeech.OnInitListener {
    private var TTS: TextToSpeech? = null
    private var text: String = ""
    private var locale: Locale = Locale.KOREAN
    private var pitch: Float =1.0.toFloat()
    private var speed: Float =1.0.toFloat()

    fun toSpeech(text: String){
        this.text = text
        if(this.TTS==null){
            this.TTS=TextToSpeech(ctx,this)
        }else{
            speech()
        }
    }

    fun speech(){
        TTS!!.setPitch(pitch)
        TTS!!.setSpeechRate(speed)
        TTS!!.speak(text, TextToSpeech.QUEUE_FLUSH, null)
    }

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            val language = TTS!!.setLanguage(locale)
            if (language == TextToSpeech.LANG_MISSING_DATA
                || language == TextToSpeech.LANG_NOT_SUPPORTED
            ) {
                Log.d("myTEST", "Language is not supported")
            } else {
                Log.d("myTEST", "TTS module is ready")
                speech()
            }
        } else {
            Log.d("myTEST", "Failed to set TTS module")
        }
    }

}