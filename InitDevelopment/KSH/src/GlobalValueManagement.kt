package com.example.kakaotalktospeech

import android.app.Application

class GlobalValueManagement {
    companion object{
        private var check = false

        fun setChecked(_check: Boolean){
            check = _check
        }
        fun getChecked(): Boolean{
            return check
        }
    }

}