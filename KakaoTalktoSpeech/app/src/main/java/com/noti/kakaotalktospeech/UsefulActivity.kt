package com.noti.kakaotalktospeech

import android.app.Activity
import android.app.AlertDialog
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.os.IBinder
import android.text.method.ScrollingMovementMethod
import android.util.Log
import android.view.LayoutInflater
import android.widget.Button
import android.widget.FrameLayout
import android.widget.Switch
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.kakaotalktospeech.R
import com.noti.kakaotalktospeech.ActionManager.Companion.NOTIBAR_CREATE


class UsefulActivity : AppCompatActivity() {

    private lateinit var notibar_switch : Switch
    private lateinit var ttsStopBtn : Button
    private lateinit var ttsShutdownBtn : Button
    private lateinit var ttsPauseBtn : Button
    private lateinit var ttsRestartBtn : Button
    private lateinit var ttsQSwitch : Switch
    private lateinit var whitelistBttnWrap:FrameLayout
    private lateinit var sttSwitch : Switch
    private lateinit var btnNotiHelpWrap:FrameLayout

    private var myService: KakaoNotificationListener? = null
    private var isConService = false
    private val serviceConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            val b = service as KakaoNotificationListener.MyServiceBinder
            myService = b.getService()
            isConService = true
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            isConService = false
        }
    }

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

        /* 알림바 설정 */
        notibar_switch = findViewById<Switch>(R.id.switchNotibar)
        setNotibarSwitch()

        /* TTS 재생 설정 */
        ttsStopBtn = findViewById(R.id.stopBtn)
        ttsShutdownBtn = findViewById(R.id.shutdownBtn)
        ttsPauseBtn = findViewById(R.id.pauseBtn)
        ttsRestartBtn = findViewById(R.id.restartBtn)
        ttsQSwitch = findViewById(R.id.switchTTSQ)
        setTTSElements()

        /* 리스트 설정 */
        whitelistBttnWrap=findViewById<FrameLayout>(R.id.btnListWrap)
        setWhiteList()

        /* 노티 어시스턴트 설정 */
        sttSwitch = findViewById(R.id.switchNotiAssistant)
        btnNotiHelpWrap=findViewById<FrameLayout>(R.id.btnNotiHelpWrap)
        setNotiAssistant()

    }

    private fun setNotibarSwitch(){
        val intent = Intent(this, NotibarService::class.java)
        notibar_switch.isChecked = SettingManager.isNotibarRunning
        notibar_switch.setOnCheckedChangeListener{ CompoundButton, value ->
            SettingManager.isNotibarRunning =value
            if(value){
                intent.action=NOTIBAR_CREATE
                intent.putExtra("isRunning", SettingManager.isRunning)
                startService(intent)
            }
            else{
                stopService(intent)
            }
        }
    }

    private fun setTTSElements(){
        /* 버튼 설정 */
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
        /* 스위치 설정 */
        ttsQSwitch?.setOnCheckedChangeListener { CompoundButton, value ->
            SettingManager.ttsQueueDelete = value
        }
    }

    private fun setWhiteList(){
        val listIntent = Intent(this, ListActivity::class.java)
        whitelistBttnWrap.setOnClickListener(){
            listIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            startActivity(listIntent)
        }
    }

    private fun setNotiAssistant(){
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

        val helpIntent=Intent(this, ListActivity::class.java)
        btnNotiHelpWrap.setOnClickListener(){
            val view=LayoutInflater.from(this).inflate(R.layout.activity_dialog,null)
            val builder=AlertDialog.Builder(this).setView(view)
            val alertDialog=builder.show()
            alertDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

            val tv=view.findViewById<TextView>(R.id.helpText)
            tv.movementMethod=ScrollingMovementMethod()
            val btnShutdown=view.findViewById<Button>(R.id.btnShutdown)
            btnShutdown.setOnClickListener{
                alertDialog.dismiss()
            }
        }
    }
    fun stopTTSforSTT(){
        myService?.pauseTTS()
    }
    fun restartTTSforSTT(){
        myService?.restartTTS()
    }
    fun recentsenderForStt() : String? {
        return myService?.recentsender()
    }
    fun replyForStt(message : String){
        myService?.reply(message)
    }

    fun saveState() {
        val pref = getSharedPreferences("pref", Activity.MODE_PRIVATE)
        val editor=pref.edit()

        editor.putInt("ttsVolume", SettingManager.ttsVolume)
        editor.putFloat("ttsSpeed", SettingManager.ttsSpeed)
        editor.putInt("ttsEngine", SettingManager.ttsEngine)
        editor.putBoolean("isReadingSender", SettingManager.isReadingSender)
        editor.putBoolean("isReadingText", SettingManager.isReadingText)
        editor.putBoolean("isReadingTime", SettingManager.isReadingTime)
        editor.putBoolean("isSttActivate", SettingManager.isSttActivate)
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
        ttsQSwitch?.isChecked= SettingManager.ttsQueueDelete
        sttSwitch.isChecked = SettingManager.isSttActivate
    }

    private fun serviceBind()
    {
        val intent = Intent(this, KakaoNotificationListener::class.java)
        bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE)
    }

    private fun serviceUnBind()
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