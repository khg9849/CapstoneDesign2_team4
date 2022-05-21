package com.example.kakaotalktospeech

import android.app.Activity
import android.view.View
import android.widget.*
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.media.AudioManager
import android.media.AudioManager.STREAM_MUSIC
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.IBinder
import android.util.Log
import android.widget.Button
import android.widget.Switch
import java.util.*
import android.widget.Toast


class UsefulActivity : AppCompatActivity() {
    lateinit var notification_switch : Switch
    lateinit var whitelistSpinner : Spinner
    lateinit var ttsStopBtn : Button
    lateinit var ttsShutdownBtn : Button
    lateinit var ttsPauseBtn : Button
    lateinit var ttsRestartBtn : Button
    lateinit var ttsQSwitch : Switch

    lateinit var sendmsg_button : Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_usefulfeatures)

        var mainIntent = Intent(this, MainActivity::class.java)
        val backBtn = findViewById<Button>(R.id.btnBacktomain)
        backBtn.setOnClickListener({
            mainIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            startActivity(mainIntent)
        })

        val intent = Intent(this, NotificationService::class.java)
        val sintent = Intent(this, SpeechToTextService::class.java)
        notification_switch = findViewById<Switch>(R.id.notification_switch)
        notification_switch.isChecked = SettingManager.isNotificationServiceRunning
        notification_switch.setOnCheckedChangeListener{ CompoundButton, value ->
            SettingManager.isNotificationServiceRunning=value
            if(value){
                startService(intent)
            }
            else{
                stopService(intent)
            }
        }

        ttsStopBtn = findViewById(R.id.stopBtn)
        ttsShutdownBtn = findViewById(R.id.shutdownBtn)
        ttsPauseBtn = findViewById(R.id.pauseBtn)
        ttsRestartBtn = findViewById(R.id.restartBtn)
        ttsQSwitch = findViewById(R.id.ttsQSwitch)

        setButton()
        setSwitch()

        sendmsg_button = findViewById(R.id.btnStt)
        val stt = SpeechToText(mainIntent , applicationContext)
        sendmsg_button.setOnClickListener{
            Toast.makeText(this,"button", Toast.LENGTH_SHORT).show()
//            stt.startSttToSend()
            stt.startSttToControlOption()
        }

        whitelistSpinner = findViewById<Spinner>(R.id.white_list_spinner)
        setwhitelistSpinner()
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

    private fun setwhitelistSpinner(){
        var _item : ArrayList<String> = ArrayList<String>()
        for((key, value) in SettingManager.whiteList)
            _item.add(key)
        val _adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, _item)
        whitelistSpinner.adapter = _adapter
        whitelistSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {

            }
            override fun onNothingSelected(p0: AdapterView<*>?) {
                TODO("Not yet implemented")
            }
        }
    }


    private fun saveState() {
        val pref = getSharedPreferences("pref", Activity.MODE_PRIVATE)
        val editor=pref.edit()

        val keys=arrayOf<String>("isRunning","ttsVolume","ttsSpeed","ttsEngine","isReadingSender","isReadingText","isReadingTime")
        editor.putInt(keys[1], SettingManager.ttsVolume)
        editor.putFloat(keys[2], SettingManager.ttsSpeed)
        editor.putInt(keys[3], SettingManager.ttsEngine)
        editor.putBoolean(keys[4], SettingManager.isReadingSender)
        editor.putBoolean(keys[5], SettingManager.isReadingText)
        editor.putBoolean(keys[6], SettingManager.isReadingTime)

        editor.putBoolean("isNotificationServiceRunning", SettingManager.isNotificationServiceRunning);
        editor.putBoolean("ttsQueueDelete", SettingManager.ttsQueueDelete)
        editor.commit()
    }

    private fun restoreState() {
        val pref = getSharedPreferences("pref", Activity.MODE_PRIVATE)
        if(pref!=null){
            SettingManager.isNotificationServiceRunning =pref.getBoolean("isNotificationServiceRunning", false)
            SettingManager.ttsQueueDelete = pref.getBoolean("ttsQueueDelete", true)
        }
    }

    private fun initState(){
        notification_switch.isChecked = SettingManager.isNotificationServiceRunning
        ttsQSwitch?.isChecked=SettingManager.ttsQueueDelete
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
        Log.d("myTEST", "useful - onResume")
        super.onResume()
        setwhitelistSpinner()
        serviceBind()
        restoreState()
        initState()
    }

    override fun onPause() {
        Log.d("myTEST", "useful - onPause")
        super.onPause()
        saveState()
        serviceUnBind()
    }
}