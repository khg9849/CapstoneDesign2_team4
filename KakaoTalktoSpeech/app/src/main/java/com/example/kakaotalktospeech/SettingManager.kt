package com.example.kakaotalktospeech

import android.content.Intent
import kotlin.properties.Delegates
import android.speech.tts.TextToSpeech

class SettingManager {

    companion object{
        var isRunning : Boolean = false

        /* TTS option */
        var ttsVolume : Int = 5
        var ttsSpeed : Float = 1.0f
        var ttsEngine: Int = 0
        var ttsQueueDelete : Boolean = true

        /* TTS text option */
        var isReadingSender : Boolean = true
        var isReadingText : Boolean = true
        var isReadingTime : Boolean = false

        /* Useful option */
        var isNotificationServiceRunning:Boolean=false
        var whiteList : HashMap<String, Int> = HashMap<String, Int>()
    }
}