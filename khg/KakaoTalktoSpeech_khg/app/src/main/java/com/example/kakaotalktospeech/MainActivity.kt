package com.example.kakaotalktospeech

import android.content.ContentValues
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.speech.tts.TextToSpeech
import android.speech.tts.TextToSpeech.Engine.KEY_PARAM_VOLUME
import android.util.Log
import android.widget.Button
import android.widget.Switch
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationManagerCompat
import kotlinx.android.synthetic.main.activity_main.*
import org.w3c.dom.Text
import java.lang.Boolean.FALSE
import java.util.*




class MainActivity : AppCompatActivity() {
    private var tts: TextToSpeech? = null
    init{
        instance = this
    }
    companion object{
        private var instance:MainActivity? = null
        fun getInstance():MainActivity?{
            return instance
        }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //액티비티 전환
        val optionIntent = Intent(this, OptionActivity::class.java)
        val usefulIntent = Intent(this, UsefulActivity::class.java)
        btnOption.setOnClickListener({
            startActivity(optionIntent)
        })
        btnUsefulfeatures.setOnClickListener({
            startActivity(usefulIntent)
        })

        val checkMain: Boolean? = GlobalVariableManagement.getMainCheck()
        //on off 스위치
        var switchMain: Switch = findViewById(R.id.tbResultItemCount)


        if(checkMain == true) {
            switchMain.toggle()
            Log.d(
                ContentValues.TAG, "MainSwitch\n" +
                        "Switch: " + GlobalVariableManagement.getMainCheck()
            )
        }


        if(checkMain == true) {
            switchMain.setText("사용 중")

            Log.d(
                ContentValues.TAG, "MainSwitch\n" +
                        "Switch: " + GlobalVariableManagement.getMainCheck()
            )
        }
        else {
            switchMain.setText("사용하지 않음")
            Log.d(
                ContentValues.TAG, "MainSwitch\n" +
                        "Switch: " + GlobalVariableManagement.getMainCheck()
            )
        }
        switchMain.setOnCheckedChangeListener({_, isChecked->
            if(isChecked){
                switchMain.setText("사용 중")
                GlobalVariableManagement.setMainCheck(true)
            }
            else {
                switchMain.setText("사용하지 않음")
                GlobalVariableManagement.setMainCheck(false)
            }
            Log.d(
                ContentValues.TAG, "MainSwitch\n" +
                        "Switch " + GlobalVariableManagement.getMainCheck()
            )
        })
        //tts
        var speed: Float = GlobalVariableManagement.getSpeed()
        var pitch: Float = GlobalVariableManagement.getVolume()
        tts = TextToSpeech(this){ status ->
            if(status != TextToSpeech.ERROR){
                tts?.setLanguage(Locale.KOREAN)
                tts?.setPitch(speed)
                tts?.setSpeechRate(speed)
            }
        }
        GlobalVariableManagement.setTTS(tts)

        //<알림 접근 허용
        if(!isNotificationPermissionAllowed())
            startActivity(Intent("android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS"))
        //알림 접근 허용/>



    }
    //<뒤로가기 두 번 누르면 종료
    var lastTimeBackPressed : Long = 0
    override fun onBackPressed() {
        if(System.currentTimeMillis() - lastTimeBackPressed >= 1500){
            lastTimeBackPressed = System.currentTimeMillis()
            Toast.makeText(this, "'뒤로' 버튼을 한 번 더 누르시면 종료됩니다.", Toast.LENGTH_LONG).show()
        }
        else{
            ActivityCompat.finishAffinity(this)
            System.runFinalization()
            System.exit(0)
        }
    }
    //뒤로가기 두 번 누르면 종료/>

    //<알림 접근
    private fun isNotificationPermissionAllowed(): Boolean{
        return NotificationManagerCompat.getEnabledListenerPackages(applicationContext).any{
            enabledPackageName -> enabledPackageName == packageName
        }
    }
    //알림접근/>

    override fun onDestroy() {
        GlobalVariableManagement.getTTS()?.stop()
        GlobalVariableManagement.getTTS()?.shutdown()
        super.onDestroy()
    }


    fun settingTTS(){
        //tts
        var speed: Float = GlobalVariableManagement.getSpeed()
        tts?.setSpeechRate(speed)
        GlobalVariableManagement.setTTS(tts)
    }
}

