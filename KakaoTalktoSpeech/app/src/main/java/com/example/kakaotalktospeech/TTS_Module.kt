package com.example.kakaotalktospeech
/*
import android.content.Context
import android.media.AudioManager
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import android.util.Log
import android.widget.Toast
import java.util.*


class TTS_Module(val ctx: Context, private val am: AudioManager) : TextToSpeech.OnInitListener {
    private var TTS: TextToSpeech? = null
    private var locale: Locale = Locale.KOREAN
    private var text:String?=null

    fun destroy(){
        if (TTS != null) {
            TTS!!.stop();
            TTS!!.shutdown();
        }
    }

    fun speech(text:String){
        TTS=TextToSpeech(ctx,this)
        TTS!!.setLanguage(locale)
        TTS!!.setPitch(MainActivity.pitch)
        TTS!!.setSpeechRate(MainActivity.speed)
        this.text=text
        Toast.makeText(ctx,text, Toast.LENGTH_SHORT).show()
    }

    override fun onInit(status: Int) {

        if (status == TextToSpeech.SUCCESS) {
            val prevVolume= am.getStreamVolume(AudioManager.STREAM_MUSIC)
            TTS?.setOnUtteranceProgressListener(object : UtteranceProgressListener() {
                override fun onDone(utteranceId: String) {
                    am.setStreamVolume(AudioManager.STREAM_MUSIC,prevVolume,0)
                   // Log.d("myTEST", "TTS - after volume: "+am.getStreamVolume(AudioManager.STREAM_MUSIC))

                }
                override fun onError(utteranceId: String) {}
                override fun onStart(utteranceId: String) {
                    //Log.d("myTEST", "TTS - prev volume: $prevVolume")
                    am.setStreamVolume(AudioManager.STREAM_MUSIC,((am.getStreamMaxVolume(AudioManager.STREAM_MUSIC)) * MainActivity.volume).toInt(),0)
                    //Log.d("myTEST", "TTS - tts volume: "+am.getStreamVolume(AudioManager.STREAM_MUSIC))
                }
            })
            Log.d("myTEST", "onInit - Success")
            val map = HashMap<String, String>()
            map.put(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, "UniqueID");
            TTS?.speak(text, TextToSpeech.QUEUE_FLUSH, map)
        } else {
            Log.d("myTEST", "onInit - Failed")
        }
    }

} */