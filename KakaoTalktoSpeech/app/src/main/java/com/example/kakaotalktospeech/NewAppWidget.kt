package com.example.kakaotalktospeech

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.util.Log
import android.widget.RemoteViews
import com.example.kakaotalktospeech.ActionManager.Companion.WIDGET_UPDATE
import com.example.kakaotalktospeech.ActionManager.Companion.WIDGET_CLICKED
import com.example.kakaotalktospeech.ActionManager.Companion.updateNotification
import com.example.kakaotalktospeech.ActionManager.Companion.updatePreferences


class NewAppWidget : AppWidgetProvider() {

    private fun getPendingIntent(context: Context, id: Int): PendingIntent? {
        val intent = Intent(context, NewAppWidget::class.java)
        intent.action = WIDGET_CLICKED
        intent.putExtra("viewId", id)

        return PendingIntent.getBroadcast(context, id, intent, PendingIntent.FLAG_IMMUTABLE)
    }
    private fun updateWidget(context: Context, views: RemoteViews) {
        if(SettingManager.isRunning){
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

        // 위젯 화면 갱신
        val appWidgetManager = AppWidgetManager.getInstance(context)
        val cpName = ComponentName(context, NewAppWidget::class.java)
        appWidgetManager.updateAppWidget(cpName, views)

        Log.d("myTEST","SettingManager.isRunning: " + SettingManager.isRunning)
    }

    override fun onReceive(context: Context, intent: Intent) {
        super.onReceive(context, intent)

        // 수신한 인텐트로부터 액션값을 읽음
        val action = intent.action
        Log.d("myTEST","action: " + action)

        // RemoteViews 인스턴트 생성
        val views = RemoteViews(context.packageName, R.layout.new_app_widget)

        when(action){
            WIDGET_UPDATE->updateWidget(context,views)
            WIDGET_CLICKED-> {
                val viewId = intent.getIntExtra("viewId", 0)
                SettingManager.isRunning = (viewId == R.id.bttn_on)

                updateWidget(context,views)
                updateNotification(context)
                updatePreferences()
            }
        }
    }
}

