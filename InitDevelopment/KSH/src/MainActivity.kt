package com.example.kakaotalktospeech

import android.content.Intent
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationManagerCompat
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*

class MainActivity : AppCompatActivity() {
    private var tts: TextToSpeech? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //UI/UX 이벤트 리스너
        btnOption.setOnClickListener { startActivity(Intent(this, OptionActivity::class.java)) }
        btnUsefulfeatures.setOnClickListener { startActivity(Intent(this, UsefulActivity::class.java)) }

        //권한 확인
        if (!this.isNotificationPermissionAllowed())
            startActivity(Intent("android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS"))

        //tts 초기화
        tts = TextToSpeech(this) { status ->
            if (status != TextToSpeech.ERROR) {
                tts?.setLanguage(Locale.KOREAN)
                tts?.setPitch(1.0f)
                tts?.setSpeechRate(1.0f)
            }
        }
        GlobalValueManagement.tts = tts

        //스위치 이벤트 리스너
        tbResultItemCount.setOnCheckedChangeListener{ _, isChecked ->
            if(isChecked) tbResultItemCount.text = "\t\t사용 중"
            else tbResultItemCount.text = "\t\t사용 안 함"

            this.onoffControl(isChecked)
        }
    }

    private fun onoffControl(_isChecked: Boolean){
        GlobalValueManagement.setApplicationUsing(_isChecked)
        GlobalValueManagement.setTTSUsing(_isChecked)
    }

    private fun isNotificationPermissionAllowed(): Boolean {
        return NotificationManagerCompat.getEnabledListenerPackages(applicationContext)
            .any { enabledPackageName ->
                enabledPackageName == packageName
            }
    }

    override fun onDestroy() {
        GlobalValueManagement.tts?.stop()
        GlobalValueManagement.tts?.shutdown()

        super.onDestroy()
    }


}