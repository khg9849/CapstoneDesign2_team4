package com.example.kakaotalktospeech

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import android.widget.SeekBar.OnSeekBarChangeListener
import androidx.appcompat.app.AppCompatActivity

class OptionActivity : AppCompatActivity() {

    val keys=arrayOf<String>("isRunning","ttsVolume","ttsSpeed","ttsEngine","isReadingSender","isReadingText","isReadingTime")
    lateinit var soundSeekbar : SeekBar
    lateinit var speedSeekbar : SeekBar
    lateinit var speedTextView : TextView
    lateinit var ttsEngineSpinner : Spinner
    lateinit var senderSwitch : Switch
    lateinit var textSwitch : Switch
    lateinit var timeSwitch : Switch

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_options)
        var mainIntent = Intent(this, MainActivity::class.java)
        val backBtn = findViewById<Button>(R.id.btnBacktomain)
        backBtn.setOnClickListener({
            mainIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            startActivity(mainIntent)
        })
        soundSeekbar = findViewById(R.id.seekSoundbar)
        speedSeekbar = findViewById(R.id.seekSpeedbar)
        speedTextView = findViewById(R.id.textSpeed)
        ttsEngineSpinner = findViewById(R.id.menuTTSengine)
        senderSwitch = findViewById(R.id.switchSender)
        textSwitch = findViewById(R.id.switchText)
        timeSwitch = findViewById(R.id.switchTime)

        setSeekbar()
        setSpinner()
        setSwitch()
    }

    private fun setSeekbar() {
        soundSeekbar?.setOnSeekBarChangeListener(object : OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {}
            override fun onStartTrackingTouch(seekBar: SeekBar) {}
            override fun onStopTrackingTouch(seekBar: SeekBar) {
                SettingManager.ttsVolume= (seekBar.progress.toFloat())/10
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
        val ttsItem = arrayOf("구글 TTS", "삼성 TTS")
        val ttsAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, ttsItem)
        ttsEngineSpinner.adapter = ttsAdapter
        ttsEngineSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                SettingManager.ttsEngine = p2
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
        soundSeekbar?.progress = (SettingManager.ttsVolume*10).toInt()
        speedSeekbar?.progress = (SettingManager.ttsSpeed/0.2).toInt()-3
        speedTextView?.text = String.format("%.1f", SettingManager.ttsSpeed)+"배속"
        ttsEngineSpinner.setSelection(SettingManager.ttsEngine)
        senderSwitch?.isChecked=SettingManager.isReadingSender
        textSwitch?.isChecked=SettingManager.isReadingText
        timeSwitch?.isChecked=SettingManager.isReadingTime
    }

    private fun saveState() {
        val pref = getSharedPreferences("pref", Activity.MODE_PRIVATE)
        val editor=pref.edit()

        editor.putFloat(keys[1], SettingManager.ttsVolume);
        editor.putFloat(keys[2], SettingManager.ttsSpeed);
        editor.putInt(keys[3], SettingManager.ttsEngine);
        editor.putBoolean(keys[4], SettingManager.isReadingSender);
        editor.putBoolean(keys[5], SettingManager.isReadingText);
        editor.putBoolean(keys[6], SettingManager.isReadingTime);
        editor.commit()
    }

    private fun restoreState() {

        val pref = getSharedPreferences("pref", Activity.MODE_PRIVATE)
        if(pref!=null){

            SettingManager.ttsVolume =pref.getFloat(keys[1], 1.0f);
            SettingManager.ttsSpeed =pref.getFloat(keys[2], 1.0f);
            SettingManager.ttsEngine =pref.getInt(keys[3], 0);
            SettingManager.isReadingSender =pref.getBoolean(keys[4], true);
            SettingManager.isReadingText  =pref.getBoolean(keys[5], true);
            SettingManager.isReadingTime  =pref.getBoolean(keys[6], false);
        }
    }

    override fun onResume() {
        Log.d("myTEST", "option - onResume")
        super.onResume()
        restoreState()
        initState()
    }

    override fun onPause() {
        Log.d("myTEST", "option - onPause")
        super.onPause()
        saveState()
    }
}
