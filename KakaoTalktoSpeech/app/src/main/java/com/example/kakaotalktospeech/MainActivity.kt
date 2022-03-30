package com.example.kakaotalktospeech

import android.app.Activity
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.media.AudioManager
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.Switch
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationManagerCompat
import java.text.SimpleDateFormat
import java.util.*


class MainActivity : AppCompatActivity() {

    companion object{
        var switchOn=false
        /* TTS option */
        var volume=0.5.toFloat()
        var speed=1.0.toFloat()
        var pitch=1.0.toFloat()
        /* TTS text option */
        var readSender=true
        var readText=true
        var readTime=false
    }

    var ttsModule:TTS_Module?=null
    var switch:Switch?=null
    var receiver: BroadcastReceiver ?= null
    var isRegistered=false

    override fun onCreate(savedInstanceState: Bundle?) {
        Log.d("myTEST", "onCreate")
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        checkNotificationAccess()

        initTTS()
        initReceiver()
        initSwitch()
        initBttn()
    }

    fun initTTS(){
        ttsModule= TTS_Module(applicationContext,getSystemService(AUDIO_SERVICE) as AudioManager)
    }

    fun initReceiver(){
        receiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent) {
                val sender=if(readSender) intent.getStringExtra("sender")+"님으로부터 " else ""
                val message=if(readText) intent.getStringExtra("message") else ""
                val time=if(readTime) getTime()+", " else ""
                val text=time+sender+"메시지가 도착했습니다."+message
                ttsModule!!.speech(text)
            }
        }
    }

    fun initSwitch(){
        switch=findViewById<Switch>(R.id.tbResultItemCount)
        switch?.setOnCheckedChangeListener{_, value ->
            switchOn=value
            Log.d("myTEST", "switchOn is "+ switchOn)
            if(!switchOn){
                unregisterReceiver(receiver) //not working (how to get registered receiver?)
                isRegistered=false
                switch?.text="사용 안 함"
            }
            else if(!isRegistered){
                val filter = IntentFilter()
                filter.addAction("com.broadcast.notification")
                registerReceiver(receiver, filter)
                isRegistered=true
                switch?.text="사용 중"
            }
        }
    }

    fun initBttn(){
        val btnOption=findViewById<Button>(R.id.btnOption)
        btnOption.setOnClickListener({
            val intent = Intent(this, OptionActivity::class.java)
            startActivity(intent)
        })

        val btnUsefulfeatures=findViewById<Button>(R.id.btnUsefulfeatures)
        btnUsefulfeatures.setOnClickListener({
            val intent = Intent(this, UsefulActivity::class.java)
            startActivity(intent)
        })
    }

    fun getTime():String{
        val now=System.currentTimeMillis()
        val date= Date(now)
        val dateFormat= SimpleDateFormat("hh시 mm분")
        val time=dateFormat.format(date)
        return time
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
        editor.putString("switchOn",switchOn.toString());
        editor.putString("isRegistered",isRegistered.toString())
        editor.commit()
}

    private fun restoreState() {
        val pref=getSharedPreferences("pref", Activity.MODE_PRIVATE)
        if(pref!=null){
            //Log.d("myTEST", "before restore -> $switchOn, $isRegistered")

            val res=pref.getString("switchOn","false");
            switchOn = if (res=="true") true else false
            switch?.isChecked=switchOn

            val res2=pref.getString("isRegistered","");
            isRegistered=if(res2=="true") true else false

            //Log.d("myTEST", "after restore -> $switchOn, $isRegistered")
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