package com.example.kakaotalktospeech

class SettingManager {

    companion object{
        var isRunning : Boolean = false

        /* TTS option */
        var ttsVolume : Int = 5 // max: 15 min: 0
        var ttsSpeed : Float = 1.0f // max: 2.0f, min = 0.6f
        var ttsEngine: Int = 0
        var ttsQueueDelete : Boolean = true

        /* TTS text option */
        var isReadingSender : Boolean = true
        var isReadingText : Boolean = true
        var isReadingTime : Boolean = false

        /*STT option*/
        var isSttActivate : Boolean = false
        var isSttWorking : Boolean = false
        var isReplying : Boolean = false
        var testSender : String = ""
        var testMessage : String = ""

        /* Useful option */
        var isNotificationServiceRunning:Boolean=false
        var whiteList : HashMap<String, ArrayList<Int>> = HashMap<String, ArrayList<Int>>()
        var usefulActivityInstance : UsefulActivity? = null
        var listActivityInstance: ListActivity? = null

    }
}