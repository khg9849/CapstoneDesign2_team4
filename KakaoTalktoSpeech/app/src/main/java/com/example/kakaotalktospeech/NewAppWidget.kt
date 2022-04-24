package com.example.kakaotalktospeech

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
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

        // AppWidget의 기본 Action 들
        Toast.makeText(context, "action: " + action, Toast.LENGTH_SHORT).show();

//        Toast.makeText(context, "isRunning: " + SettingManager.isRunning, Toast.LENGTH_SHORT).show();

        // AppWidget의 기본 Action 들
        if(action==APPWIDGET_UPDATE){
            if(SettingManager.isRunning===true){
                setON(views)
            }
            else{
                setOFF(views)
            }
        }
        else if (action == PENDING_ACTION) {

            //val viewId=String.valueOf(intent.getIntExtra("viewId", 0))
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



//            Toast.makeText(context, "Touched view " + viewId, Toast.LENGTH_SHORT).show();

//            views.setTextViewText(R.id.tv_on, action)
//            views.setTextViewText(
//                R.id.tv_on,
//                String.valueOf(intent.getIntExtra("viewId", 0))
//            )
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

        // 중요!!! getBroadcast를 이용할 때 동일한 Action명을 이용할 경우 서로 다른 request ID를 이용해야함
        // 아래와 같이 동일한 request ID를 주면 서로 다른 값을 putExtra()하더라도 제일 처음 값만 반환됨
        // return PendingIntent.getBroadcast(context, 0, intent, 0);
        return PendingIntent.getBroadcast(context, id, intent, 0)
    }

/*
    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {

        // There may be multiple widgets active, so update all of them
        for (appWidgetId in appWidgetIds) {

            // Create an Intent to launch ExampleActivity
            val intent = Intent(context, MainActivity::class.java)
            val pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);

            // Get the layout for the App Widget and attach an on-click listener
            // to the button
            val views = RemoteViews(context.packageName, R.layout.new_app_widget)
            views.setOnClickPendingIntent(R.id.bttn_off, pendingIntent);

            // Tell the AppWidgetManager to perform an update on the current app widget
            appWidgetManager.updateAppWidget(appWidgetId, views)
        }
    }

    override fun onEnabled(context: Context) {
        // Enter relevant functionality for when the first widget is created
    }

    override fun onDisabled(context: Context) {
        // Enter relevant functionality for when the last widget is disabled
    }

        override fun onReceive(context: Context?, intent: Intent?) {
        super.onReceive(context, intent)


    }

*/

//    fun onReceive(Context context, Intent intent) {
//        AppWidgetManager mgr = AppWidgetManager.getInstance(context);
//        if (intent.getAction().equals(TOAST_ACTION)) {
//            int appWidgetId = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID,
//            AppWidgetManager.INVALID_APPWIDGET_ID);
//            int viewIndex = intent.getIntExtra(EXTRA_ITEM, 0);
//            Toast.makeText(context, "Touched view " + viewIndex, Toast.LENGTH_SHORT).show();
//        }
//        super.onReceive(context, intent);
//    }

}

