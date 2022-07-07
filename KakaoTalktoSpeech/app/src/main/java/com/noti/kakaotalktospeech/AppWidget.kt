package com.noti.kakaotalktospeech

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.widget.RemoteViews
import com.example.kakaotalktospeech.R
import com.noti.kakaotalktospeech.ActionManager.Companion.WIDGET_CREATE
import com.noti.kakaotalktospeech.ActionManager.Companion.WIDGET_CLICKED
import com.noti.kakaotalktospeech.ActionManager.Companion.updateIsRunning
import com.noti.kakaotalktospeech.ActionManager.Companion.updateNotibar
import com.noti.kakaotalktospeech.ActionManager.Companion.updatePreferences


class AppWidget : AppWidgetProvider() {

    // 위젯 클릭시 브로드캐스트
    private fun getPendingIntent(context: Context, id: Int): PendingIntent? {
        val intent = Intent(context, AppWidget::class.java)
        intent.action = WIDGET_CLICKED
        intent.putExtra("viewId", id)
        return PendingIntent.getBroadcast(context, id, intent, PendingIntent.FLAG_IMMUTABLE)
    }

    override fun onReceive(context: Context, intent: Intent) {
        super.onReceive(context, intent)

        val action = intent.action
        val views = RemoteViews(context.packageName, R.layout.app_widget)

        when(action){
            // 위젯 생성시
            WIDGET_CREATE->updateWidget(context,views, SettingManager.isRunning)
            // 위젯 클릭시 설정값 변경
            WIDGET_CLICKED-> {
                val viewId = intent.getIntExtra("viewId", 0)
                val isRunning=(viewId == R.id.bttn_on)
                updateIsRunning(isRunning)
                updateWidget(context,views,isRunning)
                // 노티바 및 pref 동기화
                updateNotibar(context)
                updatePreferences()
            }
        }
    }

    //  위젯 UI 변경
    private fun updateWidget(context: Context, views: RemoteViews,isRunning:Boolean) {
        if(isRunning){
            views.setInt(R.id.bttn_on, "setTextColor", Color.BLACK);
            views.setInt(R.id.bttn_on, "setBackgroundColor", Color.WHITE);
            views.setInt(R.id.bttn_off, "setTextColor", Color.WHITE);
            views.setInt(R.id.bttn_off, "setBackgroundColor", Color.BLACK);
        }
        else{
            views.setInt(R.id.bttn_on, "setTextColor", Color.WHITE);
            views.setInt(R.id.bttn_on, "setBackgroundColor", Color.BLACK);
            views.setInt(R.id.bttn_off, "setTextColor", Color.BLACK);
            views.setInt(R.id.bttn_off, "setBackgroundColor", Color.WHITE);
        }
        views.setOnClickPendingIntent(R.id.bttn_on, getPendingIntent(context, R.id.bttn_on))
        views.setOnClickPendingIntent(R.id.bttn_off, getPendingIntent(context, R.id.bttn_off))

        val appWidgetManager = AppWidgetManager.getInstance(context)
        val cpName = ComponentName(context, AppWidget::class.java)
        appWidgetManager.updateAppWidget(cpName, views)
    }

}

