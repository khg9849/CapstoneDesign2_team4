package com.example.kakaotalktospeech

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.core.app.NotificationManagerCompat

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        checkNotificationAccess()
    }

    fun checkNotificationAccess() {
        val sets = NotificationManagerCompat.getEnabledListenerPackages(this)
        if (sets.contains(packageName)){
            Log.d("myTEST", "Has Notification Access")
        }
        else{
            Log.d("myTEST", "Doesn't have Notification Access")
            startActivity(Intent("android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS"));
        }
    }

    override fun onNewIntent(intent: Intent?) {
        if(intent!=null){
            val sender=intent.getStringExtra("sender")
            val message=intent.getStringExtra("message")
            Toast.makeText(this,"message from $sender\n$message", Toast.LENGTH_SHORT).show()
        }
        super.onNewIntent(intent)
    }

}