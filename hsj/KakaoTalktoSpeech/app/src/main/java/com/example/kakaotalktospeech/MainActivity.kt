package com.example.kakaotalktospeech

import android.app.NotificationManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.widget.Button
import android.widget.Switch
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationManagerCompat

class MainActivity : AppCompatActivity() {
    companion object {
        var isUse : Boolean = false
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if(!isNotificationPermission()){
            startActivity(Intent(Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS))
        }

        val tbSwitch = findViewById<Switch>(R.id.tbResultItemCount)
        tbSwitch.setOnCheckedChangeListener{CompoundButton, onSwitch ->
            isUse = onSwitch
            if(onSwitch){
                tbSwitch.text = "사용 중"
            }
            else{
                tbSwitch.text = "사용 안 함"
            }
        }

        val optionButton = findViewById<Button>(R.id.btnOption)
        optionButton.setOnClickListener{
            val intent = Intent(this, OptionActivity::class.java)
            startActivity(intent)
        }

        val usefulButton = findViewById<Button>(R.id.btnUsefulfeatures)
        usefulButton.setOnClickListener{
            val intent = Intent(this, UsefulActivity::class.java)
            startActivity(intent)
        }
    }

    private fun isNotificationPermission():Boolean {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1) {
            val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            return notificationManager.isNotificationListenerAccessGranted(ComponentName(application, NotificationListener::class.java))
        }
        else {
            return NotificationManagerCompat.getEnabledListenerPackages(applicationContext)
                .contains(applicationContext.packageName)
        }
    }
}