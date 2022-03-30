package com.example.kakaotalktospeech

import android.app.Notification
import android.content.ContentValues.TAG
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import android.speech.tts.TextToSpeech
import android.speech.tts.TextToSpeech.Engine.KEY_PARAM_VOLUME
import android.util.Log
import android.widget.Toast
import java.text.SimpleDateFormat

class KakaoNotificationListenerService: NotificationListenerService() {
    override fun onListenerConnected(){
        super.onListenerConnected()
    }

    override fun onListenerDisconnected(){
        super.onListenerDisconnected()
    }

    override fun onNotificationPosted(sbn: StatusBarNotification){
        super.onNotificationPosted(sbn)

        var extras = sbn?.notification?.extras
        val sender: String = extras?.get(Notification.EXTRA_TITLE).toString()
        //EXTRA_SUB_TEXT <- 방제목
        val sendedText: String = extras?.get(Notification.EXTRA_TEXT).toString()
        var dateformat: SimpleDateFormat = SimpleDateFormat("yyyy년MM월dd일 hh시mm분")
        val sendedTime: String = dateformat.format(sbn.postTime)

        val mainCheck: Boolean? = GlobalVariableManagement.getMainCheck()
        val senderCheck: Boolean? = GlobalVariableManagement.getSenderCheck()
        val timeCheck: Boolean? = GlobalVariableManagement.getSendedtimeCheck()
        val textCheck: Boolean? = GlobalVariableManagement.getSendedtextCheck()

        //val extraApllicationTitle: String = extras?.get(Notification.EXTRA_).toString()
        Log.d(
            TAG, "onNotificationPosted:\n" +
                    "Title: $sender\n" +
                    "Text: $sendedText\n" +
                    "Time: $sendedTime\n" +
                    "\nmain check "+ mainCheck+
                    "\nsender check "+ senderCheck+
                    "\ntime check "+ timeCheck +
                    "\ntext check " + textCheck

        )
        GlobalVariableManagement.getTTS()
            ?.playSilentUtterance(100, TextToSpeech.QUEUE_ADD, null)

        if(sbn!=null && sbn.getPackageName().equals(("com.kakao.talk")) && mainCheck == true && sender != "null") {
            Log.d(
                TAG, "\nlet tts\n"
            )
            if(timeCheck == true) {
                GlobalVariableManagement.getTTS()
                    ?.speak("$sendedTime", TextToSpeech.QUEUE_ADD, null, null)
                GlobalVariableManagement.getTTS()
                    ?.playSilentUtterance(500, TextToSpeech.QUEUE_ADD, null)
            }
            if(senderCheck == true) {
                GlobalVariableManagement.getTTS()
                    ?.speak("$sender", TextToSpeech.QUEUE_ADD, null, null)
                GlobalVariableManagement.getTTS()
                    ?.playSilentUtterance(500, TextToSpeech.QUEUE_ADD, null)
            }

            if(textCheck == true) {
                GlobalVariableManagement.getTTS()
                    ?.speak("$sendedText", TextToSpeech.QUEUE_ADD, null, null)
                GlobalVariableManagement.getTTS()
                    ?.playSilentUtterance(500, TextToSpeech.QUEUE_ADD, null)
            }

        }
        /*
        val mNotification : Notification = sbn . getNotification ()
        var extras :Bundle = mNotification.extras;
        var from: String
        var text: String
        var time: String
        if(sbn!=null && sbn.getPackageName().equals(("com.kakao.talk"))) {
            from= extras.getString(Notification.EXTRA_TITLE).toString()
            text= extras.getString(Notification.EXTRA_TEXT).toString()
            time= extras.getString(Notification.EXTRA_SHOW_WHEN).toString()
            Log.d(TAG, "kakao>> " + "Title: " + from + ", Text: " + text)
        }*/
    }



}