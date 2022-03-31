package com.example.kakaotalktospeech

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.Switch
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationManagerCompat

class MainActivity : AppCompatActivity() {

    lateinit var switch : Switch

    override fun onCreate(savedInstanceState: Bundle?) {
        Log.d("myTEST", "onCreate")
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        checkNotificationAccess()

        initSwitch()
        initBtn()
    }

    private fun initSwitch(){
        switch = findViewById(R.id.tbResultItemCount)
        switch.setOnCheckedChangeListener{_, value ->
            SettingManager.switchOn = value
            Log.d("myTEST", "switchOn is ${SettingManager.switchOn}")
            switch.text= if(!SettingManager.switchOn) "사용 안 함" else "사용 중"
        }
    }

    private fun initBtn(){
        val btnOption = findViewById<Button>(R.id.btnOption)
        btnOption.setOnClickListener{
            val intent = Intent(this, OptionActivity::class.java)
            startActivity(intent)
        }

        val btnUsefulfeatures = findViewById<Button>(R.id.btnUsefulfeatures)
        btnUsefulfeatures.setOnClickListener{
            val intent = Intent(this, UsefulActivity::class.java)
            startActivity(intent)
        }
    }

    private fun checkNotificationAccess() {
        val sets = NotificationManagerCompat.getEnabledListenerPackages(this)
        if (sets.contains(packageName)){
            Log.d("myTEST", "Has Notification Access")
        }
        else{
            Log.d("myTEST", "Doesn't have Notification Access")
            startActivity(Intent("android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS"));
        }
    }

    private fun saveState() {
        val pref = getSharedPreferences("pref", Activity.MODE_PRIVATE)
        val editor=pref.edit()
        editor.putString("switchOn", ""+SettingManager.switchOn);
        editor.commit()
}

    private fun restoreState() {
        val pref = getSharedPreferences("pref", Activity.MODE_PRIVATE)
        if(pref!=null){
            //Log.d("myTEST", "before restore -> $switchOn, $isRegistered")
            val res=pref.getString("switchOn","false");
            SettingManager.switchOn = if (res=="true") true else false
            switch.isChecked = SettingManager.switchOn
            //Log.d("myTEST", "after restore -> $switchOn, $isRegistered")
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
        restoreState()
    }

    override fun onPause() {
        Log.d("myTEST", "onPause")
        super.onPause()
        saveState()
    }

    override fun onDestroy() {
        Log.d("myTEST", "onDestroy")
        super.onDestroy()
    }
}