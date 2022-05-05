package com.example.kakaotalktospeech

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity

class UsefulActivity : AppCompatActivity() {

    lateinit var notification_switch : Switch
    lateinit var whitelistSpinner : Spinner

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


        whitelistSpinner = findViewById<Spinner>(R.id.white_list_spinner)
        setwhitelistSpinner()
    }


    private fun setwhitelistSpinner(){
        var _item : ArrayList<String> = ArrayList<String>()
        for((key, value) in SettingManager.whiteList)
            _item.add(key)
        val _adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, _item)
        whitelistSpinner.adapter = _adapter
        whitelistSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {

            }
            override fun onNothingSelected(p0: AdapterView<*>?) {
                TODO("Not yet implemented")
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
        setwhitelistSpinner()
        restoreState()
        initState()
    }

    override fun onPause() {
        Log.d("myTEST", "useful - onPause")
        super.onPause()
        saveState()
    }
}