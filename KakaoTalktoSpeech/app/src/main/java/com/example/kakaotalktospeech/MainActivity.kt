package com.example.kakaotalktospeech

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent


import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationManagerCompat
import com.example.kakaotalktospeech.ActionManager.Companion.sendUpdateWidgetIntent
import com.example.kakaotalktospeech.ActionManager.Companion.updateNotibar
import androidx.core.content.ContextCompat

class MainActivity : AppCompatActivity() {
    companion object{
        var instance: MainActivity? = null
        fun context(): Context {
            return instance!!.applicationContext
        }
    }

    lateinit var runningSwitch : Switch
    lateinit var runningText: TextView
    lateinit var intersection : RelativeLayout
    lateinit var icon: ImageView
    lateinit var contactsManager : ContactsManager

    init{
        Log.d("myTEST", "init")
        instance = this
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        Log.d("myTEST", "onCreate")
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        requestPermission()
        serviceStart()
        checkNotificationAccess()
        initSwitch()
        initBtn()
    }

    private fun serviceStart()
    {
        val intent = Intent(this, KakaoNotificationListener::class.java)
        startService(intent)
    }

    //<뒤로가기 두 번 누르면 종료
    var lastTimeBackPressed : Long = 0
    override fun onBackPressed() {
        if(System.currentTimeMillis() - lastTimeBackPressed >= 1500){
            lastTimeBackPressed = System.currentTimeMillis()
            Toast.makeText(this, "'뒤로' 버튼을 한 번 더 누르시면 종료됩니다.", Toast.LENGTH_LONG).show()
        }
        else{
            super.onBackPressed()
        }
    }
    //뒤로가기 두 번 누르면 종료/>

    private fun initSwitch(){
        runningSwitch = findViewById(R.id.tbResultItemCount)
        runningText = findViewById(R.id.runningText)
        intersection=findViewById(R.id.intersection)
        icon=findViewById(R.id.icon)
        runningSwitch.setOnCheckedChangeListener{ _, value ->
            SettingManager.isRunning = value
            if(SettingManager.isRunning){
                runningText.text="사용 중"
                intersection.setBackgroundResource(R.drawable.intersection_darkblue)
                icon.setImageResource(R.drawable.icon2_yellow)

            }
            else{
                runningText.text="사용 안 함"
                intersection.setBackgroundResource(R.drawable.intersection_lightgray)
                icon.setImageResource(R.drawable.icon2_darkgray)
            }


            sendUpdateWidgetIntent(this)
            updateNotibar(this)
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

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
//        Log.d("myTEST", "MainActivity-onWindowFocusChanged: "+hasFocus)
        if(hasFocus){
            runningSwitch.isChecked = SettingManager.isRunning
            if(SettingManager.isRunning){
                runningText.text="사용 중"
                intersection.setBackgroundResource(R.drawable.intersection_darkblue)
                icon.setImageResource(R.drawable.icon2_yellow)

            }
            else{
                runningText.text="사용 안 함"
                intersection.setBackgroundResource(R.drawable.intersection_lightgray)
                icon.setImageResource(R.drawable.icon2_darkgray)
            }
        }

    }

    private fun saveState() {
        val pref = getSharedPreferences("pref", Activity.MODE_PRIVATE)
        val editor=pref.edit()
        editor.putBoolean("isRunning", SettingManager.isRunning);
        editor.commit()
    }

    private fun restoreState() {
        val pref = getSharedPreferences("pref", Activity.MODE_PRIVATE)
        if(pref!=null){
            SettingManager.isRunning=pref.getBoolean("isRunning", false);
            runningSwitch.isChecked = SettingManager.isRunning
        }
    }

    private fun requestPermission(){
        if(Build.VERSION.SDK_INT >= 23 && ContextCompat.checkSelfPermission(this,
                Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this,
                arrayOf(Manifest.permission.RECORD_AUDIO), 0)
        }

        //메시지 전송 권한 요청
        val MY_PERMISSION_ACCESS_ALL = 100
        if(ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
            || ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)
        {
            var permissions = arrayOf(
                android.Manifest.permission.SEND_SMS
            )
            ActivityCompat.requestPermissions(this, permissions, MY_PERMISSION_ACCESS_ALL)
        }
    }

    override fun onStart(){
//        Log.d("myTEST", "MainActivity-onStart")
        contactsManager = ContactsManager()
        contactsManager.makeContactsFile()
        contactsManager.exportContactsFile()
        super.onStart()
    }

    override fun onRestart() {
//        Log.d("myTEST", "MainActivity-onRestart")
        super.onRestart()
        contactsManager.exportContactsFile()
    }

    override fun onResume() {
//        Log.d("myTEST", "MainActivity-onResume")
        super.onResume()
        restoreState()
        contactsManager.importContactsFile()
    }

    override fun onPause() {
//        Log.d("myTEST", "MainActivity-onPause")
        super.onPause()
        saveState()
        contactsManager.exportContactsFile()
    }

    override fun onDestroy() {
//        Log.d("myTEST", "MainActivity-onDestroy")
        super.onDestroy()
    }
}