package com.example.kakaotalktospeech

import android.media.AudioManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.SeekBar
import android.widget.SeekBar.OnSeekBarChangeListener
import android.widget.Switch
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity


class OptionActivity : AppCompatActivity() {

    var soundSeekbar:SeekBar?=null
    var speedSeekbar:SeekBar?=null
    var speedTextView:TextView?=null
    var switchSender:Switch?=null
    var switchSendedtext:Switch?=null
    var switchSendedtime:Switch?=null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_options)

        soundSeekbar=findViewById<SeekBar>(R.id.seekSoundbar)
        speedSeekbar=findViewById<SeekBar>(R.id.seekSpeedbar)
        speedTextView=findViewById<TextView>(R.id.textSpeed)

        switchSender=findViewById<Switch>(R.id.switchSender)
        switchSendedtext=findViewById<Switch>(R.id.switchSendedtext)
        switchSendedtime=findViewById<Switch>(R.id.switchSendedtime)

        setSeekbar()
        setSwitch()
    }

    private fun setSeekbar() {
        soundSeekbar?.progress= (MainActivity.volume*10).toInt()
        speedSeekbar?.progress= (MainActivity.speed/0.25).toInt()-1
        speedTextView?.text= MainActivity.speed.toString()+"배속"

        soundSeekbar?.setOnSeekBarChangeListener(object : OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {}
            override fun onStartTrackingTouch(seekBar: SeekBar) {}
            override fun onStopTrackingTouch(seekBar: SeekBar) {
                MainActivity.volume= (seekBar.progress.toFloat())/10
            }
        })

        speedSeekbar?.setOnSeekBarChangeListener(object : OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                speedTextView?.text= ((progress+1)*0.25).toString()+"배속"
            }
            override fun onStartTrackingTouch(seekBar: SeekBar) {}
            override fun onStopTrackingTouch(seekBar: SeekBar) {
                MainActivity.speed=(seekBar.progress+1)*0.25.toFloat()
            }
        })
    }


    fun setSwitch(){
        switchSender?.isChecked=MainActivity.readSender
        switchSendedtext?.isChecked=MainActivity.readText
        switchSendedtime?.isChecked=MainActivity.readTime

        switchSender?.setOnCheckedChangeListener{CompoundButton, switchOn ->
            MainActivity.readSender=switchOn
        }
        switchSendedtext?.setOnCheckedChangeListener{CompoundButton, switchOn ->
            MainActivity.readText=switchOn
        }
        switchSendedtime?.setOnCheckedChangeListener{CompoundButton, switchOn ->
            MainActivity.readTime=switchOn
        }
    }
}
