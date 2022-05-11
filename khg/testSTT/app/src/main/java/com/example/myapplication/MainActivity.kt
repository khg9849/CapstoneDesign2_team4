package com.example.myapplication

import android.Manifest
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothHeadset
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.speech.RecognizerIntent
import android.speech.tts.TextToSpeech
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import kotlinx.coroutines.*
import java.util.*
import kotlin.concurrent.timer


class MainActivity : AppCompatActivity() {

    private var tts: TextToSpeech? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        requestPermission()

        /*val br : BroadcastReceiver = BroadcastReceiver()
        lateinit var filter : IntentFilter

        filter = IntentFilter().apply {
            addAction(Intent.ACTION_SCREEN_ON)
            addAction(BluetoothHeadset.ACTION_VENDOR_SPECIFIC_HEADSET_EVENT)
            addAction(BluetoothHeadset.ACTION_AUDIO_STATE_CHANGED)
            addAction(BluetoothHeadset.ACTION_CONNECTION_STATE_CHANGED)
            addAction(BluetoothDevice.ACTION_ACL_CONNECTED)
            addAction(BluetoothDevice.ACTION_ACL_DISCONNECTED)
        }

        registerReceiver(br, filter)

        var intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
        intent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, packageName)
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "ko-KR")*/

        val btn : Button = findViewById(R.id.buttonSTT)
        val ttsbtn : Button = findViewById(R.id.buttonTTS)
        var textView : TextView = findViewById(R.id.tvResult)
        var i : Int = 0

        var speed: Float = 1.0F
        var pitch: Float = 1.0F
        tts = TextToSpeech(this){ status ->
            if(status != TextToSpeech.ERROR){
                tts?.setLanguage(Locale.KOREAN)
                tts?.setPitch(speed)
                tts?.setSpeechRate(speed)
            }
        }

        btn.setOnClickListener{
            val sint = Intent(this, MyService::class.java)
            startService(sint)

        }
        ttsbtn.setOnClickListener({
            i++
            tts?.playSilentUtterance(500, TextToSpeech.QUEUE_ADD, null)
            tts?.speak("$i 번 누름", TextToSpeech.QUEUE_ADD, null, null)

        })

    }

    private fun requestPermission(){
        if(Build.VERSION.SDK_INT >= 23 && ContextCompat.checkSelfPermission(this,
                Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this,
                arrayOf(Manifest.permission.RECORD_AUDIO), 0)
        }
    }

    private fun sttService(){
        while(true){
            Handler().postDelayed({
                Toast.makeText(applicationContext, "실행중", Toast.LENGTH_SHORT).show()
            }, 5000)

        }
    }
}