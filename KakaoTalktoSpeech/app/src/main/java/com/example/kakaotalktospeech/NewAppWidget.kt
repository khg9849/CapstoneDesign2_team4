package com.example.kakaotalktospeech

import android.app.Activity
import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.widget.RemoteViews
import android.widget.Toast
import kotlin.Int


/**
 * Implementation of App Widget functionality.
 */
class NewAppWidget : AppWidgetProvider() {

    val APPWIDGET_UPDATE="android.appwidget.action.APPWIDGET_UPDATE"
    var PENDING_ACTION = "com.example.testsetonclickpendingintent.Pending_Action"

    val NOTIFICATION_UPDATE_START="NOTIFICATION_UPDATE_START"
    val NOTIFICATION_UPDATE_STOP="NOTIFICATION_UPDATE_STOP"

    fun setON(views:RemoteViews){
        views.setInt(R.id.bttn_on, "setTextColor", Color.BLACK);
        views.setInt(R.id.bttn_on, "setBackgroundColor", Color.WHITE);
        views.setInt(R.id.bttn_off, "setTextColor", Color.WHITE);
        views.setInt(R.id.bttn_off, "setBackgroundColor", Color.BLACK);
    }
    fun setOFF(views:RemoteViews){
        views.setInt(R.id.bttn_on, "setTextColor", Color.WHITE);
        views.setInt(R.id.bttn_on, "setBackgroundColor", Color.BLACK);
        views.setInt(R.id.bttn_off, "setTextColor", Color.BLACK);
        views.setInt(R.id.bttn_off, "setBackgroundColor", Color.WHITE);
    }

    override fun onReceive(context: Context, intent: Intent) {
        super.onReceive(context, intent)

        // RemoteViews 인스턴트 생성
        val views = RemoteViews(context.packageName, R.layout.new_app_widget)

        // 수신한 인텐트로부터 액션값을 읽음
        val action = intent.action
        Toast.makeText(context, "action: " + action, Toast.LENGTH_SHORT).show();

        if(action==APPWIDGET_UPDATE){
            if(SettingManager.isRunning===true){
                setON(views)
            }
            else{
                setOFF(views)
            }
        }
        else if (action == PENDING_ACTION) {
                val viewId=intent.getIntExtra("viewId",0)
                if(viewId==R.id.bttn_on){
                    setON(views)
                    SettingManager.isRunning=true
                    }
                else if(viewId==R.id.bttn_off){
                   setOFF(views)
                    SettingManager.isRunning=false
                    Toast.makeText(context, "setting mng: " + SettingManager.isRunning, Toast.LENGTH_SHORT).show();
                }
            // Update Notification Bar
            if(SettingManager.isNotificationServiceRunning){
                val intent2 = Intent(context, NotificationService::class.java)
                intent2.setAction(if(SettingManager.isRunning) NOTIFICATION_UPDATE_START else NOTIFICATION_UPDATE_STOP)
                context.startService(intent2)
            }
            // Update pref
            val pref=MainActivity.context().getSharedPreferences("pref",Activity.MODE_PRIVATE)
            val editor=pref.edit()
            editor.putBoolean("isRunning", SettingManager.isRunning);
            editor.commit()
        }

        views.setOnClickPendingIntent(R.id.bttn_on, getPendingIntent(context, R.id.bttn_on))
        views.setOnClickPendingIntent(R.id.bttn_off, getPendingIntent(context, R.id.bttn_off))

        // 위젯 화면 갱신
        val appWidgetManager = AppWidgetManager.getInstance(context)
        val cpName = ComponentName(context, NewAppWidget::class.java)
        appWidgetManager.updateAppWidget(cpName, views)
    }

    // 호출한 객체에 PendingIntent를 부여
    private fun getPendingIntent(context: Context, id: Int): PendingIntent? {
        val intent = Intent(context, NewAppWidget::class.java)
        intent.action = PENDING_ACTION
        intent.putExtra("viewId", id)

        return PendingIntent.getBroadcast(context, id, intent, 0)
    }

}

