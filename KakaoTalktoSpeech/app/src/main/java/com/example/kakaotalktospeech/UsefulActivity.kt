package com.example.kakaotalktospeech

import android.app.Activity
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.IBinder
import android.util.Log
import android.widget.Button
import android.widget.SeekBar
import android.widget.Switch
import java.util.*
import kotlin.concurrent.schedule

class UsefulActivity : AppCompatActivity() {
    lateinit var ttsStopBtn : Button
    lateinit var ttsShutdownBtn : Button
    lateinit var ttsPauseBtn : Button
    lateinit var ttsRestartBtn : Button
    lateinit var ttsQSwitch : Switch

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_usefulfeatures)

        var mainIntent = Intent(this, MainActivity::class.java)
        val backBtn = findViewById<Button>(R.id.btnBacktomain)
        backBtn.setOnClickListener({
            mainIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            startActivity(mainIntent)
        })

        ttsStopBtn = findViewById(R.id.stopBtn)
        ttsShutdownBtn = findViewById(R.id.shutdownBtn)
        ttsPauseBtn = findViewById(R.id.pauseBtn)
        ttsRestartBtn = findViewById(R.id.restartBtn)
        ttsQSwitch = findViewById(R.id.ttsQSwitch)

        setButton()
        setSwitch()
    }

    private fun setButton() {
        ttsStopBtn.setOnClickListener {
            myService?.stopTTS()
        }
        ttsShutdownBtn.setOnClickListener {
            myService?.shutdownTTS()
        }
        ttsPauseBtn.setOnClickListener {
            myService?.pauseTTS()
        }
        ttsRestartBtn.setOnClickListener {
            myService?.restartTTS()
        }
    }

    private fun setSwitch() {
        ttsQSwitch?.setOnCheckedChangeListener { CompoundButton, value ->
            SettingManager.ttsQueueDelete = value
        }
    }

    private fun initState(){
        ttsQSwitch?.isChecked=SettingManager.ttsQueueDelete
    }

    private fun saveState() {
        val pref = getSharedPreferences("pref", Activity.MODE_PRIVATE)
        val editor=pref.edit()

        editor.putBoolean("ttsQueueDelete", SettingManager.ttsQueueDelete)
        editor.commit()
    }

    private fun restoreState() {
        val pref = getSharedPreferences("pref", Activity.MODE_PRIVATE)
        if(pref!=null){
            SettingManager.ttsQueueDelete = pref.getBoolean("ttsQueueDelete", true)
        }
    }

    var myService:KakaoNotificationListener? = null
    var isConService = false
    val serviceConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            Log.v("myTEST", "binder 생성")
            val b = service as KakaoNotificationListener.MyServiceBinder
            myService = b.getService()
            isConService = true
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            isConService = false
        }
    }

    fun serviceBind()
    {
        val intent = Intent(this, KakaoNotificationListener::class.java)
        bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE)
    }

    fun serviceUnBind()
    {
        if (isConService) {
            unbindService(serviceConnection)
            isConService = false
        }
    }

    override fun onResume() {
        super.onResume()
        serviceBind()
        restoreState()
        initState()
    }

    override fun onPause() {
        super.onPause()
        saveState()
        serviceUnBind()
    }
}