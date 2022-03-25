package com.example.kakaotalktospeech

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Switch
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationManagerCompat

class MainActivity : AppCompatActivity() {

    val TTS_Module=TTS_Module(this)
    var switchOn=false

    override fun onCreate(savedInstanceState: Bundle?) {
        Log.d("myTEST", "onCreate")
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        checkNotificationAccess()

        val switch=findViewById<Switch>(R.id.tbResultItemCount)
        switch.isChecked=switchOn
        Log.d("myTEST", "switchOn is $switchOn")

        switch.setOnCheckedChangeListener{CompoundButton, onSwitch ->
            if (onSwitch){
                switchOn=true
                Log.d("myTEST", "switchOn is $switchOn")
                Toast.makeText(this, "switch on", Toast.LENGTH_SHORT).show()
            }
            else{
                switchOn=false
                Toast.makeText(this, "switch off", Toast.LENGTH_SHORT).show()
            }
        }

        if (intent.hasExtra("INTENT_KEY") && switchOn) {
            readNotification(intent)
        }

    }

    override fun onStart(){
        Log.d("myTEST", "onStart")
        super.onStart()
    }

    override fun onRestart() {
        Log.d("myTEST", "onRestart")
        super.onRestart()
    }
    override fun onResume() {
        Log.d("myTEST", "onResume")
        super.onResume()
    }

    override fun onPause() {
        Log.d("myTEST", "onPause")
        super.onPause()
    }

    override fun onDestroy() {
        Log.d("myTEST", "onDestroy")
        TTS_Module!!.destroy()
        super.onDestroy()
    }

    fun checkNotificationAccess() {
        val sets = NotificationManagerCompat.getEnabledListenerPackages(this)
        if (sets.contains(packageName)){
            Log.d("myTEST", "Has Notification Access")
        }
        else{
            Log.d("myTEST", "Doesn't have Notification Access")
            startActivity(Intent("android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS"));
        }
    }

    fun readNotification(intent: Intent) {
        Log.d("myTEST", "readNotification")
        val sender=intent.getStringExtra("sender")
        val message=intent.getStringExtra("message")
        Toast.makeText(this,"message from $sender\n$message", Toast.LENGTH_SHORT).show()
        TTS_Module?.toSpeech(sender+"님으로부터 메시지가 도착했습니다. "+message)
    }

    override fun onNewIntent(intent: Intent?) {
        Log.d("myTEST", "onNewIntent")
        if (intent!=null && intent.hasExtra("INTENT_KEY")&&switchOn) {
            readNotification(intent)
        }
        super.onNewIntent(intent)
    }

}