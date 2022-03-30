package com.example.kakaotalktospeech

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.content.Intent
import android.widget.*

class OptionActivity : AppCompatActivity() {
    companion object {
        var ttsSound : Float = 1.0f
        var ttsSpeed : Float = 1.0f
        // tts engine
        var isSender : Boolean = false
        var isText : Boolean = false
        var isTime : Boolean = false
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_options)

        val backButton = findViewById<Button>(R.id.btnBacktomain)
        backButton.setOnClickListener{
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }

        val soundBar = findViewById<SeekBar>(R.id.seekSoundbar)
        soundBar.setOnSeekBarChangeListener(object:SeekBar.OnSeekBarChangeListener{
            override fun onProgressChanged(p0: SeekBar?, p1: Int, p2: Boolean) {
                ttsSound = p1*0.2f
            }

            override fun onStartTrackingTouch(p0: SeekBar?) {

            }

            override fun onStopTrackingTouch(p0: SeekBar?) {

            }
        })

        val speedBar = findViewById<SeekBar>(R.id.seekSpeedbar)
        val textSpeed = findViewById<TextView>(R.id.textSpeed)
        speedBar.setOnSeekBarChangeListener(object:SeekBar.OnSeekBarChangeListener{
            override fun onProgressChanged(p0: SeekBar?, p1: Int, p2: Boolean) {
                ttsSpeed = 0.6f+p1*0.2f
                textSpeed.text = String.format("%.1f", ttsSpeed)+"배속"
            }

            override fun onStartTrackingTouch(p0: SeekBar?) {

            }

            override fun onStopTrackingTouch(p0: SeekBar?) {

            }
        })

        val ttsItem = arrayOf("삼성","구글")
        val ttsSpinner = findViewById<Spinner>(R.id.ttsSpinner)
        val ttsAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, ttsItem)
        ttsSpinner.adapter = ttsAdapter
        ttsSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                //when(p2){
                //    0->
                //    1->
                //}
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {

            }
        }

        val senderSwitch = findViewById<Switch>(R.id.switchSender)
        senderSwitch.setOnCheckedChangeListener{CompoundButton, onSwitch ->
            isSender = onSwitch
        }

        val textSwitch = findViewById<Switch>(R.id.switchSendedtext)
        textSwitch.setOnCheckedChangeListener{CompoundButton, onSwitch ->
            isText = onSwitch
        }

        val timeSwitch = findViewById<Switch>(R.id.switchSendedtime)
        timeSwitch.setOnCheckedChangeListener { CompoundButton, onSwitch ->
            isTime = onSwitch
        }
    }
}
