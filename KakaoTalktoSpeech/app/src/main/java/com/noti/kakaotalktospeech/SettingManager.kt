package com.noti.kakaotalktospeech

import android.speech.tts.TextToSpeech

class SettingManager {

    companion object{
        var isRunning : Boolean = false

        /* TTS option */
        var ttsVolume : Int = 5 // max: 15 min: 0
        var ttsSpeed : Float = 1.0f // max: 2.0f, min = 0.6f
        var ttsEngine: Int = 0
        var ttsEngineList : List<TextToSpeech.EngineInfo>? = null
        var ttsQueueDelete : Boolean = true

        /* TTS text option */
        var isReadingSender : Boolean = true
        var isReadingText : Boolean = true
        var isReadingTime : Boolean = false

        /*STT option*/
        var isSttActivate : Boolean = false
        var isSttWorking : Boolean = false
        var isReplying : Boolean = false


        /* Useful option */
        var isNotibarRunning:Boolean=false
        var whiteList : HashMap<String, ArrayList<Int>> = HashMap<String, ArrayList<Int>>()
        var usefulActivityInstance : UsefulActivity? = null
        var listActivityInstance: ListActivity? = null

    }
}