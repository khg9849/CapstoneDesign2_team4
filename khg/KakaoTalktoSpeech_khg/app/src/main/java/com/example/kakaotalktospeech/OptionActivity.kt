package com.example.kakaotalktospeech

import android.content.ContentValues
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.SeekBar
import android.widget.Switch
import android.widget.TextView
import kotlinx.android.synthetic.main.activity_options.*

class OptionActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_options)

        var mainIntent = Intent(this, MainActivity::class.java)

        btnBacktomain.setOnClickListener({
            mainIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            startActivity(mainIntent)
        })

        // </set switch
        var switchSender: Switch = findViewById(R.id.switchSender)
        var switchSendedtext: Switch = findViewById(R.id.switchSendedtext)
        var switchSendedtime: Switch = findViewById(R.id.switchSendedtime)

        if(GlobalVariableManagement.getSenderCheck() == true)
            switchSender.toggle()
        if(GlobalVariableManagement.getSendedtextCheck() == true)
            switchSendedtext.toggle()
        if(GlobalVariableManagement.getSendedtimeCheck() == true)
            switchSendedtime.toggle()



        switchSender.setOnCheckedChangeListener({_, isChecked->
            if(isChecked)
                GlobalVariableManagement.setSenderCheck(true)
            else
                GlobalVariableManagement.setSenderCheck(false)
            Log.d(
                ContentValues.TAG, "MainSwitch\n" +
                        "Switch on/off" + GlobalVariableManagement.getSenderCheck()
            )
        })

        switchSendedtext.setOnCheckedChangeListener({_, isChecked->
            if(isChecked)
                GlobalVariableManagement.setSendedtextCheck(true)
            else
                GlobalVariableManagement.setSendedtextCheck(false)
            Log.d(
                ContentValues.TAG, "MainSwitch\n" +
                        "Switch on/off" + GlobalVariableManagement.getSendedtextCheck()
            )
        })
        switchSendedtime.setOnCheckedChangeListener({_, isChecked->
            if(isChecked)
                GlobalVariableManagement.setSendedtimeCheck(true)
            else
                GlobalVariableManagement.setSendedtimeCheck(false)
            Log.d(
                ContentValues.TAG, "MainSwitch\n" +
                        "Switch on/off" + GlobalVariableManagement.getSendedtimeCheck()
            )
        })

        // set switch />

        //</ set speed and volume with seekbar
        var textSpeed: TextView = findViewById(R.id.textSpeed)
        var seekSpeed: SeekBar = findViewById(R.id.seekSpeedbar)
        var seekVolume: SeekBar = findViewById(R.id.seekSoundbar)


        seekSpeed.setOnSeekBarChangeListener(object:SeekBar.OnSeekBarChangeListener{
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                val valueSpeed: Float = (progress.toFloat() * 0.25f) + 0.5f
                GlobalVariableManagement.setSpeed(valueSpeed)
                MainActivity.getInstance()?.settingTTS()
                textSpeed.setText("$valueSpeed 배속")
            }

            override fun onStartTrackingTouch(p0: SeekBar?) {
            }

            override fun onStopTrackingTouch(p0: SeekBar?) {
            }
        })


        seekVolume.setOnSeekBarChangeListener(object:SeekBar.OnSeekBarChangeListener{
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                val valueVolume: Float = (progress.toFloat() * 0.2f)
                GlobalVariableManagement.setVolume(valueVolume)
                //MainActivity.getInstance()?.settingTTS()
                Log.d(
                    ContentValues.TAG, "Current Volume: " +
                            "$valueVolume"
                )
            }

            override fun onStartTrackingTouch(p0: SeekBar?) {
            }

            override fun onStopTrackingTouch(p0: SeekBar?) {
            }
        })

        // />
    }
}
