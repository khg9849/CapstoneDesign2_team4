package com.example.kakaotalktospeech

import android.app.Activity
import android.content.*
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.Switch
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationManagerCompat
import java.util.*


class MainActivity : AppCompatActivity() {

    var switch:Switch?=null
    var switchOn=false
    var receiver: BroadcastReceiver ?= null
    var ttsModule:TTS_Module?=null

    override fun onCreate(savedInstanceState: Bundle?) {
        Log.d("myTEST", "onCreate")

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initTTS()
        initReceiver()
        checkNotificationAccess()

        switch=findViewById<Switch>(R.id.tbResultItemCount)
        switch?.setOnCheckedChangeListener{CompoundButton, switchOn ->

            this.switchOn=switchOn
            if(switchOn){
                val filter = IntentFilter()
                filter.addAction("com.broadcast.notification")
                registerReceiver(receiver, filter)
            }
            else{
                unregisterReceiver(receiver)
            }
        }

        val btnOption=findViewById<Button>(R.id.btnOption)
        btnOption.setOnClickListener({
            val intent = Intent(this, OptionActivity::class.java)
            startActivity(intent)
        })


    }

    fun initTTS(){
        this.ttsModule= TTS_Module(applicationContext)
    }

    fun initReceiver(){
        receiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent) {
                Log.d("myTEST", "onReceive - SwitchOn is "+switchOn)
                if (intent!=null && switchOn) {
                    readNotification(intent)
                }
            }
        }
    }

    fun readNotification(intent: Intent) {
        Log.d("myTEST", "readNotification")
        val sender=intent.getStringExtra("sender")
        val message=intent.getStringExtra("message")
        val text=sender+"님으로부터 메시지가 도착했습니다. "+message;
        ttsModule!!.speech(text)
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

    private fun saveState() {
        val pref=getSharedPreferences("pref", Activity.MODE_PRIVATE)
        val editor=pref.edit()
        editor.putString("switch",switchOn.toString());
        editor.commit()
    }
    private fun restoreState() {
        val pref=getSharedPreferences("pref", Activity.MODE_PRIVATE)
        if((pref!=null)&&(pref.contains("switch"))){
            val res=pref.getString("switch","");
            if(res=="true"){
                switch?.isChecked=true
            }
            else{
                switch?.isChecked=false
            }

        }

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
        restoreState()
    }



    override fun onPause() {
        Log.d("myTEST", "onPause")
        super.onPause()
        saveState()
    }



    override fun onDestroy() {
        Log.d("myTEST", "onDestroy")
        ttsModule?.destroy()
        super.onDestroy()
    }
}