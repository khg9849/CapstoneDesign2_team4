package com.example.kakaotalktospeech

import android.os.Bundle
import android.widget.SeekBar
import android.widget.SeekBar.OnSeekBarChangeListener
import android.widget.Spinner
import android.widget.Switch
import android.widget.TextView
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
