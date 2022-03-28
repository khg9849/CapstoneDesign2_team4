package com.example.kakaotalktospeech

import android.speech.tts.TextToSpeech

class GlobalValueManagement {
    companion object{
        private var isApplicationUsing = false
        private var isTTSUsing = false
        var tts: TextToSpeech? = null

        fun setApplicationUsing(_check: Boolean){
            isApplicationUsing = _check
        }
        fun isApplicationUsing(): Boolean{
            return isApplicationUsing
        }

        fun setTTSUsing(_check: Boolean){
            isTTSUsing = _check
            if(_check == false) tts?.stop()
        }
        fun isTTSUsing(): Boolean{
            return isTTSUsing
        }
    }
}