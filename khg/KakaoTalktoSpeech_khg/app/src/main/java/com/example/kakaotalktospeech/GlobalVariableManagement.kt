package com.example.kakaotalktospeech

import android.speech.tts.TextToSpeech

class GlobalVariableManagement {
    companion object{
        private var tts: TextToSpeech? = null
        private var switchMainCheck : Boolean? = false
        private var switchSenderCheck : Boolean? = false
        private var switchSendedtextCheck : Boolean? = false
        private var switchSendedtimeCheck : Boolean? = false
        private var speed : Float = 1.0f
        private var volume : Float = 1.0f

        fun setTTS(_tts: TextToSpeech?){
            tts = _tts
        }
        fun getTTS(): TextToSpeech? {
            return tts
        }
        fun setSpeed(_speed: Float){
            speed = _speed
        }
        fun getSpeed():Float{
            return speed
        }
        fun setVolume(_volume: Float){
            volume = _volume
        }
        fun getVolume(): Float{
            return volume
        }


        fun setMainCheck(_switchMainCheck: Boolean?){
            switchMainCheck = _switchMainCheck
        }
        fun setSenderCheck(_switchSenderCheck: Boolean?){
            switchSenderCheck = _switchSenderCheck
        }
        fun setSendedtextCheck(_switchSendedtextCheck: Boolean?){
            switchSendedtextCheck = _switchSendedtextCheck
        }
        fun setSendedtimeCheck(_switchSendedtimeCheck: Boolean?){
            switchSendedtimeCheck = _switchSendedtimeCheck
        }

        fun getMainCheck(): Boolean?{
            return switchMainCheck
        }
        fun getSenderCheck(): Boolean?{
            return switchSenderCheck
        }
        fun getSendedtextCheck(): Boolean?{
            return switchSendedtextCheck
        }
        fun getSendedtimeCheck(): Boolean?{
            return switchSendedtimeCheck
        }

    }


}