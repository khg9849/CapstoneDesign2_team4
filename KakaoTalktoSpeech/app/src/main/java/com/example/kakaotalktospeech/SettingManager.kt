package com.example.kakaotalktospeech

class SettingManager {
    companion object{
        var isRunning : Boolean = false
        /* TTS option */
        var ttsVolume : Float = 1.0f
        var ttsSpeed : Float = 1.0f
        var ttsEngine: Int = 0

        /* TTS text option */
        var isReadingSender : Boolean = true
        var isReadingText : Boolean = true
        var isReadingTime : Boolean = false

        /* Useful option */
        var isNotificationServiceRunning:Boolean=false
    }
}