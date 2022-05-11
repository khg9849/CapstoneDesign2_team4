package com.example.kakaotalktospeech

import android.speech.tts.TextToSpeech

class SettingManager {
    companion object{
        var isRunning : Boolean = false
        /* TTS option */
        var ttsVolume : Float = 1.0f
        var ttsSpeed : Float = 1.0f
        var ttsEngine: Int = 0
        var ttsQueueDelete : Boolean = true

        /* TTS text option */
        var isReadingSender : Boolean = true
        var isReadingText : Boolean = true
        var isReadingTime : Boolean = false
    }
}