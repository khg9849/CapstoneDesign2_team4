package com.example.kakaotalktospeech

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.SeekBar
import android.widget.Switch

class UsefulActivity : AppCompatActivity() {

    lateinit var notification_switch : Switch

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_usefulfeatures)

        var mainIntent = Intent(this, MainActivity::class.java)
        val backBtn = findViewById<Button>(R.id.btnBacktomain)
        backBtn.setOnClickListener({
            mainIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            startActivity(mainIntent)
        })

        val intent = Intent(this, NotificationService::class.java)

        notification_switch = findViewById<Switch>(R.id.notification_switch)
        notification_switch.setOnCheckedChangeListener{ CompoundButton, value ->
            if(value){
                SettingManager.isNotificationServiceRunning=true
                startService(intent)
            }
            else{
                SettingManager.isNotificationServiceRunning=false
                stopService(intent)
            }
        }

    }

    private fun saveState() {
        val pref = getSharedPreferences("pref", Activity.MODE_PRIVATE)
        val editor=pref.edit()

        editor.putBoolean("isNotificationServiceRunning", SettingManager.isNotificationServiceRunning);
        editor.commit()
    }

    private fun restoreState() {
        val pref = getSharedPreferences("pref", Activity.MODE_PRIVATE)
        if(pref!=null){
            SettingManager.isNotificationServiceRunning =pref.getBoolean("isNotificationServiceRunning", false);
        }
    }
    private fun initState(){
        notification_switch.isChecked = SettingManager.isNotificationServiceRunning
    }
    override fun onResume() {
        Log.d("myTEST", "useful - onResume")
        super.onResume()
        restoreState()
        initState()
    }

    override fun onPause() {
        Log.d("myTEST", "useful - onPause")
        super.onPause()
        saveState()
    }
}