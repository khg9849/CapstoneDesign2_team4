package com.example.kakaotalktospeech

import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationManagerCompat
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    private var temp = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        btnOption.setOnClickListener { startActivity(Intent(this, OptionActivity::class.java)) }
        btnUsefulfeatures.setOnClickListener { startActivity(Intent(this, UsefulActivity::class.java)) }

        //권한 확인
        if (!this.isNotificationPermissionAllowed())
            startActivity(Intent("android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS"));

        //스위치 변화
        tbResultItemCount.setOnCheckedChangeListener{ _, isChecked ->
            if(isChecked)
                tbResultItemCount.text = "\t\t사용 중"
            else
                tbResultItemCount.text = "\t\t사용 안 함"

            this.onoffControl(isChecked)
            //Log.d("myTag",temp.toString())
        }
    }

    private fun onoffControl(_isChecked: Boolean){
        Toast.makeText(this, "onoffControl : $_isChecked", Toast.LENGTH_SHORT).show()

        GlobalValueManagement.setChecked(_isChecked)
    }

    private fun isNotificationPermissionAllowed(): Boolean {
        return NotificationManagerCompat.getEnabledListenerPackages(applicationContext)
            .any { enabledPackageName ->
                enabledPackageName == packageName
            }
    }



}