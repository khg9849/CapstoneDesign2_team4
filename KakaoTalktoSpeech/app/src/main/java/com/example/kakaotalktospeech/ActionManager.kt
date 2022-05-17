package com.example.kakaotalktospeech

import android.app.Activity
import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.util.Log
import android.widget.RemoteViews

class ActionManager {
    companion object{
        val SWITCH_UPDATE="com.example.action.SWITCH_UPDATE"

        val WIDGET_UPDATE="android.appwidget.action.APPWIDGET_UPDATE"
        var WIDGET_CLICKED = "com.example.pendingintent.Pending_Action"

        val NOTIFICATION_UPDATE_START="START"
        val NOTIFICATION_UPDATE_STOP="STOP"

        fun sendUpdateWidgetIntent(context: Context){
            val intent = Intent(context, NewAppWidget::class.java)
            intent.setAction(WIDGET_UPDATE)
            context.sendBroadcast(intent)
        }

        fun updateNotification(context: Context){
            // Update Notification Bar
            if (SettingManager.isNotificationServiceRunning) {
                val intent2 = Intent(context, NotificationService::class.java)
                intent2.setAction(if (SettingManager.isRunning) NOTIFICATION_UPDATE_START else NOTIFICATION_UPDATE_STOP)
                context.startService(intent2)
            }
        }

        fun updatePreferences(){
            // Update pref
            val pref =
                MainActivity.context().getSharedPreferences("pref", Activity.MODE_PRIVATE)
            val editor = pref.edit()
            editor.putBoolean("isRunning", SettingManager.isRunning);
            editor.commit()
        }
    }

}