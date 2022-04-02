package com.example.kakaotalktospeech

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import android.widget.SeekBar.OnSeekBarChangeListener
import androidx.appcompat.app.AppCompatActivity
import java.util.*

class OptionActivity : AppCompatActivity() {

    lateinit var soundSeekbar : SeekBar
    lateinit var speedSeekbar : SeekBar
    lateinit var speedTextView : TextView
    lateinit var menuTTSEngine : Spinner
    lateinit var switchSender : Switch
    lateinit var switchSendedtext : Switch
    lateinit var switchSendedtime : Switch

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_options)

        soundSeekbar = findViewById(R.id.seekSoundbar)
        speedSeekbar = findViewById(R.id.seekSpeedbar)
        speedTextView = findViewById(R.id.textSpeed)
        menuTTSEngine = findViewById(R.id.menuTTSengine)

        switchSender = findViewById(R.id.switchSender)
        switchSendedtext = findViewById(R.id.switchSendedtext)
        switchSendedtime = findViewById(R.id.switchSendedtime)

        setSeekbar()
        setSwitch()
        setSpinner()
    }
    private fun setSpinner(){
        val ttsItem = arrayOf("구글 TTS", "삼성 TTS")
        val ttsSpinner = findViewById<Spinner>(R.id.menuTTSengine)
        val ttsAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, ttsItem)
        ttsSpinner.adapter = ttsAdapter
        ttsSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                Log.d(
                    "spinner" ,"$p0 \n $p1 \n $p2 \n $p3"
                )
                when(p2){
                    0->{
                        SettingManager.engineTitle = "com.google.android.tts"
                    }
                    1-> {
                        SettingManager.engineTitle = "com.samsung.android.tts"
                    }
                }
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {
                TODO("Not yet implemented")
            }
        }


    }

    private fun setSeekbar() {
        soundSeekbar?.progress = (SettingManager.volume*10).toInt()
        speedSeekbar?.progress = (SettingManager.speed/0.2).toInt()-3
        speedTextView?.text = String.format("%.1f", SettingManager.speed)+"배속"
        // 현재 tts 가져오기

        soundSeekbar?.setOnSeekBarChangeListener(object : OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {}
            override fun onStartTrackingTouch(seekBar: SeekBar) {}
            override fun onStopTrackingTouch(seekBar: SeekBar) {
                SettingManager.volume= (seekBar.progress.toFloat())/10
            }
        })

        speedSeekbar?.setOnSeekBarChangeListener(object : OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                speedTextView?.text= String.format("%.1f", (progress+3)*0.2)+"배속"
            }
            override fun onStartTrackingTouch(seekBar: SeekBar) {}
            override fun onStopTrackingTouch(seekBar: SeekBar) {
                SettingManager.speed=(seekBar.progress+3)*0.2f
            }
        })
    }

    private fun setSwitch(){
        switchSender?.isChecked=SettingManager.readSender
        switchSendedtext?.isChecked=SettingManager.readText
        switchSendedtime?.isChecked=SettingManager.readTime

        switchSender?.setOnCheckedChangeListener{CompoundButton, switchOn ->
            SettingManager.readSender=switchOn
        }
        switchSendedtext?.setOnCheckedChangeListener{CompoundButton, switchOn ->
            SettingManager.readText=switchOn
        }
        switchSendedtime?.setOnCheckedChangeListener{CompoundButton, switchOn ->
            SettingManager.readTime=switchOn
        }
    }
}
