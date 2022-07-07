package com.noti.kakaotalktospeech

import android.app.Activity
import android.content.Context
import android.content.Intent

class ActionManager {
    companion object{
        val WIDGET_CREATE="android.appwidget.action.APPWIDGET_UPDATE"
        var WIDGET_CLICKED = "com.example.pendingintent.Pending_Action"

        val NOTIBAR_CREATE="CREATE"
        val NOTIBAR_UPDATE_START="UPDATE_START"
        val NOTIBAR_UPDATE_STOP="UPDATE_STOP"

        fun updateIsRunning(isRunning:Boolean){
            SettingManager.isRunning =isRunning

            // Update pref
            val pref =
                MainActivity.context().getSharedPreferences("pref", Activity.MODE_PRIVATE)
            val editor = pref.edit()
            editor.putBoolean("isRunning", SettingManager.isRunning);
            editor.commit()
        }


        fun sendUpdateWidgetIntent(context: Context){
            val intent = Intent(context, AppWidget::class.java)
            intent.setAction(WIDGET_CREATE)
            intent.putExtra("isRunning", SettingManager.isRunning)
            context.sendBroadcast(intent)
        }

        fun updateNotibar(context: Context){
            // Update Notification Bar
            if (SettingManager.isNotibarRunning) {
                val intent2 = Intent(context, NotibarService::class.java)
                intent2.setAction(if (SettingManager.isRunning) NOTIBAR_UPDATE_START else NOTIBAR_UPDATE_STOP)
                context.startService(intent2)
            }
        }

        fun updatePreferences(){
            // Update pref
            val pref =
                MainActivity.context().getSharedPreferences("pref", Activity.MODE_PRIVATE)
            val editor = pref.edit()
            editor.putBoolean("isRunning", SettingManager.isRunning)
            editor.putInt("ttsVolume", SettingManager.ttsVolume)
            editor.putFloat("ttsSpeed", SettingManager.ttsSpeed)
            editor.commit()
        }
    }

}