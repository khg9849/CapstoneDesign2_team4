package com.example.kakaotalktospeech

import android.app.Activity
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
import android.util.Log
import android.view.View
import android.widget.*
import android.widget.SeekBar.OnSeekBarChangeListener
import androidx.appcompat.app.AppCompatActivity

class OptionActivity : AppCompatActivity() {

    private lateinit var soundSeekbar : SeekBar
    private lateinit var speedSeekbar : SeekBar
    private lateinit var speedTextView : TextView
    private lateinit var ttsEngineSpinner : Spinner
    private lateinit var senderSwitch : Switch
    private lateinit var textSwitch : Switch
    private lateinit var timeSwitch : Switch
    private var ttsItem : ArrayList<String> = arrayListOf<String>()
    private var myService:KakaoNotificationListener? = null
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
        setContentView(R.layout.activity_options)

        var mainIntent = Intent(this, MainActivity::class.java)
        val backBtn = findViewById<Button>(R.id.btnBacktomain)
        backBtn.setOnClickListener({
            mainIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            startActivity(mainIntent)
        })

        soundSeekbar = findViewById(R.id.seekbarSound)
        speedSeekbar = findViewById(R.id.seekbarSpeed)
        speedTextView = findViewById(R.id.speed)
        setSeekbar()

        ttsEngineSpinner = findViewById(R.id.spinnerEngine)
        setSpinner()

        senderSwitch = findViewById(R.id.switchSender)
        textSwitch = findViewById(R.id.switchText)
        timeSwitch = findViewById(R.id.switchTime)
        setSwitch()
    }

    private fun setSeekbar() {
        soundSeekbar?.setOnSeekBarChangeListener(object : OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {}
            override fun onStartTrackingTouch(seekBar: SeekBar) {}
            override fun onStopTrackingTouch(seekBar: SeekBar) {
                SettingManager.ttsVolume= seekBar.progress
            }
        })

        speedSeekbar?.setOnSeekBarChangeListener(object : OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                speedTextView?.text= String.format("%.1f", (progress+3)*0.2)+"배속"
            }
            override fun onStartTrackingTouch(seekBar: SeekBar) {}
            override fun onStopTrackingTouch(seekBar: SeekBar) {
                SettingManager.ttsSpeed=(seekBar.progress+3)*0.2f
            }
        })
    }

    private fun setSpinner(){
        val ttsAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, ttsItem)
        ttsEngineSpinner.adapter = ttsAdapter
        ttsEngineSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                if(SettingManager.ttsEngine != p2) {
                    SettingManager.ttsEngine = p2
                    myService?.changeTTS()
                }
            }
            override fun onNothingSelected(p0: AdapterView<*>?) {
                TODO("Not yet implemented")
            }
        }
    }

    private fun setSwitch(){
        senderSwitch?.setOnCheckedChangeListener{ CompoundButton, value ->
            SettingManager.isReadingSender=value
        }
        textSwitch?.setOnCheckedChangeListener{ CompoundButton, value ->
            SettingManager.isReadingText=value
        }
        timeSwitch?.setOnCheckedChangeListener{ CompoundButton, value ->
            SettingManager.isReadingTime=value
        }
    }

    private fun initState(){
        var ttsList = SettingManager.ttsEngineList
        if(ttsList!=null){
            if(ttsItem!!.size != ttsList.size) {
                ttsItem.clear()
                for (i in ttsList) {
                    when(i.name){
                        "com.samsung.SMT" -> ttsItem.add("삼성 TTS")
                        "com.google.android.tts" -> ttsItem.add("구글 TTS")
                        else -> ttsItem.add(i.name)
                    }
                }
            }
        }
        val ttsAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, ttsItem)
        ttsEngineSpinner.adapter = ttsAdapter
        soundSeekbar?.progress = SettingManager.ttsVolume
        speedSeekbar?.progress = (SettingManager.ttsSpeed/0.2).toInt()-3
        speedTextView?.text = String.format("%.1f", SettingManager.ttsSpeed)+"배속"
        ttsEngineSpinner.setSelection(SettingManager.ttsEngine)
        /*
        Timer().schedule(5000){
            myService?.changeTTS()
        }*/ // 저장된 tts로 생성되게 만들어야됨
        senderSwitch?.isChecked=SettingManager.isReadingSender
        textSwitch?.isChecked=SettingManager.isReadingText
        timeSwitch?.isChecked=SettingManager.isReadingTime
    }

    private fun saveState() {
        val pref = getSharedPreferences("pref", Activity.MODE_PRIVATE)
        val editor=pref.edit()

        editor.putInt("ttsVolume", SettingManager.ttsVolume)
        editor.putFloat("ttsSpeed", SettingManager.ttsSpeed)
        editor.putInt("ttsEngine", SettingManager.ttsEngine)
        editor.putBoolean("isReadingSender", SettingManager.isReadingSender)
        editor.putBoolean("isReadingText", SettingManager.isReadingText)
        editor.putBoolean("isReadingTime", SettingManager.isReadingTime)
        editor.commit()
    }

    private fun restoreState() {
        val pref = getSharedPreferences("pref", Activity.MODE_PRIVATE)
        if(pref!=null){
            SettingManager.ttsVolume =pref.getInt("ttsVolume", 5)
            SettingManager.ttsSpeed =pref.getFloat("ttsSpeed", 1.0f)
            SettingManager.ttsEngine =pref.getInt("ttsEngine", SettingManager.ttsEngine)
            SettingManager.isReadingSender =pref.getBoolean("isReadingSender", true)
            SettingManager.isReadingText  =pref.getBoolean("isReadingText", true)
            SettingManager.isReadingTime  =pref.getBoolean("isReadingTime", false)
        }
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