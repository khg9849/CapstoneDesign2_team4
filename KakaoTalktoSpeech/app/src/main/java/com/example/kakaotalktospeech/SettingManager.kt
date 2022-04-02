package com.example.kakaotalktospeech

class SettingManager {
    companion object{
        var switchOn : Boolean = false
        /* TTS option */
        var volume : Float = 1.0f
        var speed : Float = 1.0f
        // engine
        var engineTitle: Int = 0

        /* TTS text option */
        var readSender : Boolean = true
        var readText : Boolean = true
        var readTime : Boolean = false
    }
}