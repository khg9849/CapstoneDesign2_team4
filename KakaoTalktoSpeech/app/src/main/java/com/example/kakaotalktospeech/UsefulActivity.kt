package com.example.kakaotalktospeech

import android.app.Activity
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
import android.util.Log
import android.widget.Button
import android.widget.Switch
import androidx.appcompat.app.AppCompatActivity
import com.example.kakaotalktospeech.ActionManager.Companion.NOTIBAR_CREATE


class UsefulActivity : AppCompatActivity() {

    lateinit var notibar_switch : Switch
    lateinit var ttsStopBtn : Button
    lateinit var ttsShutdownBtn : Button
    lateinit var ttsPauseBtn : Button
    lateinit var ttsRestartBtn : Button
    lateinit var ttsQSwitch : Switch
    lateinit var whitelistButton : Button
    lateinit var sttSwitch : Switch

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_usefulfeatures)

        SettingManager.usefulActivityInstance = this

        var mainIntent = Intent(this, MainActivity::class.java)
        val backBtn = findViewById<Button>(R.id.btnBacktomain)
        backBtn.setOnClickListener({
            mainIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            startActivity(mainIntent)
        })

        val intent = Intent(this, NotibarService::class.java)
        notibar_switch = findViewById<Switch>(R.id.notibar_switch)
        notibar_switch.isChecked = SettingManager.isNotibarRunning
        notibar_switch.setOnCheckedChangeListener{ CompoundButton, value ->
            SettingManager.isNotibarRunning=value
            if(value){
                intent.action=NOTIBAR_CREATE
                intent.putExtra("isRunning",SettingManager.isRunning)
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

        sttSwitch = findViewById(R.id.switchStt)
        val sttintent = Intent(this, SpeechToTextService::class.java)
        sttSwitch.setOnCheckedChangeListener{ CompoundButton, value ->
            if(value){
                SettingManager.isSttActivate = true
                startService(sttintent)
            }
            else{
                SettingManager.isSttActivate = false
                stopService(sttintent)
            }
        }

        val listIntent = Intent(this, ListActivity::class.java)
        whitelistButton = findViewById<Button>(R.id.btnList)
        whitelistButton.setOnClickListener {
            listIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            startActivity(listIntent)
        }
        //setwhitelistSpinner()
    }

    private fun setButton() {
        ttsStopBtn.setOnClickListener {
            myService?.stopTTS()
            //myTestFunc()
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

    /*
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
    */


    private fun saveState() {
        val pref = getSharedPreferences("pref", Activity.MODE_PRIVATE)
        val editor=pref.edit()

        val keys=arrayOf<String>("isRunning","ttsVolume","ttsSpeed","ttsEngine","isReadingSender","isReadingText","isReadingTime", "isSttActivate")
        editor.putInt(keys[1], SettingManager.ttsVolume)
        editor.putFloat(keys[2], SettingManager.ttsSpeed)
        editor.putInt(keys[3], SettingManager.ttsEngine)
        editor.putBoolean(keys[4], SettingManager.isReadingSender)
        editor.putBoolean(keys[5], SettingManager.isReadingText)
        editor.putBoolean(keys[6], SettingManager.isReadingTime)
        editor.putBoolean(keys[7], SettingManager.isSttActivate)
        editor.putBoolean("isNotificationServiceRunning", SettingManager.isNotibarRunning);
        editor.putBoolean("ttsQueueDelete", SettingManager.ttsQueueDelete)
        editor.commit()
    }

    private fun restoreState() {
        val pref = getSharedPreferences("pref", Activity.MODE_PRIVATE)
        if(pref!=null){
            SettingManager.isNotibarRunning =pref.getBoolean("isNotificationServiceRunning", false)
            SettingManager.ttsQueueDelete = pref.getBoolean("ttsQueueDelete", true)
            SettingManager.isSttActivate = pref.getBoolean("isSttActivate", false)
        }
    }

    private fun initState(){
        notibar_switch.isChecked = SettingManager.isNotibarRunning
        ttsQSwitch?.isChecked=SettingManager.ttsQueueDelete
        sttSwitch.isChecked = SettingManager.isSttActivate
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

    fun recentsenderForStt() : String? {
        return myService?.recentsender()
    }

    fun replyForStt(message : String){
        myService?.reply(message)
    }

    override fun onResume() {
        Log.d("myTEST", "useful - onResume")
        super.onResume()
        //setwhitelistSpinner()
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

    fun stopTTSforSTT(){
        Log.d("myTEST", "stopTTS22")
        myService?.pauseTTS()
    }
    fun restartTTSforSTT(){
        myService?.restartTTS()
    }

}