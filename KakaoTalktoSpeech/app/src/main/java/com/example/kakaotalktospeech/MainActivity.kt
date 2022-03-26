package com.example.kakaotalktospeech

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.util.Log
import android.widget.Switch
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationManagerCompat

class MainActivity : AppCompatActivity() {

    var switch:Switch?=null
    var switchOn=false
    var TTS_Module:TTS_Module?=null

    var receiver: BroadcastReceiver ?= null

    override fun onCreate(savedInstanceState: Bundle?) {

        Log.d("myTEST", "onCreate")
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        checkNotificationAccess()

        switch=findViewById<Switch>(R.id.tbResultItemCount)
        Log.d("myTEST", "switch is $switch.isChecked()")

        initiateReceiver()
        initiateTTS()

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


    fun initiateReceiver(){
        receiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent) {
                if (intent!=null&& switchOn) {
                    readNotification(intent)
                }
            }
        }
        val filter = IntentFilter()
        filter.addAction("com.broadcast.notification")
        registerReceiver(receiver, filter)
    }

    fun initiateTTS(){
        TTS_Module=TTS_Module(this)
    }

    fun readNotification(intent: Intent) {
        Log.d("myTEST", "readNotification")
        val sender=intent.getStringExtra("sender")
        val message=intent.getStringExtra("message")
        Toast.makeText(this,"message from $sender\n$message", Toast.LENGTH_SHORT).show()
        TTS_Module?.toSpeech(sender+"님으로부터 메시지가 도착했습니다. "+message)
    }

    override fun onStart(){
        Log.d("myTEST", "onStart")
        super.onStart()
    }

    override fun onRestart() {
        Log.d("myTEST", "onRestart")
        super.onRestart()
    }
    override fun onResume() {
        Log.d("myTEST", "onResume")
        super.onResume()
    }

    override fun onPause() {
        Log.d("myTEST", "onPause")
        super.onPause()
    }

    override fun onDestroy() {
        Log.d("myTEST", "onDestroy")
        TTS_Module!!.destroy()
        super.onDestroy()
    }




}