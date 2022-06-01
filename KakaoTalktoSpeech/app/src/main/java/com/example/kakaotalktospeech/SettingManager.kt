package com.example.kakaotalktospeech

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

        /*STT option*/
        var isSttActivate : Boolean = false
        var isSttWorking : Boolean = false

        /* Useful option */
        var isNotibarRunning:Boolean=false
        var whiteList : HashMap<String, ArrayList<Int>> = HashMap<String, ArrayList<Int>>()
        var usefulActivityInstance : UsefulActivity? = null
        var listActivityInstance: ListActivity? = null

    }
}