package com.example.kakaotalktospeech

import android.app.Notification
import android.icu.text.SimpleDateFormat
import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import android.text.TextUtils
import android.widget.Toast


class NotificationListener : NotificationListenerService() {

    override fun onNotificationPosted(sbn: StatusBarNotification) {
        //Toast.makeText(this, "onNotificationPosted : $GlobalValueManagement.getChecked()", Toast.LENGTH_SHORT).show()

        if(GlobalValueManagement.getChecked()){
            val packageName = sbn.packageName
            if (!TextUtils.isEmpty(packageName) && packageName == "com.kakao.talk") {
                // 알람 확인
                //Toast.makeText(this, "테스트1", Toast.LENGTH_SHORT).show()
                val notification: Notification = sbn.notification
                val extras = sbn.notification.extras
                val title = extras.getString(Notification.EXTRA_TITLE)
                val text = extras.getCharSequence(Notification.EXTRA_TEXT)
                val subText = extras.getCharSequence(Notification.EXTRA_SUB_TEXT)
                var dateformat: SimpleDateFormat = SimpleDateFormat("yyyy년MM월dd일 hh시mm분")
                val extraTime: String = dateformat.format(sbn.postTime)

                Toast.makeText(this, "$title  $text  $subText\n" +
                                                 "$extraTime  $extras\n", Toast.LENGTH_SHORT).show()

                //Log.d("TAG", "onNotificationPosted: 이름 $title")
                //Log.d("TAG", "onNotificationPosted: 메시지 :$text")
                //Log.d("TAG", "onNotificationPosted: 채팅방 이름:$subText")
                //Log.d("TAG", "onNotificationPosted: 알림 시각:$extraTime")
            }
        }
    }

    override fun onNotificationRemoved(sbn: StatusBarNotification) {
        //Toast.makeText(this, "onNotificationRemoved : $GlobalValueManagement.getChecked()", Toast.LENGTH_SHORT).show()
        if(GlobalValueManagement.getChecked()){

            // 알람 미확인
            //Toast.makeText(this, "테스트2", Toast.LENGTH_SHORT).show()
        }
    }
}