package com.example.kakaotalktospeech

import android.content.ContentValues
import android.net.Uri
import android.telephony.SmsManager
import android.util.Log

class SendMessageActivity {
    fun sendMessage(txt:String){
        val smsManager = SmsManager.getDefault()
        val inputPhoneNum = "01091382824"
        val myUri = Uri.parse("smsto:${inputPhoneNum}")
        //val myIntent = Intent(Intent.ACTION_SENDTO, myUri)

        val size: Int = txt.length
        Log.d(
            ContentValues.TAG,"$size\n"
        )
        if(size < 70) {
            smsManager.sendTextMessage(inputPhoneNum, null, txt, null, null)
        }
        else {
            var i : Int = 0
            var j : Int = 64
            while(j < size){

                smsManager.sendTextMessage(inputPhoneNum, null, txt.slice(i..j), null, null)
                i+=65
                j+=65
                Log.d(
                    ContentValues.TAG,"$i $j\n"
                )
            }
            smsManager.sendTextMessage(inputPhoneNum, null, txt.slice(i..size-1), null, null)

        }
    }
}