package com.noti.kakaotalktospeech

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent


import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationManagerCompat
import com.noti.kakaotalktospeech.ActionManager.Companion.sendUpdateWidgetIntent
import com.noti.kakaotalktospeech.ActionManager.Companion.updateNotibar
import androidx.core.content.ContextCompat
import com.example.kakaotalktospeech.R

class MainActivity : AppCompatActivity() {
    companion object{
        var instance: MainActivity? = null
        fun context(): Context {
            return instance!!.applicationContext
        }
    }

    lateinit private var runningSwitch : Switch
    lateinit private var runningText: TextView
    lateinit private var intersection : RelativeLayout
    lateinit private var icon: ImageView
    lateinit private var btnOption:Button
    lateinit private var btnUsefulfeatures: Button

    lateinit private var contactsManager : ContactsManager

    init{
        instance = this
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        requestPermission()
        serviceStart()
        checkNotificationAccess()

        runningSwitch = findViewById(R.id.switchRunning)
        runningText = findViewById(R.id.textRunning)
        intersection=findViewById(R.id.iconWrap)
        icon=findViewById(R.id.iconMain)
        setSwitch()

        btnOption = findViewById<Button>(R.id.btnOption)
        btnUsefulfeatures = findViewById<Button>(R.id.btnUsefulfeatures)
        setButton()
    }


    // 메시지 전송 권한 요청 및 설정창으로 이동
    private fun requestPermission(){
        if(Build.VERSION.SDK_INT >= 23 && ContextCompat.checkSelfPermission(this,
                Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this,
                arrayOf(Manifest.permission.RECORD_AUDIO), 0)
        }
    }

    private fun serviceStart()
    {
        val intent = Intent(this, KakaoNotificationListener::class.java)
        startService(intent)
    }

    // 알림 접근 권한 확인 및 설정창으로 이동
    private fun checkNotificationAccess() {
        val sets = NotificationManagerCompat.getEnabledListenerPackages(this)
        if (!sets.contains(packageName)){
            startActivity(Intent("android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS"));
        }
    }

    // isRunning 상태에 따라 UI 설정
    private fun setSwitchUI(){
        if(SettingManager.isRunning){
            runningText.text="사용 중"
            intersection.setBackgroundResource(R.drawable.intersection_darkblue)
            icon.setImageResource(R.drawable.icon2_yellow_500)

        }
        else{
            runningText.text="사용 안 함"
            intersection.setBackgroundResource(R.drawable.intersection_lightgray)
            icon.setImageResource(R.drawable.icon2_darkgray_500)
        }
    }

    private fun setSwitch(){
        runningSwitch.setOnCheckedChangeListener{ _, value ->
            SettingManager.isRunning = value
            setSwitchUI()
            sendUpdateWidgetIntent(this)
            updateNotibar(this)
        }
    }

    // 상단바를 내렸다 올렸을 때 스위치 UI 설정
    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        if(hasFocus){
            runningSwitch.isChecked = SettingManager.isRunning
            setSwitchUI()
        }
    }

    private fun setButton(){
        btnOption.setOnClickListener{
            val intent = Intent(this, OptionActivity::class.java)
            startActivity(intent)
        }
        btnUsefulfeatures.setOnClickListener{
            val intent = Intent(this, UsefulActivity::class.java)
            startActivity(intent)
        }
    }


    // 뒤로가기 버튼 두 번 누르면 종료
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




    private fun saveState() {
        val pref = getSharedPreferences("pref", Activity.MODE_PRIVATE)
        val editor=pref.edit()
        editor.putBoolean("isRunning", SettingManager.isRunning);
        editor.commit()
    }

    private fun restoreState() {
        val pref = getSharedPreferences("pref", Activity.MODE_PRIVATE)
        if(pref!=null){
            SettingManager.isRunning =pref.getBoolean("isRunning", false);
            runningSwitch.isChecked = SettingManager.isRunning
        }
    }


    override fun onStart(){
        contactsManager = ContactsManager()
        contactsManager.makeContactsFile()
        contactsManager.exportContactsFile()
        super.onStart()
    }

    override fun onRestart() {
        super.onRestart()
        contactsManager.exportContactsFile()
    }

    override fun onResume() {
        super.onResume()
        restoreState()
        contactsManager.importContactsFile()
    }

    override fun onPause() {
        super.onPause()
        saveState()
        contactsManager.exportContactsFile()
    }


}