package com.example.kakaotalktospeech

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.Switch
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationManagerCompat
import com.example.kakaotalktospeech.ActionManager.Companion.WIDGET_UPDATE
import com.example.kakaotalktospeech.ActionManager.Companion.NOTIFICATION_UPDATE_START
import com.example.kakaotalktospeech.ActionManager.Companion.NOTIFICATION_UPDATE_STOP

class MainActivity : AppCompatActivity() {
    companion object{
        var instance: MainActivity? = null
        fun context(): Context {
            return instance!!.applicationContext
        }
    }

    lateinit var runningSwitch : Switch
    lateinit var contactsManager : ContactsManager

    init{
        Log.d("myTEST", "init")
        instance = this
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        Log.d("myTEST", "onCreate")
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        serviceStart()

        checkNotificationAccess()
        initSwitch()
        initBtn()
    }

    fun serviceStart()
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
        runningSwitch.setOnCheckedChangeListener{ _, value ->
            SettingManager.isRunning = value
            Log.d("myTEST", "switchOn is ${SettingManager.isRunning}")
            runningSwitch.text= if(!SettingManager.isRunning) "사용 안 함" else "사용 중"

            // Update AppWidget
            val intent1 = Intent(this, NewAppWidget::class.java)
            intent1.setAction(WIDGET_UPDATE)
            sendBroadcast(intent1)
            // Update Notification Bar
            if(SettingManager.isNotificationServiceRunning){
                val intent2 = Intent(this, NotificationService::class.java)
                intent2.setAction(if(SettingManager.isRunning) NOTIFICATION_UPDATE_START else NOTIFICATION_UPDATE_STOP)
                startService(intent2)
            }

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
    override fun onStart(){
        Log.d("myTEST", "onStart")
        contactsManager = ContactsManager()
        contactsManager.makeContactsFile()
        contactsManager.exportContactsFile()
        super.onStart()
    }

    override fun onRestart() {
        Log.d("myTEST", "onRestart")
        super.onRestart()
        contactsManager.exportContactsFile()
    }

    override fun onResume() {
        Log.d("myTEST", "onResume")
        super.onResume()
        restoreState()
        contactsManager.importContactsFile()
    }

    override fun onPause() {
        Log.d("myTEST", "onPause")
        super.onPause()
        saveState()
        contactsManager.exportContactsFile()
    }

    override fun onDestroy() {
        Log.d("myTEST", "onDestroy")
        super.onDestroy()
    }
}