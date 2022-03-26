package com.example.kakaotalktospeech

import android.app.Activity
import android.content.*
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.util.Log
import android.widget.CompoundButton
import android.widget.Switch
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationManagerCompat
import java.util.*


class MainActivity : AppCompatActivity(), TextToSpeech.OnInitListener {

    var switch:Switch?=null
    var switchOn=false
    var receiver: BroadcastReceiver ?= null

    private var TTS: TextToSpeech? = null
    private var text:String?=null
    private var locale: Locale = Locale.KOREAN
    private var pitch: Float =1.0.toFloat()
    private var speed: Float =1.0.toFloat()

    override fun onCreate(savedInstanceState: Bundle?) {
        Log.d("myTEST", "onCreate")

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        switch=findViewById<Switch>(R.id.tbResultItemCount)
        switch?.setOnCheckedChangeListener{CompoundButton, switchOn ->
            this.switchOn=switchOn
        }

        initReceiver()
        checkNotificationAccess()
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
        val filter = IntentFilter()
        filter.addAction("com.broadcast.notification")
        registerReceiver(receiver, filter)
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

    fun readNotification(intent: Intent) {
        Log.d("myTEST", "readNotification")
        val sender=intent.getStringExtra("sender")
        val message=intent.getStringExtra("message")
        text=sender+"님으로부터 메시지가 도착했습니다. "+message
        speech()
    }

    fun speech(){
        TTS=TextToSpeech(applicationContext,this)
        TTS!!.setPitch(pitch)
        TTS!!.setSpeechRate(speed)
        TTS!!.setLanguage(locale)
        Toast.makeText(this,text, Toast.LENGTH_SHORT).show()
    }

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            Log.d("myTEST", "onInit - Success")
            TTS?.speak(text, TextToSpeech.QUEUE_FLUSH, null)
        } else {
            Log.d("myTEST", "onInit - Failed")
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
        if (TTS != null) {
            TTS!!.stop();
            TTS!!.shutdown();
        }
        super.onDestroy()
    }
}