package com.example.kakaotalktospeech

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.*
import android.widget.SeekBar.OnSeekBarChangeListener
import androidx.appcompat.app.AppCompatActivity

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
        var mainIntent = Intent(this, MainActivity::class.java)
        val backBtn = findViewById<Button>(R.id.btnBacktomain)
        backBtn.setOnClickListener({
            mainIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            startActivity(mainIntent)
        })
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
                SettingManager.ttsEngine = p2
            }
            override fun onNothingSelected(p0: AdapterView<*>?) {
                TODO("Not yet implemented")
            }
        }


    }

    private fun setSeekbar() {
        soundSeekbar?.progress = (SettingManager.ttsVolume*10).toInt()
        speedSeekbar?.progress = (SettingManager.ttsSpeed/0.2).toInt()-3
        speedTextView?.text = String.format("%.1f", SettingManager.ttsSpeed)+"배속"
        // 현재 tts 가져오기

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

    private fun setSwitch(){
        switchSender?.isChecked=SettingManager.isReadingSender
        switchSendedtext?.isChecked=SettingManager.isReadingText
        switchSendedtime?.isChecked=SettingManager.isReadingTime

        switchSender?.setOnCheckedChangeListener{CompoundButton, switchOn ->
            SettingManager.isReadingSender=switchOn
        }
        switchSendedtext?.setOnCheckedChangeListener{CompoundButton, switchOn ->
            SettingManager.isReadingText=switchOn
        }
        switchSendedtime?.setOnCheckedChangeListener{CompoundButton, switchOn ->
            SettingManager.isReadingTime=switchOn
        }
    }
}
